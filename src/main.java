import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;



public class main {
	private static ArrayList<TrendTopic> activeTrends;
	private static TwitterStream twitterStream;
	private static DB db;
	private static int counter;

	public static void main(String args[]){
		counter = 0;
		//twitterStream.cleanUp();
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient();
			db = mongoClient.getDB( "MyDataBase" );//vash dedomenwn
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}//sundeomaste me to server

		
		db.dropDatabase(); // svhnoume gia arxikopoihsh
		
		db.createCollection("Trends", null);

		db.createCollection("Tweets", null);
		
		//System.out.println(db.getCollection("Tweets").count());
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setJSONStoreEnabled(true);
		cb.setOAuthAccessToken("2870879723-zQyxbqIe6Oq2D4N16WL8V2eqMPHOb6RI5NZduuq");
		cb.setOAuthAccessTokenSecret("Q29BLdtJT8actTp6s2yAFV8Mb58OTHvoIbJO4dt1VvoRr");
		cb.setOAuthConsumerKey("pyTD0ZTSYBza0pOpN2Sm1Guwe");
		cb.setOAuthConsumerSecret("24ZIqlPbAz90P0xjyV5eFircsTIe70uTWTPamYRV1cnlVlsFBZ");
		Configuration conf = cb.build();
		final Twitter twitter = new TwitterFactory(conf).getInstance();

		twitterStream = new TwitterStreamFactory(conf).getInstance();
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
				User user = status.getUser();
				
				// gets Username
				String username = status.getUser().getScreenName();
				String profileLocation = user.getLocation();
				long tweetId = status.getId();
				String content = status.getText();
				String rawJSON = TwitterObjectFactory.getRawJSON(status);//metatrepoume to tweet se JSON
				DBObject dbObject = (DBObject) JSON.parse(rawJSON);// Dhmiourgoume apo to JSON antikeimeno gia th bash
				db.getCollection("Tweets").insert(dbObject);// Eisagoume to antikeimeno sth vash

			}

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub

			}
		};
		twitterStream.addListener(listener);

		activeTrends = new ArrayList<>();

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				long started = new Date().getTime();//arxh gia elegxo gia 3 meres
				long now=new Date().getTime();
				// TODO Auto-generated method stub
				while (now - started <=  259200000) {

					Trends trends;
					Trend[] trendsArray;
					try {
						// get top ten trends globally
						trends = twitter.getPlaceTrends(1);
						trendsArray = trends.getTrends();
						updateActive(trendsArray);

						FilterQuery fq = new FilterQuery();
						String keywords[] = new String[activeTrends.size()];
						for (int i = 0; i < keywords.length; i++) {
							keywords[i] = activeTrends.get(i).getTitle();
						}

						fq.track(keywords);
						twitterStream.cleanUp();
						twitterStream.filter(fq);

					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("gamithike o dias");
					}
					try {
						Thread.sleep(1000);
						System.exit(0);
						Thread.sleep(300000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					now = new Date().getTime();//ananewnw ton elegxo ths wras
				}
				// Na valoume ola ta activeTrends na exoun endTime to twrino meta tis 3 wres
				
				for(TrendTopic t :activeTrends){
					t.setEndTime();
					t.saveToCollection(db.getCollection("Trends"));
				}
				activeTrends.clear();
				
			}
		});
		t.start();
	}
//	
//	private static void storeJSON(String rawJSON, String fileName)
//			throws IOException {
//		FileOutputStream fos = null;
//		OutputStreamWriter osw = null;
//		BufferedWriter bw = null;
//		try {
//			fos = new FileOutputStream(fileName,true);
//			osw = new OutputStreamWriter(fos, "UTF-8");
//			bw = new BufferedWriter(osw);
//			bw.write(rawJSON);
//			bw.write(System.getProperty("line.separator"));
//			bw.write(System.getProperty("line.separator"));
//			bw.flush();
//		} finally {
//			if (bw != null) {
//				try {
//					bw.close();
//				} catch (IOException ignore) {
//				}
//			}
//			if (osw != null) {
//				try {
//					osw.close();
//				} catch (IOException ignore) {
//				}
//			}
//			if (fos != null) {
//				try {
//					fos.close();
//				} catch (IOException ignore) {
//				}
//			}
//		}
//	}
	

	private static void updateActive(Trend[] trendsArray) {
		// insert new topics to active
		for (Trend t : trendsArray) {
			boolean found = false;

			for (TrendTopic topic : activeTrends) {
				if (topic.isSameTopic(t.getName())) {
					found = true;
					topic.setEndTime(null);
					continue;
				}
			}
			if (!found) {
				// if the new trend was not found , add it to active
				activeTrends.add(new TrendTopic(t.getName()));
			}
		}
		
		//tempcode
		DBCollection col = db.getCollection("Trends");
		activeTrends.get(0).saveToCollection(col);
		
		col.setObjectClass(BasicDBObject.class);
		BasicDBObject one = (BasicDBObject) col.findOne();
		System.out.println(one);
		System.out.println(new TrendTopic(one));
		
		// remove active topics tha expired
		ListIterator<TrendTopic> it = activeTrends.listIterator();
	 	while (it.hasNext()) {
		 	TrendTopic topic = (TrendTopic) it.next();
	
		 	for (Trend t : trendsArray) {
			 	if (topic.isSameTopic(t.getName()) && (topic.getEndTime() == null)) {
			 		topic.setEndTime();
			 	}
		 	}
	
		 	if (topic.expired()) {
			 	// to apo8hkeuoume sth vash dedomenwn me MONGODB
			 	topic.saveToCollection(db.getCollection("Trends"));
			 	it.remove();
		 	}
	 	}
	}

}
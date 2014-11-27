import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	public static void main(String args[]) {
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
				String rawJSON = TwitterObjectFactory.getRawJSON(status);
				try {
					storeJSON(rawJSON, "/home/thanos/Desktop/JSON.txt");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(username);

				System.out.println(profileLocation);

				System.out.println(tweetId);

				System.out.println(content + "\n");

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
				// TODO Auto-generated method stub
				while (true) {

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
						Thread.sleep(5 * 60000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		t.start();
	}

	private static void storeJSON(String rawJSON, String fileName)
			throws IOException {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(fileName,true);
			osw = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(osw);
			bw.write(rawJSON);
			bw.write(System.getProperty("line.separator"));
			bw.write(System.getProperty("line.separator"));
			bw.flush();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ignore) {
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException ignore) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

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

		// remove active topics tha expired
		for (TrendTopic topic : activeTrends) {

			boolean found = false;
			for (Trend t : trendsArray) {
				if (topic.isSameTopic(t.getName())
						&& (topic.getEndTime() == null)) {
					topic.setEndTime();
				}
			}

			if (topic.expired()) {
				activeTrends.remove(topic);
				// write to mongoDB
			}
		}
	}

}
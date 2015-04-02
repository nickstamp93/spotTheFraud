import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ListIterator;
import java.util.Random;

import twitter4j.FilterQuery;
import twitter4j.ResponseList;
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
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class main {
	private static ArrayList<TrendTopic> activeTrends;
	private static TwitterStream twitterStream;
	private static DB db;
	private static Twitter twitter;
	private static int tweetsCounter = 0;

	// private static int counter;

	public static void main(final String args[]) {
		
		int dist = LevenshteinDistance("pata", "portakias");
		
		System.out.println(dist);
		
		if(true) return;
		
		
		
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient();
			db = mongoClient.getDB("MyDataBase");// vash dedomenwn

			// Print database stats
			CommandResult cr = db.getStats();
			for (String s : cr.keySet()) {
				System.out.println(s + ": " + cr.get(s));
			}

			/*
			 * for (String s : db.getCollectionNames()) { System.out.println(s);
			 * }
			 */

		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}// sundeomaste me to server

		Configuration conf = getTwitterConfiguration();
		twitter = new TwitterFactory(conf).getInstance();
		twitterStream = new TwitterStreamFactory(conf).getInstance();

		
	
		//part4B();

		// Find the 40 users
		// userTracking();

		// twitterStream = new TwitterStreamFactory(conf).getInstance();
		// CollectTrendsAndTweets();
	}

	public static Configuration getTwitterConfiguration() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setJSONStoreEnabled(true);
		cb.setOAuthAccessToken("2870879723-zQyxbqIe6Oq2D4N16WL8V2eqMPHOb6RI5NZduuq");
		cb.setOAuthAccessTokenSecret("Q29BLdtJT8actTp6s2yAFV8Mb58OTHvoIbJO4dt1VvoRr");
		cb.setOAuthConsumerKey("pyTD0ZTSYBza0pOpN2Sm1Guwe");
		cb.setOAuthConsumerSecret("24ZIqlPbAz90P0xjyV5eFircsTIe70uTWTPamYRV1cnlVlsFBZ");
		Configuration conf = cb.build();
		return conf;
	}
	
	public static void part4A() {
		
		
 /*
		DBCursor c = db.getCollection("UsersStats").find();
		
		
		for(int i=0;i< 10;i++){
			DBObject obj = c.next();
			
			System.out.println(obj);
			
		}
*/		
		
		
		if(true) return;
		
		
		
		DBCollection userColl = db.getCollection("Users");

		
		
		
		DBCursor cursor = userColl.find();
		
		

		// All the distinct user IDs
		ArrayList<Long> usersIDs = new ArrayList<>();

		while (cursor.hasNext()) {
			DBObject user = cursor.next();
			usersIDs.add((Long) user.get("myUserID"));
		}

		
		//PROSOXH
		// The collection that stores the stats for all the users
		DBCollection usersStatsColl = db.createCollection("UsersStats", null);
		
		DBCollection tweetsColl = db.getCollection("Tweets");
		

		

		BasicDBObject select = new BasicDBObject();
		select.put("user", 1);
		
		
		
		for(int i = 0; i < usersIDs.size(); i++){
			BasicDBObject query = new BasicDBObject();
			query.put("myUserID", usersIDs.get(i));
			
			
			DBObject obj = tweetsColl.findOne(query, select);
			
			try {
				User user =  TwitterObjectFactory.createUser(obj.get("user").toString());
				int friends = user.getFriendsCount();
				int followers = user.getFollowersCount();

				long now = new Date().getTime();
				long createdAt = user.getCreatedAt().getTime();
				long age = now - createdAt;

				BasicDBObject userStats = new BasicDBObject();
				userStats.put("userID", user.getId());
				userStats.put("followers", followers);
				userStats.put("friends", friends);
				userStats.put("age", age);
				
				usersStatsColl.insert(userStats);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(i % 1000 == 0){
				System.out.println(i);
			}
			
		}
		
/*
		// Get users from userIDs using twitter API
		for (int i = 0; i < usersIDs.size(); i += 100) {

			int batchSize = usersIDs.size() - i;

			if (batchSize > 100) {
				batchSize = 100;
			}

			long usersBatch[] = new long[batchSize];

			for (int j = 0; j < batchSize; j++) {
				usersBatch[j] = usersIDs.get(i + j);
			}

			try {
				ResponseList<User> users = twitter.lookupUsers(usersBatch);

				for (User user : users) {
					int friends = user.getFriendsCount();
					int followers = user.getFollowersCount();

					long now = new Date().getTime();
					long createdAt = user.getCreatedAt().getTime();
					long age = now - createdAt;

					BasicDBObject obj = new BasicDBObject();
					obj.put("userID", user.getId());
					obj.put("followers", followers);
					obj.put("friends", friends);
					obj.put("age", age);
					
					usersStatsColl.insert(obj);
				}
				System.out.println(i);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		*/
	}
	
	public static void part4B() {
		/*  
		 // EPAGGELMATIKO PROPROGRAMMING
		
		DBCollection usersStatsColl = db.getCollection("UsersStats");
		
		System.out.println(usersStatsColl.count());
		
		BasicDBObject a = new BasicDBObject();
		a.put("userID", 1);

		BasicDBObject b = new BasicDBObject();
		b.put("unique", true);
		b.put("dropDups",true);
		
		usersStatsColl.createIndex(a, b);
		
		
		System.out.println(usersStatsColl.count());
		//DBCollection allUsersColl = db.createCollection("UserStats",null);
		*/
		
		DBCollection surveilanceTweetsColl = db.getCollection("SurveilanceTweets");
		
		
		BasicDBObject a = new BasicDBObject();
		a.put("userID", 1);

		
		surveilanceTweetsColl.createIndex(a);
		
		
		long[] allUsers = 
			{
				796664444L,
				2682619416L,
				2816971998L,
				106844439L,
				2837377765L,
				308600439L,
				788362998L,
				1697075682L,
				331926254L,
				2186537780L,
				2971275874L,
				2821715723L,
				71797278L,
				2821743573L,
				2988360424L,
				2431107457L,
				2942350465L,
				2452999712L,
				2821728940L,
				2197858347L,
				2821724331L,
				2821796200L,
				2821682193L,
				2821712415L,
				2821701844L,
				2821701898L,
				2821699036L,
				2821759187L,
				2821702996L,
				2821722719L,
				2978200793L,
				2978221756L,
				815828540L,
				2743678871L,
				2922003072L,
				2882134416L,
				2978216873L,
				2909648091L,
				2382224588L,
				537058601L
				};
		
		//For each user
		for(long userID : allUsers){
			
			BasicDBObject where = new BasicDBObject();
			where.put("myUserID", userID);
			
			//Find all the users tweets
			DBCursor cursor = surveilanceTweetsColl.find(where);
			
			
			//A list of all the users tweets
			ArrayList<Status> tweets = new ArrayList<Status>();
			
			//Store the tweets to the list
			while(cursor.hasNext()){
				DBObject obj = cursor.next();
				obj.removeField("myUserID");
				
				try {
					Status tweet = TwitterObjectFactory.createStatus(obj.toString());
					tweets.add(tweet);
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		
			
			int retweets = 0;
			
			int replies = 0;
			
			int totalHashtags = 0;
			
			int tweetsWithHashtag= 0;
			
			int urlCounter = 0;
			
			int noMentions = 0;
			
			int retweetsReceived = 0;
			
			
			ArrayList<Status> simpleTweets = new ArrayList<>();
			
			
			for(Status tweet : tweets){	
				
				boolean isRetweetOrReply = false;
				
				// ii
				if(tweet.isRetweet()){
					retweets++;
					isRetweetOrReply = true;
				}
				
				// iii
				if(tweet.getInReplyToScreenName() != null){
					replies++;
					isRetweetOrReply = true;
				}
				
				
				if(!isRetweetOrReply){
					simpleTweets.add(tweet);
				}
				
				
				
				
				// iv
				noMentions += tweet.getUserMentionEntities().length;
				
				// v
				retweetsReceived += tweet.getRetweetCount();
				
				
				if(tweet.getURLEntities().length > 0){
					urlCounter++;
				}
			
				
				totalHashtags += tweet.getHashtagEntities().length;
				
				if(tweet.getHashtagEntities().length > 0){
					tweetsWithHashtag ++;
				}
				
			}
			
			
			// vi
			double retweetsPerTweet = (1.0 * retweetsReceived)/tweets.size();
			
			// vii
			double hashtagsPerTweet = (1.0 * totalHashtags)/tweets.size();
			
			// viii
			double tweetsWithHashtagRatio = (1.0 * tweetsWithHashtag) / tweets.size();
			
			// ix
			double urlRatio = (1.0 * urlCounter) / tweets.size();
			
			
			//Pinakas apo strings pou krataei to keimeno tou tweet ka8aro xwris mentions kai urls
			String simpleTweetTexts[] = new String[simpleTweets.size()];
			
			//ka8arise ta tweetTexts
			for(int i = 0; i < simpleTweets.size(); i++){
				
				
				UserMentionEntity mentions[] = simpleTweets.get(i).getUserMentionEntities();
				for(int j = 0; j < mentions.length ; j++){
					simpleTweetTexts[i] = simpleTweets.get(i).getText().replaceAll(mentions[j].getText(),"");
				}
				
				URLEntity urls[] = simpleTweets.get(i).getURLEntities();
				for(int j = 0; j < urls.length ; j++){
					simpleTweetTexts[i] = simpleTweetTexts[i].replaceAll(urls[j].getText(),"");
				}
			}
			
			
			int similarTweets =0;
			
			for(int i = 0; i < simpleTweets.size(); i++){
				
				for(int j = i + 1; j< simpleTweetTexts.length; j++){
					int distance = LevenshteinDistance(simpleTweetTexts[i], simpleTweetTexts[j]);
					
					int mean = simpleTweetTexts[i].length() + simpleTweetTexts[j].length();
					mean /= 2;		
					
					if( distance < mean * 0.1){
						similarTweets++;
					}
				}
				
			}
			
			
			System.out.println( userID + " "
								+ simpleTweets.size() + " " 
								+ retweets + " " 
								+ replies + " " 
								+ noMentions + " "
								+ retweetsReceived + " "
								+ retweetsPerTweet + " " 
								+ hashtagsPerTweet + " " 
								+ tweetsWithHashtagRatio + " " 
								+ urlRatio + " "
								+ similarTweets);
			
		}
	}

	public static int LevenshteinDistance (String s0, String s1) {                          
	    int len0 = s0.length() + 1;                                                     
	    int len1 = s1.length() + 1;                                                     
	 
	    // the array of distances                                                       
	    int[] cost = new int[len0];                                                     
	    int[] newcost = new int[len0];                                                  
	 
	    // initial cost of skipping prefix in String s0                                 
	    for (int i = 0; i < len0; i++) cost[i] = i;                                     
	 
	    // dynamically computing the array of distances                                  
	 
	    // transformation cost for each letter in s1                                    
	    for (int j = 1; j < len1; j++) {                                                
	        // initial cost of skipping prefix in String s1                             
	        newcost[0] = j;                                                             
	        
	        // transformation cost for each letter in s0                                
	        for(int i = 1; i < len0; i++) {                                             
	            // matching current letters in both strings                             
	            int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;             
	 
	            // computing cost for each transformation                               
	            int cost_replace = cost[i - 1] + match;                                 
	            int cost_insert  = cost[i] + 1;                                         
	            int cost_delete  = newcost[i - 1] + 1;                                  
	 
	            // keep minimum cost                                                    
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }                                                                           
	 
	        // swap cost/newcost arrays                                                 
	        int[] swap = cost; cost = newcost; newcost = swap;                          
	    }                                                                               
	 
	    // the distance is the cost for transforming all letters in both strings        
	    return cost[len0 - 1];                                                          
	}
	
	public static void I_ll_I_ll_Follow(){
		
		
		if(true) return;

		db.createCollection("SurveilanceTweets" , null);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0 ;i<4*24;i++){

					try {
						System.out.println("Exoun perasei " + i + " wres ekteleshs");
						
						Thread.sleep(1000*60*60);//koimate gia 1 wra

						//Print database stats
						CommandResult cr = db.getStats();
						for(String s: cr.keySet()){
							System.out.println(s + ": " + cr.get(s));
						}
						System.out.println();
						System.out.println();
						
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				twitterStream.clearListeners();
			}
		});
		
		
		StatusListener surveilanceListener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
				// metatrepoume to tweet se JSON
				String rawJSON = TwitterObjectFactory.getRawJSON(status);
					
				
				// Dhmiourgoume apo to JSON antikeimeno gia th bash
				DBObject dbObject = (DBObject) JSON.parse(rawJSON);

				// Eisagoume to antikeimeno sth vash
				dbObject.put("myUserID", status.getUser().getId());
//				System.out.println("name:" + status.getUser().getScreenName());
//				System.out.println("text:" + status.getText());
				db.getCollection("SurveilanceTweets").insert(dbObject);
				tweetsCounter++;

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
		
		twitterStream.addListener(surveilanceListener);
		FilterQuery fq = new FilterQuery();
		
		long[] allUsers = 
			{
				796664444L,
				2682619416L,
				2816971998L,
				106844439L,
				2837377765L,
				308600439L,
				788362998L,
				1697075682L,
				331926254L,
				2186537780L,
				2971275874L,
				2821715723L,
				71797278L,
				2821743573L,
				2988360424L,
				2431107457L,
				2942350465L,
				2452999712L,
				2821728940L,
				2197858347L,
				2821724331L,
				2821796200L,
				2821682193L,
				2821712415L,
				2821701844L,
				2821701898L,
				2821699036L,
				2821759187L,
				2821702996L,
				2821722719L,
				2978200793L,
				2978221756L,
				815828540L,
				2743678871L,
				2922003072L,
				2882134416L,
				2978216873L,
				2909648091L,
				2382224588L,
				537058601L
				};
		
		fq.follow(allUsers);
		twitterStream.cleanUp();
		twitterStream.filter(fq);
		
		t.start();
		
		
	}

	public static void userTracking() {

		// --------------------------------------------------------
		// -------------FIND ALL THE DISTINCT USERIDs--------------
		// --------------------------------------------------------

		//
		// //Prepei na kanoume aggregate giati ta monadika myUserIDs einai >
		// 16mb
		//
		// // kanoume group ws pros ta myUserIDs
		// DBObject group1Fields = new BasicDBObject( "_id", "$myUserID");
		// DBObject group1 = new BasicDBObject("$group", group1Fields);
		//
		//
		//
		//
		//
		// AggregationOptions aggregationOptions = AggregationOptions.builder()
		// .batchSize(100)
		// .outputMode(AggregationOptions.OutputMode.CURSOR)
		// .allowDiskUse(true)
		// .build();
		//
		//
		//
		//
		// //Run the query
		// List<DBObject> pipeline = Arrays.asList(group1);
		// Cursor usersCursor =
		// db.getCollection("Tweets").aggregate(pipeline,aggregationOptions);
		//
		//
		//
		// DBCollection userColl = db.createCollection("Users",null);
		//
		//
		// //Add the queries results to the Users Collection
		// while(usersCursor.hasNext()){
		// DBObject obj = usersCursor.next();
		// Long userID = (Long)obj.get("_id");
		//
		// BasicDBObject userObject = new BasicDBObject();
		// userObject.put("myUserID", userID);;
		//
		// userColl.insert(userObject);
		// }

		// //Read the users from the Users Collection
		// ArrayList<Long> users = new ArrayList<>();
		//
		// DBCursor myUsersCursor = db.getCollection("Users").find();
		// while(myUsersCursor.hasNext()){
		// DBObject user = myUsersCursor.next();
		// users.add((Long)user.get("myUserID"));
		// }
		//
		//
		//
		// // lista me tous users
		// //List<Long> users = db.getCollection("Tweets").distinct("myUserID");
		//
		//
		// int numberOfUsers = users.size();
		//
		// int usersCounter = 0;
		//
		//
		// ArrayList<Integer> frequency = new ArrayList<>();
		//
		// DBCollection tweetsColl = db.getCollection("Tweets");
		//
		// //Count the number of trends each user has refered to(frequency)
		// for (long user : users) {
		//
		// HashSet<String> trends = new HashSet<>();
		//
		//
		// BasicDBObject query = new BasicDBObject("myUserID", user);
		// BasicDBObject fields = new BasicDBObject("trendTopics",1);
		//
		//
		// DBCursor cursor = tweetsColl.find(query, fields); //find(query);
		//
		//
		// DBObject dbobj;
		//
		// while (cursor.hasNext()) {
		//
		// dbobj = cursor.next();
		//
		// trends.addAll((List) dbobj.get("trendTopics"));
		//
		// }
		//
		// frequency.add(trends.size());
		//
		// usersCounter++;
		// if(usersCounter % 1000 == 0){
		// System.out.println(usersCounter);
		// }
		//
		// }
		//
		// //Store the frequencies to the Frequencies Collection
		// DBCollection frequenciesColl = db.createCollection("Frequencies",
		// null);
		// frequenciesColl.createIndex(new BasicDBObject("frequency",1));
		//
		//
		// for(int i = 0; i < users.size() ; i++){
		// BasicDBObject f = new BasicDBObject();
		// f.put("userID",users.get(i));
		// f.put("frequency", frequency.get(i));
		// frequenciesColl.insert(f);
		// }
		//

		// Read the users and their frequencies from the Frequencies Collection
		ArrayList<Long> users = new ArrayList<>();
		ArrayList<Integer> frequency = new ArrayList<>();

		DBCursor myUsersCursor = db.getCollection("Frequencies").find();
		while (myUsersCursor.hasNext()) {
			DBObject user = myUsersCursor.next();
			users.add((Long) user.get("userID"));
			frequency.add((Integer) user.get("frequency"));
		}

		int max = Collections.max(frequency);

		// invertedIndex stores all the userIDs for each frequency
		ArrayList<ArrayList<Long>> invertedIndex = new ArrayList<ArrayList<Long>>();

		for (int i = 0; i <= max; i++) {
			invertedIndex.add(new ArrayList<Long>());
		}

		for (int i = 0; i < frequency.size(); i++) {
			invertedIndex.get(frequency.get(i)).add(users.get(i));

		}

		// Print inverted index
		for (int i = 0; i <= max; i++) {
			System.out.print(i + " ---> ");
			System.out.println(invertedIndex.get(i).size());

		}

		// ---------------------------------------------
		// -------------Calculate Q1,Q2,Q3--------------
		// ---------------------------------------------

		double q1, q2, q3;

		int listSize;

		// numbersList stores all the frequencies that have at least one userID
		// (sorted)
		ArrayList<Integer> numbersList = new ArrayList<Integer>();
		for (int i = 0; i <= max; i++) {
			if (invertedIndex.get(i).size() > 0) {
				numbersList.add(i);
			}
		}

		listSize = numbersList.size() - 1;

		if (numbersList.size() % 2 == 0) {
			q2 = (numbersList.get(listSize / 2) + numbersList
					.get(listSize / 2 + 1)) / 2.0;
		} else {
			q2 = numbersList.get(listSize / 2);
		}

		ArrayList<Integer> qList = new ArrayList<>();
		int counter = 0;
		while (numbersList.get(counter) < q2) {
			qList.add(numbersList.get(counter));
			counter++;
		}
		listSize = qList.size() - 1;
		if (qList.size() % 2 == 0) {
			q1 = (qList.get(listSize / 2) + qList.get(listSize / 2 + 1)) / 2.0;
		} else {
			q1 = qList.get(listSize / 2);
		}

		qList = new ArrayList<>();
		counter = numbersList.size() - 1;
		while (numbersList.get(counter) > q2) {
			qList.add(numbersList.get(counter));
			counter--;
		}
		listSize = qList.size() - 1;
		if (qList.size() % 2 == 0) {
			q3 = (qList.get(listSize / 2) + qList.get(listSize / 2 + 1)) / 2.0;
		} else {
			q3 = qList.get(listSize / 2);
		}

		System.out.println("Q1 = " + q1);
		System.out.println("Q2 = " + q2);
		System.out.println("Q3 = " + q3);

		ArrayList<ArrayList<Long>> quartiles = new ArrayList<ArrayList<Long>>();

		// //////////////////////////////////////////////////////////////////////

		// for (int i = 0; i < 4; i++) {
		// quartiles.add(new ArrayList<Long>());
		// quartiles.get(i).addAll(sortedArray.subList(q[i], q[i + 1]));
		// }
		quartiles.add(new ArrayList<Long>());
		quartiles.add(new ArrayList<Long>());
		quartiles.add(new ArrayList<Long>());
		quartiles.add(new ArrayList<Long>());

		for (int i = 1; i < invertedIndex.size(); i++) {
			if (i < q1) {
				quartiles.get(0).addAll(invertedIndex.get(i));
			} else if (i < q2) {
				quartiles.get(1).addAll(invertedIndex.get(i));
			} else if (i < q3) {
				quartiles.get(2).addAll(invertedIndex.get(i));
			} else {
				quartiles.get(3).addAll(invertedIndex.get(i));
			}
		}

		// dialegoume 100 tuxaious xrhstes apo ka8e tetarthmorio
		long[][] tempSurveilance = new long[4][100];
		Random random = new Random();
		int r;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; quartiles.get(i).size() > 0 && j < 100; j++) {
				r = random.nextInt(quartiles.get(i).size());

				tempSurveilance[i][j] = quartiles.get(i).remove(r);

			}
		}

		Configuration conf = getTwitterConfiguration();
		final Twitter twitter = new TwitterFactory(conf).getInstance();

		// 4 dekades apo ka8e tatarthmorio tuxaiwn xrhstwn gia parakolou8hsh 7
		// hmerwn
		User[][] usersUnderSurveilance = new User[4][10];

		for (int i = 0; i < 4; i++) {

			ResponseList<User> userName = null;
			try {
				userName = twitter.lookupUsers(tempSurveilance[i]);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int j = 0; j < 10; j++) {
				usersUnderSurveilance[i][j] = userName.get(j);
				// System.out.println(i*10 +j +" -> "
				// +usersUnderSurveilance[i][j].getName());
			}
		}

		long test[] = { 1231324124L, 123123123L };

		// Print the 40 users

		// System.out.println("{");

		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 10; j++) {
				System.out.print(" "
						+ usersUnderSurveilance[i][j].getScreenName() + "-->");
				System.out.println(usersUnderSurveilance[i][j].getId());

				// System.out.println(usersUnderSurveilance[i][j].getId() +
				// "L,");
			}
			// System.out.println("---------------");
		}

		// System.out.println("}");

		long allUsers[] = new long[40];
		for (int i = 0; i < 1; i++) {
			allUsers[i] = usersUnderSurveilance[i / 10][i % 10].getId();
		}
		/*
		 * allUsers[0] = usersUnderSurveilance[0][0].getId();
		 * 
		 * db.createCollection("SurveilanceTweets" , null);
		 * 
		 * Thread t = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub try
		 * { Thread.sleep(1000*60*60*24*4); } catch (InterruptedException e1) {
		 * e1.printStackTrace(); } twitterStream.clearListeners(); } });
		 * 
		 * 
		 * StatusListener surveilanceListener = new StatusListener() {
		 * 
		 * @Override public void onStatus(Status status) { // metatrepoume to
		 * tweet se JSON String rawJSON =
		 * TwitterObjectFactory.getRawJSON(status);
		 * 
		 * // Dhmiourgoume apo to JSON antikeimeno gia th bash DBObject dbObject
		 * = (DBObject) JSON.parse(rawJSON);
		 * 
		 * // Eisagoume to antikeimeno sth vash dbObject.put("myUserID",
		 * status.getUser().getId()); System.out.println("name:" +
		 * status.getUser().getScreenName()); System.out.println("text:" +
		 * status.getText());
		 * db.getCollection("SurveilanceTweets").insert(dbObject);
		 * tweetsCounter++;
		 * 
		 * }
		 * 
		 * @Override public void onException(Exception arg0) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onDeletionNotice(StatusDeletionNotice arg0) {
		 * // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onScrubGeo(long arg0, long arg1) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onStallWarning(StallWarning arg0) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onTrackLimitationNotice(int arg0) { // TODO
		 * Auto-generated method stub
		 * 
		 * } };
		 * 
		 * twitterStream.addListener(surveilanceListener); FilterQuery fq = new
		 * FilterQuery();
		 * 
		 * fq.follow(allUsers); twitterStream.cleanUp();
		 * twitterStream.filter(fq);
		 * 
		 * t.start();
		 */

	}

	public static void CollectTrendsAndTweets() {
		// counter = 0;
		// twitterStream.cleanUp();

		if (true)
			return;

		// svhnoume gia arxikopoihsh
		// db.dropDatabase();

		db.createCollection("Trends", null);

		db.createCollection("Tweets", null);
		db.getCollection("Tweets").ensureIndex("myUserID");

		// System.out.println(db.getCollection("Tweets").count());
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
				// metatrepoume to tweet se JSON
				String rawJSON = TwitterObjectFactory.getRawJSON(status);

				// Dhmiourgoume apo to JSON antikeimeno gia th bash

				DBObject dbObject = (DBObject) JSON.parse(rawJSON);

				// Get the tweet's text
				String tweetText;

				if (status.isRetweet()) {
					tweetText = status.getRetweetedStatus().getText();
				} else {
					tweetText = status.getText();
				}
				tweetText = tweetText.replaceAll("\\s+", " ");

				// Find all the trends in the tweet
				ArrayList<String> tweetTrends = new ArrayList<>();

				for (TrendTopic trendTopic : activeTrends) {

					String t = trendTopic.getTitle().toLowerCase();
					if (tweetText.toLowerCase().contains(t)) {
						tweetTrends.add(t);
					}
				}

				/*
				 * if(tweetTrends.size() == 0){ System.out.println("-" +
				 * status.getText()); if(status.isRetweet()){
				 * System.out.println("-" +
				 * status.getRetweetedStatus().getText()); }
				 * System.out.println("-----------------------------------------"
				 * ); }
				 */

				// An exei toulaxiston 1 trend apo8hkeuse to
				if (tweetTrends.size() > 0) {
					// Eisagoume to antikeimeno sth vash
					dbObject.put("myUserID", status.getUser().getId());
					dbObject.put("trendTopics", tweetTrends);
					db.getCollection("Tweets").insert(dbObject);
					tweetsCounter++;
				}
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
				long started = new Date().getTime();// arxh gia elegxo gia 3
													// meres
				long now = new Date().getTime();
				CommandResult cr = db.getStats();

				long databaseFileSize = cr.getLong("fileSize");
				long thirtyGiga = 32212254720L;

				// h while prepei na krathsei 3 meres h mexri ta 30 GigaByte
				while ((now - started <= 259200000)
						&& (databaseFileSize < thirtyGiga)) {

					double fileSizeGiga = (double) databaseFileSize
							/ (1024 * 1014 * 1024);

					System.out.println();
					System.out.printf("Filesize: %.2f GB", fileSizeGiga);
					System.out.println("   #ActiveTrends: "
							+ activeTrends.size());
					System.out.println("#tweets: " + tweetsCounter);
					tweetsCounter = 0;

					Trends trends;
					Trend[] trendsArray;
					try {
						// get top ten trends globally
						trends = twitter.getPlaceTrends(1);
						trendsArray = trends.getTrends();

						updateActive(trendsArray);

						// Print active trends
						/*
						 * for (TrendTopic trendTopic : activeTrends) { String t
						 * = trendTopic.getTitle(); System.out.println(t); }
						 */

						FilterQuery fq = new FilterQuery();
						String keywords[] = new String[activeTrends.size()];
						for (int i = 0; i < keywords.length; i++) {
							keywords[i] = activeTrends.get(i).getTitle();
						}
						fq.track(keywords);
						twitterStream.cleanUp();
						twitterStream.filter(fq);

					} catch (TwitterException e) {
						e.printStackTrace();
						System.out.println(e.toString());
					}
					// Perimene gia 5 lepta
					try {
						Thread.sleep(300000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					// ananewnw ton elegxo ths wras
					now = new Date().getTime();
					cr = db.getStats();
					databaseFileSize = cr.getLong("fileSize");
				}

				twitterStream.cleanUp();
				// Na valoume ola ta activeTrends na exoun endTime to twrino
				// meta tis 3 wres
				for (TrendTopic t : activeTrends) {
					t.setEndTime();
					t.saveToCollection(db.getCollection("Trends"));
				}
				activeTrends.clear();
				System.out.println("Data collection terminating...");
				// System.exit(0);
			}
		});
		t.start();
	}

	private static void updateActive(Trend[] trendsArray) {
		// insert new topics to active
		for (Trend t : trendsArray) {
			boolean found = false;

			for (TrendTopic topic : activeTrends) {
				if (topic.isSameTopic(t.getName())) {
					found = true;
					topic.setEndTimeToNull();
					break;
				}
			}
			if (!found) {
				// if the new trend was not found , add it to active
				activeTrends.add(new TrendTopic(t.getName()));
			}
		}

		// remove active topics tha expired
		ListIterator<TrendTopic> it = activeTrends.listIterator();
		while (it.hasNext()) {
			TrendTopic topic = (TrendTopic) it.next();
			boolean found = false;

			for (Trend t : trendsArray) {
				if (topic.isSameTopic(t.getName())) {
					found = true;
				}
			}
			if (!found && topic.getEndTime() == null) {
				topic.setEndTime();
			}

			if (topic.expired()) {
				// to apo8hkeuoume sth vash dedomenwn me MONGODB
				topic.saveToCollection(db.getCollection("Trends"));
				it.remove();
			}
		}
	}

}
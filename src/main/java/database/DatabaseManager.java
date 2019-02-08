package database;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

public class DatabaseManager {
	private static DatabaseManager manager = null;
	private ScheduledExecutorService buttonExecutorService;
	private MongoCollection<Document> coll;

	private DatabaseManager(String URI) {
		if (manager == null) {
			// connect to database and insert
			MongoClient mongo = MongoClients.create(URI);
			coll = mongo.getDatabase("tuner-bot").getCollection("buttons");
			// start thread to give buttons every 30 mins
			startThread();

			manager = this;
//			Document test = new Document("_id", "jo").append("buttons", 1);
//			coll.insertOne(test);
//			Document test2 = coll.find(new Document("_id", "jo")).first();
//			test2.replace("buttons", ((int) test2.get("buttons") + 1));
//			coll.findOneAndReplace(coll.find(new Document("_id", "jo")).first(), test2);
		}
	}

	// Task to give buttons
	Runnable giveButtons = () -> {
		// find online users that have role verified
		// give 1 button to each
		// for debug
		System.out.println("Gave buttons!");
	};
	private void startThread() {
		buttonExecutorService = Executors.newSingleThreadScheduledExecutor();
		buttonExecutorService.scheduleAtFixedRate(giveButtons, 30, 30, TimeUnit.MINUTES);
	}

	public static DatabaseManager getInstance(String URI) {
		return new DatabaseManager(URI);
	}

	public static DatabaseManager getInstace() {
		return manager;
	}

	public MongoCollection<Document> getCollection() {
		return coll;
	}
}

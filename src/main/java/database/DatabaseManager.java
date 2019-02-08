package database;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bson.Document;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

public class DatabaseManager {
	private static DatabaseManager manager = null;
	private ScheduledExecutorService buttonExecutorService;
	private MongoCollection<Document> coll;
	private DiscordApi api;

	private DatabaseManager(String URI, DiscordApi api) {
		if (manager == null) {
			// connect to database and insert
			MongoClient mongo = MongoClients.create(URI);
			coll = mongo.getDatabase("tuner-bot").getCollection("buttons");

			// give manager the api to find users
			this.api = api;

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
		List<User> onlineUsers = api.getServerById(91082346962358272L).get().getRolesByName("Verified").get(0)
				.getUsers().stream().filter(u -> u.getDesktopStatus() != UserStatus.OFFLINE)
				.collect(Collectors.toList());
		// give 1 button to each
		// for debug
		System.out.println("Gave buttons!");
	};

	private void startThread() {
		buttonExecutorService = Executors.newSingleThreadScheduledExecutor();
		buttonExecutorService.scheduleAtFixedRate(giveButtons, 00, 30, TimeUnit.MINUTES);
	}

	public static DatabaseManager getInstance(String URI, DiscordApi api) {
		return new DatabaseManager(URI, api);
	}

	public static DatabaseManager getInstace() {
		return manager;
	}

	public MongoCollection<Document> getCollection() {
		return coll;
	}
}

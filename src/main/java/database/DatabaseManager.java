package database;

import java.util.List;
import java.util.ArrayList;
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
		}
	}

	// Task to give buttons
	Runnable giveButtons = () -> {
		// find online users that have role verified
		List<User> onlineUsers = api.getServerById(91082346962358272L).get().getRolesByName("Verified").get(0)
				.getUsers().stream().filter(u -> u.getDesktopStatus() != UserStatus.OFFLINE)
				.collect(Collectors.toList());
		// give 1 button to each
		onlineUsers.forEach(u -> addButton(u, 1));
	};

	private void startThread() {
		buttonExecutorService = Executors.newSingleThreadScheduledExecutor();
		buttonExecutorService.scheduleAtFixedRate(giveButtons, 5, 30, TimeUnit.MINUTES);
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

	private void addButton(User u, int amount) {
		// check if user in database
		Document query = coll.findOneAndDelete(new Document("_id", u.getId()));
		// if they are
		if(query != null) 
		{
			Document dbUser = query;
			int currentButtons = (int) dbUser.get("buttons") + 1;
			dbUser.replace("buttons", currentButtons);
			coll.insertOne(dbUser);
		}
		// if not
		else
			addNewUser(u.getId());
	}

	private void addNewUser(Long id) {
		Document newUser = new Document("_id", id).append("buttons", 1).append("rapsheet",
				new Document("warings", new ArrayList<String>()).append("kicks", new ArrayList<String>()).append("bans",
						new ArrayList<String>()));
		coll.insertOne(newUser);
	}
}

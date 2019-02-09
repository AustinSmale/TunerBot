package database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
	private static MongoCollection<Document> coll;
	private DiscordApi api;
	private boolean writeLock;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
		List<User> onlineUsersList = api.getServerById(91082346962358272L).get().getRolesByNameIgnoreCase("Verified")
				.get(0).getUsers().stream().filter(u -> u.getStatus() != UserStatus.OFFLINE)
				.collect(Collectors.toList());
		// give 1 button to each
		writeLock = true;
		onlineUsersList.forEach(u -> addButton(u, 1));
		writeLock = false;

	};

	private void startThread() {
		buttonExecutorService = Executors.newSingleThreadScheduledExecutor();
		buttonExecutorService.scheduleAtFixedRate(giveButtons, 0, 30, TimeUnit.MINUTES);
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
		Document query = coll.find(new Document("d_id", u.getId())).first();
		// if they are
		if (query != null) {
			Document dbUser = query;
			int currentButtons = (int) dbUser.get("buttons") + 1;
			dbUser.replace("buttons", currentButtons);
			coll.replaceOne(new Document("d_id", u.getId()), dbUser);
		}
		// if not
		else
			addNewUser(u.getId());
	}

	public static void addButtonsToID(long id, int amount) {
		// check if user in database
		Document query = coll.find(new Document("d_id", id)).first();
		// if they are
		if (query != null) {
			Document dbUser = query;
			int currentButtons = (int) dbUser.get("buttons") + amount;
			dbUser.replace("buttons", currentButtons);
			coll.replaceOne(new Document("d_id", id), dbUser);
		}
	}

	private Document addNewUser(Long id) {
		Document newUser = new Document("d_id", id).append("buttons", 1).append("rapsheet",
				new Document("warning", new ArrayList<String>()).append("kick", new ArrayList<String>()).append("ban",
						new ArrayList<String>()));
		coll.insertOne(newUser);
		return newUser;
	}

	public static int getButtonsForUser(Long id) {
		Document user = coll.find(new Document("d_id", id)).first();
		if(user != null)
			return user.getInteger("buttons");
		return 0;
	}

	public void addToRapSheet(Long id, User mod, String type, String reason) {
		Document user = coll.find(new Document("d_id", id)).first();
		// if not in db, add them
		if (user == null)
			user = addNewUser(id);
		List<String> rapsheet = (List<String>) getRapSheetForUser(id).get(type);
		String toAdd = dateFormat.format(new Date()) + " - " + mod.getMentionTag() + " - " + reason;
		rapsheet.add(0, toAdd);
		Document newRS = (Document) user.get("rapsheet");
		newRS.replace(type, rapsheet);
		user.replace("rapsheet", newRS);

		coll.replaceOne(new Document("d_id", id), user);
	}

	public Document getRapSheetForUser(Long id) {
		Document user = coll.find(new Document("d_id", id)).first();
		Document rapsheet = (Document) user.get("rapsheet");
		return rapsheet;
	}
}

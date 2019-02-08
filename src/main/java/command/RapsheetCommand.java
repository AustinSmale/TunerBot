package command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import com.mongodb.client.MongoCollection;

import database.DatabaseManager;

public class RapsheetCommand implements Command {

	String warning = "";
	String kick = "";
	String ban = "";
	
	@Override
	public void process(MessageCreateEvent event) {
		MongoCollection<Document> coll = DatabaseManager.getInstace().getCollection();
		
		String userID = event.getMessageContent().split(" ")[1];
		userID = userID.substring(0, userID.length());
		User user = event.getServer().get().getMemberById(userID).get();
		Document userDB = coll.find(new Document("_id", userID.toString())).first();
		
		// build warnings string
		Document temp = (Document) userDB.get("rapsheet");
		ArrayList<String> warnings = (ArrayList<String>) temp.get("warning");
		warnings.forEach(w -> { warning += w+"\n";});
		// build kicks string
		ArrayList<String> kicks = (ArrayList<String>) temp.get("kick");
		kicks.forEach(w -> { kick += w+"\n";});
		// build bans string
		ArrayList<String> bans = (ArrayList<String>) temp.get("ban");
		bans.forEach(w -> { ban += w+"\n";});
		
		
		
		EmbedBuilder embed = new EmbedBuilder().setThumbnail(user.getAvatar()).setColor(new Color(139, 0, 0))
				.addField(user.getDiscriminatedName(), "Nickname: " + user.getName(), false)
				.addField("Warnings:", warning)
				.addField("Kicks:", kick)
				.addField("Bans:", ban);

		event.getChannel().sendMessage(embed);
	}

	@Override
	public List<String> getAlternativeNames() {
		return Arrays.asList("rs");
	}

}

package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

public class UserJoined implements ServerMemberJoinListener {
	private static Logger logger = LogManager.getLogger();

	@Override
	public void onServerMemberJoin(ServerMemberJoinEvent event) {
		logger.info("User Joined: "+event.getUser().getId());
		
		event.getServer().getTextChannelById(542759482434781184L).get().sendMessage(
				"Welcome "+event.getUser().getMentionTag()+ "to ***Porphi's Discord***"
				+ "\n"
				+ "\nMake sure to read the rules in "+event.getServer().getTextChannelById(298683234290761729L).get().getMentionTag()
				+ "\nPlease use "+event.getServer().getTextChannelById(310560101352210432L).get().getMentionTag()+" to spam commands, also use `!help` for a list of commands"
				+ "\nAlso check the pinned message at the top for the color schemes");
	}

}

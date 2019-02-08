import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import database.DatabaseManager;
import listeners.CommandHandler;
import listeners.UserJoined;

/**
 * Runner Class
 * 
 * @author Austin
 * 
 *         Discord Bot - TunerBot Feb 6th, 2019
 */
public class Bot {
	private final static Logger logger = LogManager.getLogger();

	public static void main(String[] args) {

		// verify token was placed in arguments
		if (args.length != 2) {
			System.err.println("Missing Arguments");
			return;
		}

		// Enable debugging, if no slf4j logger was found
		FallbackLoggerConfiguration.setDebug(false);
		
		// connect bot
		DiscordApi api = new DiscordApiBuilder().setToken(args[0]).login().join();
		
		// Initialize database
		DatabaseManager.getInstance(args[1], api);
		
		logger.info("Successfully Connected as: "+api.getClientId());
	
		// commands
		api.addMessageCreateListener(new CommandHandler());
		
		//user join
		api.addServerMemberJoinListener(new UserJoined());
	}
}

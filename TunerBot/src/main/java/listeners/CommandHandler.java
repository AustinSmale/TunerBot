package listeners;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import command.Command;
import command.PingCommand;
import command.RapsheetCommand;
import command.UserInfoCommand;
import command.VerifyCommand;

public class CommandHandler implements MessageCreateListener {

	private HashMap<String, Command> commands = new HashMap<String, Command>();
	private Logger logger = LogManager.getLogger();
	private HashMap<String, String> commandAlternative = new HashMap<String, String>();

	/**
	 * Add all the commands to the HashMap and alternative names to another that
	 * point to one in the first HashMap
	 */
	public CommandHandler() {
		commands.put("userinfo", new UserInfoCommand());
		commands.put("verify", new VerifyCommand());
		commands.put("ping", new PingCommand());
		commands.put("rapsheet", new RapsheetCommand());

		// for each command in commands
		// call getAlternativeName and assign to that key
		commands.keySet().stream().forEach(key -> {
			List<String> alts = commands.get(key).getAlternativeNames();
			if (alts != null)
				alts.forEach(a -> commandAlternative.put(a, key));
		});
	}

	@Override
	public void onMessageCreate(MessageCreateEvent event) {

		// make sure prefix starts message command and is in #bot_spam
		if (event.getMessageContent().startsWith("!") && event.getChannel().getId() == 310560101352210432L) {
			// remove prefix from string and anything after
			String command = event.getMessageContent().substring(1).split(" ")[0].toLowerCase();
			// check if original command name
			if (commands.containsKey(command))
				commands.get(command).process(event);
			// check if alternative name for command
			else if (commandAlternative.containsKey(command))
				commands.get(commandAlternative.get(command)).process(event);

			logger.info(event.getMessageAuthor().getDiscriminatedName() + " invoked: " + command);
		}

	}

}

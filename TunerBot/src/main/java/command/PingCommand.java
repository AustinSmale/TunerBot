package command;

import java.util.List;

import org.javacord.api.event.message.MessageCreateEvent;

/**
 * Delegate the commands to proper command
 * 
 * @author Austin
 *
 */
public class PingCommand implements Command {

	public void process(MessageCreateEvent event) {
		event.getChannel().sendMessage("pong");
	}

	@Override
	public List<String> getAlternativeNames() {
		return null;
	}
}

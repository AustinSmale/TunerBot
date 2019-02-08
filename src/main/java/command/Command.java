package command;

import java.util.List;

import org.javacord.api.event.message.MessageCreateEvent;

public interface Command {
	public void process(MessageCreateEvent event);
	public List<String> getAlternativeNames();
}

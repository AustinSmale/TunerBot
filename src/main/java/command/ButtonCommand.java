package command;

import java.util.Arrays;
import java.util.List;

import org.javacord.api.event.message.MessageCreateEvent;

import database.DatabaseManager;

public class ButtonCommand implements Command {

	@Override
	public void process(MessageCreateEvent event) {
		event.getChannel()
				.sendMessage(event.getMessageAuthor().asUser().get().getMentionTag() + " you currently have "
						+ DatabaseManager.getButtonsForUser(event.getMessageAuthor().getId())
						+ event.getServer().get().getCustomEmojiById(310860361006055424L).get().getMentionTag() + "'s");
	}

	@Override
	public List<String> getAlternativeNames() {
		return Arrays.asList("b", "button");
	}

}

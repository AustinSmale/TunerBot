package command;

import java.util.List;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

public class VerifyCommand implements Command {
	@Override
	public void process(MessageCreateEvent event) {
		Role role = (Role) event.getApi().getRolesByName("Verified").toArray()[0];
		event.getMessageAuthor().asUser().ifPresent(user -> user.addRole(role));
		event.getChannel().sendMessage("You have been verified");
	}

	@Override
	public List<String> getAlternativeNames() {
		return null;
	}

}

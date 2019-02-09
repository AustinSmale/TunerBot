package command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.javacord.api.event.message.MessageCreateEvent;

import database.DatabaseManager;

public class BetCommand implements Command {

	@Override
	public void process(MessageCreateEvent event) {
		try {
			int amount = Integer.parseInt(event.getMessageContent().split(" ")[1]);
			int userAmount = DatabaseManager.getButtonsForUser(event.getMessageAuthor().getId());
			if (amount > userAmount || amount < 1) {
				event.getChannel().sendMessage("Can't bet that amount of buttons");
			} else {
				int roll = gamble(event.getMessageAuthor().getId(), amount);
				String message = "You rolled " + roll + ", You ";
				message += (roll >= 600 || roll == 420) ? "won!\n" : "lost!\n";
				message += event.getMessageAuthor().asUser().get().getMentionTag() + ", now has "
						+ DatabaseManager.getButtonsForUser(event.getMessageAuthor().getId());
				event.getChannel().sendMessage(message);
			}
		} catch (Exception e) {
			event.getChannel().sendMessage("Error: Command Usage is `!bet <amount>`");
		}
	}

	private int gamble(long id, int amount) {
		Random rnd = new Random();
		int roll = rnd.nextInt(1000) + 1;
		if (roll == 1000)
			amount = amount * 14;
		else if (roll >= 990)
			amount = amount * 9;
		else if (roll >= 900)
			amount = amount * 2;
		else if (roll == 777)
			amount = amount * 6;
		else if (roll >= 600)
			amount = amount;
		else if (amount == 420)
			amount = amount * (rnd.nextInt(37) + 2);
		else
			amount = -amount;

		// persist to database
		DatabaseManager.addButtonsToID(id, amount);
		return roll;
	}

	@Override
	public List<String> getAlternativeNames() {
		return Arrays.asList("gamble", "g");
	}

}

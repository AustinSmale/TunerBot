package command;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

public class UserInfoCommand implements Command {

	@Override
	public void process(MessageCreateEvent event) {
		// user
		MessageAuthor user = event.getMessageAuthor();
		Optional<Instant> joinServerDate = event.getServer().get()
				.getMemberByDiscriminatedName(user.getDiscriminatedName()).get()
				.getJoinedAtTimestamp(event.getServer().get());
		// user roles
		List<Role> roles = event.getServer().get().getMemberByDiscriminatedName(user.getDiscriminatedName()).get()
				.getRoles(event.getServer().get());
		roles = roles.subList(1, roles.size());
		String rolesToString = "";
		Color roleColor = roles.get(roles.size() - 1).getColor().get();
		for (Role r : roles)
			rolesToString += r.getMentionTag() + ", ";

		// get rid of ending ,
		rolesToString = rolesToString.substring(0, rolesToString.length() - 2);

		// build dates
		Date discordDate = Date.from(user.getCreationTimestamp());
		Date serverDate = Date.from(joinServerDate.get());
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd, yyyy 'at' h:mm a z");
		String formattedDiscordDate = formatter.format(discordDate);
		String formattedServerDate = formatter.format(serverDate);

		// embed
		EmbedBuilder embed = new EmbedBuilder().setThumbnail(user.getAvatar()).setColor(roleColor)
				.addField(user.getDiscriminatedName(), "Nickname: " + user.getDisplayName(), false)
				.addField("Joined Discord:", formattedDiscordDate, true)
				.addField("Joined Server:", formattedServerDate, true).addField("Roles:", rolesToString, false)
				.addField("Buttons:", "Not in yet :(", false);

		event.getChannel().sendMessage(embed);

	}

	@Override
	public List<String> getAlternativeNames() {
		return Arrays.asList("ui");
	}
}

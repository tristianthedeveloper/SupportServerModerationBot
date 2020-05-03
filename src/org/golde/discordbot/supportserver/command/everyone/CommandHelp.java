package org.golde.discordbot.supportserver.command.everyone;

import java.util.List;
import java.util.Objects;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandHelp extends EveryoneCommand {

	public CommandHelp() {
		super("help", null, "What are all the commands again?!");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		StringBuilder builder = new StringBuilder(" __**Commands**:__");
		Category category = null;

		for(Command command : event.getClient().getCommands()) {
			//if(!command.isOwnerCommand() || event.getAuthor().getId().equals(event.getClient().getOwnerId())){
				
				if(!Objects.equals(category, command.getCategory())){
					category = command.getCategory();
					builder.append("\n\n  __").append(category.getName()).append("__:");
				}

				builder.append("\n**").append("        �   ").append(event.getClient().getPrefix()).append(command.getName())
				.append(command.getArguments()==null ? "**" : " "+command.getArguments()+"**")
				.append(" - ").append(command.getHelp());
			//}
		}
		builder.append("\n\nDo not include <> nor [] - <> means required and [] means optional."
				+ "\nFor additional help, contact **Eric Golde#3352**");


		event.getChannel().sendMessage(builder.toString()).queue();;
	}

}

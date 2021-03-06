package org.golde.discordbot.supportserver.command.chatmod;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.constants.Categories;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class CommandLock extends ChatModCommand {

	public CommandLock(@Nonnull ESSBot bot) {
		super(bot, "lock", "<channel>", "Lock a given channel");
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		Guild g = event.getGuild();
		TextChannel tc = event.getTextChannel();
		
		
		if(args.size() != 0) {
			List<TextChannel> gotten = g.getTextChannelsByName(args.get(0), true);
			if(gotten.size() == 0) {
				replyError(tc, "No channel found!");
				return;
			}
			else {
				tc = gotten.get(0);
			}
		}
		
		if(!canLock(g, tc)) {
			replyError(tc, "That channel is protected and can not be locked.");
			return;
		}
		
		PermissionOverride permissionOverride = tc.getPermissionOverride(g.getRoleById(Roles.EVERYONE));
		PermissionOverrideAction manager;
		if(permissionOverride == null) {
			manager = tc.createPermissionOverride(g.getRoleById(Roles.EVERYONE));
		}
		else {
			manager = permissionOverride.getManager();
		}

        manager.deny(Permission.MESSAGE_WRITE).queue();
		ModLog.log(g, ModLog.getActionTakenEmbed(bot, ModAction.LOCK, event.getAuthor(), new String[] {
				"Channel",
				tc.getAsMention()
		}));
		
		replySuccess(tc, "Success!");
		
	}

	static boolean canLock(Guild g, TextChannel tc){

		List<GuildChannel> channels = new ArrayList<GuildChannel>();

		//Discussion
		for(GuildChannel c : g.getCategoryById(Categories.DISCUSSION).getChannels()) {

			if(!c.getName().equalsIgnoreCase("leak-lounge")) {
				channels.add(c);
			}

		}

		//User Stuff & Things
		for(GuildChannel c : g.getCategoryById(Categories.USER_CONTRIBUTIONS).getChannels()) {
			channels.add(c);
		}

		//Misc
		for(GuildChannel c : g.getCategoryById(Categories.MISCELLANEOUS_CHATS).getChannels()) {
			channels.add(c);
		}

		return channels.contains(tc);

	}



}

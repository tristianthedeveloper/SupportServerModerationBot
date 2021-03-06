package org.golde.discordbot.utilities.event;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.shared.event.AbstractMessageChecker;
import org.golde.discordbot.shared.util.FileUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class LikeDislikePollEvents extends AbstractMessageChecker {

	private static List<Long> IDS = new ArrayList<Long>();
	
	public static void reload() {
		IDS.clear();
		IDS = FileUtil.loadArrayFromFile("like-dislike-poll-channels", Long[].class);
	}
	
	public LikeDislikePollEvents(ESSBot bot) {
		super(bot);
	}

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		if(sender.isFake() || sender.getUser().isBot()) {
			return false;
		}
		
		if(sender.isOwner()) {
			return false;
		}
		
		return IDS.contains(msg.getTextChannel().getIdLong());
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		
		msg.addReaction("like:604876349844226060").queue(onSuccess -> {
			msg.addReaction("dislike:604876349286645780").queue(onSuccess2 -> {
				
			}, onFail2 -> {
				//do nothing
			});;
		}, onFail -> {
			//do nothing
		});	
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		
		Guild g = event.getGuild();
		TextChannel tc = event.getChannel();
		
		if(event.getUser().isBot() || event.getUser().isFake()) {
			return;
		}
		
		if(IDS.contains(tc.getIdLong())) {
			
			tc.retrieveMessageById(event.getMessageIdLong()).queue(message -> {
				for(MessageReaction r : message.getReactions()) {
					//System.out.println(event.getReactionEmote().getName());
					if(event.getReactionEmote().getName().equals("like")) {
						if(r.getReactionEmote().getName().equals("dislike")) {
							r.removeReaction(event.getUser()).queue();
						}
					}
					
					else if(event.getReactionEmote().getName().equals("dislike")) {
						if(r.getReactionEmote().getName().equals("like")) {
							r.removeReaction(event.getUser()).queue();
						}
					}
				}
			});
			
		}
		
	}

}

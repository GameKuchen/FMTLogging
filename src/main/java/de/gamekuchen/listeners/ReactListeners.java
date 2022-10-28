package de.gamekuchen.listeners;

import de.gamekuchen.FMTLogging;
import de.gamekuchen.utils.GetPropertyValues;
import de.gamekuchen.utils.JDBCUtilsLogs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;

public class ReactListeners extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getChannel().getId().equals(GetPropertyValues.banLog)) {
            if (event.getEmoji().getType().equals(Emoji.Type.CUSTOM)) {
                if (event.getEmoji().asCustom().getFormatted().equals("<:verify:995242387561513040>")) {
                    event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {
                        if (message.getAuthor().isBot()) {
                            if (!event.getUser().isBot()) {
                                Role seniorMod = event.getGuild().getRoleById(GetPropertyValues.seniorModID);
                                Role roomMod = event.getGuild().getRoleById(GetPropertyValues.roomModID);
                                Role owner = event.getGuild().getRoleById(GetPropertyValues.ownerID);
                                if (event.getMember().getRoles().contains(seniorMod) || event.getMember().getRoles().contains(roomMod)|| event.getMember().getRoles().contains(owner)) {
                                    FMTLogging.logger.debug(String.format("Embeds: %s", event.getChannel().retrieveMessageById(event.getMessageId())));
                                    event.getChannel().retrieveMessageById(event.getMessageId()).queue(e -> {
                                        MessageEmbed embed = e.getEmbeds().get(0);
                                        if(embed.getTitle().equals("User Banned")) {
                                            event.getReaction().removeReaction(event.getUser()).queue();
                                            return;
                                        }
                                        event.getChannel().retrieveMessageById(event.getMessageId()).queue(e2 -> {
                                            EmbedBuilder eb = new EmbedBuilder();
                                            eb.copyFrom(embed);
                                            eb.setTitle("User Banned");
                                            eb.setColor(new Color(96, 222, 96));
                                            eb.setDescription(String.format("User has been banned by %s", event.getUser().getName()));
                                            eb.setTimestamp(Instant.now());
                                            e2.editMessageEmbeds(embed).setEmbeds(eb.build()).queue();
                                            try {
                                                JDBCUtilsLogs.dbUserSetBanned(embed.getFields().get(0).getValue(), 1);
                                                message.getReactions().forEach(messageReaction -> messageReaction.clearReactions().queue());
                                            } catch (IOException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                        });
                                    });
                                } else {
                                    event.getReaction().removeReaction(event.getUser()).queue();
                                }
                            }
                        }
                    });
                }
            }

        }
    }
}

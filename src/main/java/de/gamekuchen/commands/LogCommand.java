package de.gamekuchen.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.gamekuchen.utils.GetPropertyValues;
import de.gamekuchen.utils.JDBCUtilsLogs;
import de.gamekuchen.utils.RecAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LogCommand extends SlashCommand {
    public LogCommand() {
        this.name = "log";
        this.help = "create, delete or view logs with this command";
        this.children = new SlashCommand[]{new Add(), new Delete(), new Get(), new Ban()};
    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }

    private static class Add extends SlashCommand {
        public Add() {
            this.name = "add";
            this.help = "Adds a new infraction";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "username", "The @ user name of the Rec Room User").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "reason", "The reason for the infraction")
                            .addChoice("RDM", "RDM")
                            .addChoice("Dryfire", "Dryfire")
                            .addChoice("Harassment", "Harassment")
                            .addChoice("Mass RDM", "Mass RDM")
                            .addChoice("Trolling", "Trolling")
                            .addChoice("Hate Speech", "Hate Speech")
                            .addChoice("Disrespect", "Disrespect")
                            .addChoice("XP-Stealing", "XP-Stealing")
                            .addChoice("2 Power Weapons", "2 Power Weapons")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            OptionMapping optionUsername = event.getOption("username");
            OptionMapping optionReason = event.getOption("reason");
            String username = optionUsername.getAsString().toLowerCase();
            String reason = optionReason.getAsString();
            event.deferReply().setEphemeral(true).queue();
            InteractionHook hook = event.getHook();

                if(!RecAPI.recAccountExists(username)) {
                    hook.sendMessage("This user does not exist.").setEphemeral(true).queue();
                    return;
                }

            String userThumbnail = RecAPI.recProfileImage(username);
            try {
                if (JDBCUtilsLogs.dbUserExists(username)) {
                    if (!JDBCUtilsLogs.dbUserGetIsBanned(username)) {
                        if (JDBCUtilsLogs.dbUserGetStrikes(username) != 3) {
                            try {
                                JDBCUtilsLogs.dbUserAddStrike(username, reason);
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Log Added");
                                eb.setColor(new Color(236, 76, 76));
                                eb.setDescription(String.format("A new Log has been added by %s", event.getMember().getUser().getName()));
                                eb.setThumbnail(userThumbnail);
                                eb.addField("Logged User: ", username, false);
                                eb.addField("Reason: ", reason, false);
                                eb.setTimestamp(Instant.now());
                                if(JDBCUtilsLogs.dbUserGetStrikes(username) == 3) {
                                    eb.clearFields();
                                    eb.setTitle("Pending Ban");
                                    eb.setColor(new Color(47, 49, 54));
                                    eb.addField("User: ", username, false);
                                    eb.addField("Infraction 1:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 1), true);
                                    eb.addField("Infraction 2:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 2), true);
                                    eb.addField("Infraction 3:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 3), true);
                                    eb.setDescription(String.format("Users ban is Pending. Logged by %s", event.getMember().getUser().getName()));
                                    event.getGuild().getTextChannelById(GetPropertyValues.banLog).sendMessageEmbeds(eb.build()).queue(e -> e.addReaction(event.getGuild().getEmojiById("995242387561513040")).queue());
                                    hook.sendMessage("The user has been added to the ban log. He has exceeded ***3*** Strikes").queue();
                                } else {
                                    hook.sendMessage("The user has been successfully logged.").setEphemeral(true).queue();
                                    event.getGuild().getTextChannelById(GetPropertyValues.kickLog).sendMessageEmbeds(eb.build()).queue();
                                }


                            } catch (ExecutionException | InterruptedException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            hook.sendMessage("This user already has 3 Infractions. Please tell a Mod to check the Ban logs for that user.").setEphemeral(true).queue();
                        }
                    }else {
                        hook.sendMessage("This user is already banned!").queue();
                    }
                } else {
                    try {
                        JDBCUtilsLogs.dbUserCreate(username, reason);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Log Added");
                    eb.setColor(new Color(236, 76, 76));
                    eb.setDescription(String.format("A new Log has been added by %s", event.getMember().getUser().getName()));
                    eb.setThumbnail(userThumbnail);
                    eb.addField("Logged User: ", username, false);
                    eb.addField("Reason: ", reason, false);
                    eb.setTimestamp(Instant.now());
                    event.getGuild().getTextChannelById(GetPropertyValues.kickLog).sendMessageEmbeds(eb.build()).queue();
                    hook.sendMessage("The user has been successfully logged.").setEphemeral(true).queue();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class Delete extends SlashCommand {
        public Delete() {
            this.name = "delete";
            this.help = "Completely removes all Infractions from a User";
            List<OptionData> optionData = new ArrayList<>();
            optionData.add(new OptionData(OptionType.STRING, "username", "The @ user name of the Rec Room User").setRequired(true));
            this.options = optionData;
        }

        protected void execute(SlashCommandEvent event) {
            Role host = event.getGuild().getRoleById(GetPropertyValues.hostID);
            Role senioHost = event.getGuild().getRoleById(GetPropertyValues.seniorHostID);
            Role ownerID = event.getGuild().getRoleById(GetPropertyValues.ownerID);

            if (!event.getMember().getRoles().contains(host) || event.getMember().getRoles().contains(senioHost) || event.getMember().getRoles().contains(ownerID)) {
                OptionMapping optionUsername = event.getOption("username");
                String username = optionUsername.getAsString().toLowerCase();

                try {
                    if(JDBCUtilsLogs.dbUserExists(username)){
                        JDBCUtilsLogs.dbUserRemove(username);
                        event.reply("Successfully removed user from the Database and cleared all Infractions").setEphemeral(true).queue();
                    } else {
                        event.reply("This user is not in the Database.").setEphemeral(true).queue();
                    }
                } catch (IOException e) {
                    event.reply("Error! Contact GameKuchen!").queue();
                    throw new RuntimeException(e);
                }
            } else  {
                event.reply("You do not have enough permissions for this command :)").setEphemeral(true).queue();
            }
        }
    }

    private static class Get extends SlashCommand {
        public Get() {
            this.name = "get";
            this.help = "Gets the specified users logs";
            List<OptionData> optionData = new ArrayList<>();
            optionData.add(new OptionData(OptionType.STRING, "username", "The @ user name of the Rec Room User").setRequired(true));
            this.options = optionData;
        }

        protected void execute(SlashCommandEvent event) {
            OptionMapping optionUsername = event.getOption("username");
            String username = optionUsername.getAsString().toLowerCase();
            try {
                if (RecAPI.recAccountExists(username)) {
                    event.deferReply().queue();
                    InteractionHook hook = event.getHook();
                    if(JDBCUtilsLogs.dbUserExists(username)) {
                    try {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setAuthor(username, null, RecAPI.recProfileImage(username));
                        eb.setTitle("User Infraction History");
                        eb.setColor(new Color(34, 211, 211));
                        eb.setDescription(String.format("%s's history", username));
                        eb.addField("Displayname: ", RecAPI.recDisplayName(username), true);
                        eb.addField("Username:", username, true);
                        eb.addField("UserID: ", Integer.toString(RecAPI.recAccountID(username)), true);
                        eb.addField("Infraction 1: ", JDBCUtilsLogs.dbUserGetStrikeReason(username, 1), true);
                        eb.setTimestamp(Instant.now());
                            switch (JDBCUtilsLogs.dbUserGetStrikes(username)) {
                                case 1:
                                    hook.sendMessageEmbeds(eb.build()).setEphemeral(false).queue();
                                    break;
                                case 2:
                                    eb.addField("Infraction 2:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 2), true);
                                    hook.sendMessageEmbeds(eb.build()).setEphemeral(false).queue();
                                    break;
                                case 3:
                                    eb.addField("Infraction 2:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 2), true);
                                    eb.addField("Infraction 3", JDBCUtilsLogs.dbUserGetStrikeReason(username, 3), true);
                                    hook.sendMessageEmbeds(eb.build()).setEphemeral(false).queue();
                                    break;
                                default:
                                    hook.sendMessage("GameKuchen fucked up. Screenshot this and tell him to fix it. line 194 in LogCommand.java").setEphemeral(true).queue();
                                    break;
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        EmbedBuilder eb2 = new EmbedBuilder();
                        eb2.setAuthor(username, null, RecAPI.recProfileImage(username));
                        eb2.setTitle("User Infraction History");
                        eb2.setColor(new Color(34, 211, 211));
                        eb2.setDescription(String.format("%s's history\nThis user has no recent infractions", username));
                        eb2.addField("Displayname: ", RecAPI.recDisplayName(username), true);
                        eb2.addField("Username:", username, true);
                        eb2.addField("UserID: ", Integer.toString(RecAPI.recAccountID(username)), true);
                        eb2.setTimestamp(Instant.now());
                        hook.sendMessageEmbeds(eb2.build()).queue();
                    }
                }else {
                    event.reply("This user does not exist on Rec Room. Check the name again.").setEphemeral(true).queue();
                }
            } catch (IOException | InterruptedException e) {
                event.reply("Error! Contact GameKuchen!").queue();
                throw new RuntimeException(e);
            }
        }
    }

    private static class Ban extends SlashCommand {
        public Ban() {
            this.name = "ban";
            this.help = "Instantly send to Ban-log";
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "username", "The @ user name of the Rec Room User").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "reason", "The reason for the ban")
                    .addChoice("N-Word", "N-Word")
                    .addChoice("Raid", "Raid")
                    .addChoice("Hitler", "Hitler")
                    .addChoice("Homophobic", "Homphobic")
                    .addChoice("Racist", "Racist")
                    .addChoice("R-Word", "R-Word")
                    .addChoice("Underage", "Underage")
                    .addChoice("Hacker", "Hacker")
                    .addChoice("Dangerous Behaviour", "Dangerous Behaviour")
                    .addChoice("Extremely offensive", "Extremely offensive")
                    .setRequired(true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            OptionMapping optionUsername = event.getOption("username");
            OptionMapping optionReason = event.getOption("reason");
            String username = optionUsername.getAsString().toLowerCase();
            String reason = optionReason.getAsString();
            event.deferReply().setEphemeral(true).queue();
            InteractionHook hook = event.getHook();
                if (!RecAPI.recAccountExists(username)) {
                    event.reply("This user does not exist.").setEphemeral(true).queue();
                    return;
                }
            try {
                if (JDBCUtilsLogs.dbUserExists(username)) {
                    if (!JDBCUtilsLogs.dbUserGetIsBanned(username)) {
                        switch (JDBCUtilsLogs.dbUserGetStrikes(username)) {

                            case 1:
                                JDBCUtilsLogs.dbUserAddStrike(username, reason);
                                JDBCUtilsLogs.dbUserAddStrike(username, reason);
                                hook.sendMessage("Successfully send to ban-log").setEphemeral(true).queue();
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Pending Ban");
                                eb.addField("User: ", username, false);
                                eb.addField("Infraction 1:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 1), true);
                                eb.addField("Infraction 2:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 2), true);
                                eb.addField("Infraction 3:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 3), true);
                                eb.setDescription(String.format("Users ban is Pending. Insta ban by %s \nReason: %s", event.getMember().getUser().getName(), reason));
                                event.getGuild().getTextChannelById(GetPropertyValues.banLog).sendMessageEmbeds(eb.build()).queue(e -> e.addReaction(event.getGuild().getEmojiById("995242387561513040")).queue());
                                break;
                            case 2:
                                JDBCUtilsLogs.dbUserAddStrike(username, reason);
                                hook.sendMessage("Successfully send to ban-log").setEphemeral(true).queue();
                                EmbedBuilder eb2 = new EmbedBuilder();
                                eb2.setTitle("Pending Ban");
                                eb2.addField("User: ", username, false);
                                eb2.addField("Infraction 1:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 1), true);
                                eb2.addField("Infraction 2:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 2), true);
                                eb2.addField("Infraction 3:", JDBCUtilsLogs.dbUserGetStrikeReason(username, 3), true);
                                eb2.setDescription(String.format("Users ban is Pending. Insta ban by %s \nReason: %s", event.getMember().getUser().getName(), reason));
                                event.getGuild().getTextChannelById(GetPropertyValues.banLog).sendMessageEmbeds(eb2.build()).queue(e -> e.addReaction(event.getGuild().getEmojiById("995242387561513040")).queue());
                                break;
                            case 3:
                                hook.sendMessage("A ban for this person is already pending!").setEphemeral(true).queue();
                                break;
                            default:
                                hook.sendMessage("Yeah, idk, I quit here, something went wrong. Contact Gamekuchen and tell him to come back from getting milk.").queue();
                                break;
                        }


                    } else {
                        hook.sendMessage("This user is already banned!").queue();
                    }
                } else {
                    JDBCUtilsLogs.dbUserCreate(username, reason);
                    JDBCUtilsLogs.dbUserAddStrike(username, reason);
                    JDBCUtilsLogs.dbUserAddStrike(username, reason);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Pending Ban");
                    eb.addField("User: ", username, false);
                    eb.addField("Reason: ", reason, true);
                    eb.setDescription(String.format("Users ban is Pending. Insta ban by %s \nReason: %s", event.getMember().getUser().getName(), reason));
                    event.getGuild().getTextChannelById(GetPropertyValues.banLog).sendMessageEmbeds(eb.build()).queue(e -> e.addReaction(event.getGuild().getEmojiById("995242387561513040")).queue());
                    hook.sendMessage("Successfully send to ban-log").setEphemeral(true).queue();
                }
            } catch (IOException | ExecutionException | InterruptedException e) {
                hook.sendMessage("Error! Contact GameKuchen!").queue();
                throw new RuntimeException(e);
            }
        }
    }
}

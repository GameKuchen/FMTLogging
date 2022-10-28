package de.gamekuchen.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class EchoCommand extends SlashCommand {
    public EchoCommand(){
        this.name = "echo";
        this.help = "You are probably a user that shouldn't use this >:";
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.CHANNEL, "channel", "the channel to echo the message to"));
        options.add(new OptionData(OptionType.STRING, "message", "the message to be send"));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
       GuildChannel channel = event.optGuildChannel("channel");
       String message = event.optString("message");
       channel.getGuild().getTextChannelById(channel.getId()).sendMessage(message).queue();
       event.reply("Sent Message!").setEphemeral(true).queue();
    }


}

package de.gamekuchen.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

public class UptimeCommand extends SlashCommand {
    public UptimeCommand() {
        this.name = "uptime";
        this.help = "Prints in chat how long the bot has been online for";

    }
    @Override
    protected void execute(SlashCommandEvent event) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();
        long uptimeInSeconds = uptime / 1000;
        long numberOfHours = uptimeInSeconds / (60 * 60);
        long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours / 60);
        long numberOfSeconds = uptimeInSeconds % 60;

        event.reply(String.format("My Uptime is %s hours, %s minutes and %s seconds", numberOfHours, numberOfMinutes, numberOfSeconds)).queue();
    }


}

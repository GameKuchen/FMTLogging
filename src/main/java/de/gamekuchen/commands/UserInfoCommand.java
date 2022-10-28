/*
package de.gamekuchen.commands;


import com.github.ygimenez.exception.InvalidHandlerException;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import com.github.ygimenez.model.PaginatorBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.gamekuchen.FMTLogging;
import de.gamekuchen.utils.RecAPI;
import de.gamekuchen.utils.RecUserRecord;
import de.gamekuchen.utils.RecUserRecordList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class UserInfoCommand extends SlashCommand {
    public UserInfoCommand() {
        this.name = "getuserinfo";
        this.help = "Tries to get user by Displayname. May result in multiple entrys!";
        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.STRING, "rec-displayname", "The Displayname of the Rec-User", true));
        this.options = optionData;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        var username = event.optString("rec-displayname");
        event.deferReply(false).queue();
        var hook = event.getHook();
        var query = RecAPI.queryProfilesByDisplayName(username);
        if(query.isEmpty()){
            hook.sendMessage("I couldn't find any results with that displayname.").queue();
        }
        if(query.size() == 1){
            var eb = new EmbedBuilder();
            eb.setTitle("I found 1 Result");
            eb.setColor(new Color(53, 225, 53));
            eb.addField("@username", query.get(0).username, true);
            eb.addField("displayname", query.get(0).displayName, true);
            eb.addField("userID", String.valueOf(RecAPI.recAccountID(query.get(0).username)), true);
            eb.setTimestamp(Instant.now());
        }
        try {
            Pages.activate(PaginatorBuilder.createSimplePaginator(FMTLogging.jda));
            var pages = new ArrayList<Page>();
            var eb = new EmbedBuilder();
            var currentpage = new AtomicInteger();

            query.forEach(recUserRecord -> {
                currentpage.getAndIncrement();
                eb.clear();
                eb.setTitle(String.format("Found %s users", query.size()));
                eb.setDescription(String.format("Users %s", currentpage.get()));
                eb.addField("@username", recUserRecord.username, true);
                eb.addField("displayname", recUserRecord.displayName, true);
                eb.addField("userID", String.valueOf(RecAPI.recAccountID(recUserRecord.username)), true);
                pages.add(new InteractPage(eb.build()));
            });

            hook.sendMessage((MessageCreateData) pages.get(0).getContent()).queue(success -> {
                Pages.paginate(success.getMessageReference().getMessage(), pages, true);
            });

        } catch (InvalidHandlerException e) {
            throw new RuntimeException(e);
        }
    }
}
*/
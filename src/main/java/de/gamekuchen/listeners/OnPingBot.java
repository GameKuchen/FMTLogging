package de.gamekuchen.listeners;

import de.gamekuchen.FMTLogging;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnPingBot extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User bot = event.getJDA().getSelfUser();
        try {
            if(event.getMessage().getMentions().getUsers().contains(bot)){

                event.getMessage().addReaction(Emoji.fromUnicode("U+1F620")).queue();
            }
            //Small dirty workaround for current Chewtils bug
            //TODO: Remove when Chewtils is updated with bug fixed
        } catch (Exception e) {
            FMTLogging.logger.debug(e.getMessage());

        }

    }
}

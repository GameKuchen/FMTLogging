package de.gamekuchen.listeners;

import de.gamekuchen.utils.GetPropertyValues;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChangeStatus extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        switch (GetPropertyValues.botStatus) {
            case "PLAYING":
                event.getJDA().getPresence().setActivity(Activity.playing(GetPropertyValues.botStatusValue));
                break;
            case "STREAMING":
                if (GetPropertyValues.botStatusValue.contains("http")) {
                    if (Activity.isValidStreamingUrl(GetPropertyValues.botStatusValue)) {
                        event.getJDA().getPresence().setActivity(Activity.streaming(GetPropertyValues.botStatusValue, GetPropertyValues.botStatusValue));
                    } else {
                        System.out.printf("%n The String %s is not a Valid Streaming URL!", GetPropertyValues.botStatusValue);
                        System.exit(0);
                    }
                } else {
                    System.out.printf("%n The String %s doesn't contain http or https!", GetPropertyValues.botStatusValue);
                    System.exit(0);
                }
                break;
            case "WATCHING":
                event.getJDA().getPresence().setActivity(Activity.watching(GetPropertyValues.botStatusValue));
                break;
            case "COMPETING":
                event.getJDA().getPresence().setActivity(Activity.competing(GetPropertyValues.botStatusValue));
                break;
            case "LISTENING":
                event.getJDA().getPresence().setActivity(Activity.listening(GetPropertyValues.botStatusValue));
                break;
        }
    }
}

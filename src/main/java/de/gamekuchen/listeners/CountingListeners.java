package de.gamekuchen.listeners;

import kotlin.text.Charsets;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class CountingListeners extends ListenerAdapter {
    public static int curNumber;
    public List<Member> memberList = new ArrayList<>();
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.getChannel().getId().equals("740509447608074341")) return;
        TextChannel channel = event.getChannel().asTextChannel();
        String rawMessageContent = event.getMessage().getContentRaw();
        Member member = event.getMember();
        if(memberList.isEmpty()){
            memberList.add(member);
        } else {
            memberList.add(0, member);
            if(memberList.size() > 2) memberList.remove(2);
        }

        Member prevMember = null;
        if (memberList.size() == 2) {
            prevMember = memberList.get(1);
        }

        //validate number
        if(rawMessageContent.equals(String.valueOf(curNumber))) {
            var manager = channel.getManager();
            if (prevMember != null) {
                manager.removePermissionOverride(prevMember.getIdLong());
            }
            var memberPerms = member.getPermissionsExplicit();
            memberPerms.remove(Permission.MESSAGE_SEND);
            manager.putMemberPermissionOverride(member.getIdLong(), memberPerms, Collections.singletonList(Permission.MESSAGE_SEND));

            manager.queue();
            setCurNumber(curNumber = curNumber + 1);
        } else {
            event.getMessage().delete().queue();
        }

        //give them a role because lol

        //funny number reactions

        //think: life system
    }

    public static int getCurNumber() {
        return curNumber;
    }

    public static void setCurNumber(int curNumber) {
        try {
            File file = new File("./curNumberFile.txt");
            Files.write(file.toPath(), String.valueOf(curNumber).getBytes(Charsets.UTF_8), StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            CountingListeners.curNumber = curNumber;
        }
    }
}

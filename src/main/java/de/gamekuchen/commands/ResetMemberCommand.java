package de.gamekuchen.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.gamekuchen.utils.JDBCUtilsUsers;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class ResetMemberCommand extends SlashCommand {
    public ResetMemberCommand() {
        this.name = "resetmember";
        this.help = "Resets the members Discord Reward";
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "The user to reset"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        var user = event.optUser("user");
        var member = event.getGuild().getMember(user);
        var memberID = user.getIdLong();
        var hook = event.getHook();
        event.deferReply().setEphemeral(true).queue();

        try {
            if(JDBCUtilsUsers.dbUserExists(memberID)){
                JDBCUtilsUsers.dbUserDelete(memberID);
                event.getGuild().removeRoleFromMember(user, event.getGuild().getRolesByName("Verified", true).get(0)).queue();
                hook.sendMessage("Successfully reset their Discord Reward").queue();
            } else {
                hook.sendMessage("This members reward cannot be reset, they probably haven't claimed it yet").queue();
            }
        } catch (IOException e) {
            hook.sendMessage("Error! Contact GameKuchen!").queue();
            throw new RuntimeException(e);
        }
    }
}

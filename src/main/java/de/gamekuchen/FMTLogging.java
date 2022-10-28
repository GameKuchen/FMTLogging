package de.gamekuchen;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import de.gamekuchen.commands.*;
import de.gamekuchen.listeners.CountingListeners;
import de.gamekuchen.listeners.ReactListeners;
import de.gamekuchen.listeners.ChangeStatus;
import de.gamekuchen.listeners.OnPingBot;
import de.gamekuchen.utils.GetPropertyValues;
import de.gamekuchen.utils.RemoveLoop;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class FMTLogging {
    public static JDA jda;
    public static Logger logger = LoggerFactory.getLogger("de.gamekuchen.FMTLoggin");
    private static final EventWaiter waiter = new EventWaiter();
    public static void main(String[] args) throws IOException, LoginException {
        new FMTLogging().start();
    }

    public void start () throws IOException, LoginException {
        //Setup config reading with mode choosing
        var readConfigMode = new Scanner(System.in);
        System.out.println("Enter a mode (production or dev)");
        var configMode = readConfigMode.nextLine();
        GetPropertyValues.getPropValues(configMode);
        //Setup JDA
        var builder = JDABuilder.createDefault(GetPropertyValues.botToken);

        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ROLE_TAGS);
        builder.enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );

        var commandClient = new CommandClientBuilder();
        commandClient.forceGuildOnly(GetPropertyValues.guildID);
        commandClient.addSlashCommands(
                new LogCommand(),
                new UptimeCommand(),
                new EchoCommand(),
                new ClaimCommand(),
                new GenerateCodeCommand(),
                new CodeCommand(this),
                new ResetMemberCommand()
        );

        commandClient.setOwnerId(286015596875874305L);
        commandClient.setCoOwnerIds(286015596875874305L);

        builder.addEventListeners(commandClient.build(), new ReactListeners(), new ChangeStatus(), new OnPingBot(), new CountingListeners(), waiter);

        jda = builder.build();
        new RemoveLoop().loop();
        CountingListeners.setCurNumber(getCurNumberFromFile());
    }

    public static int getCurNumberFromFile() {
        File curNumberFile = Paths.get("./curNumberFile.txt").toFile();
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(curNumberFile));
            return Integer.parseInt(brTest.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public EventWaiter getEventWaiter()
    {
        return waiter;
    }
}

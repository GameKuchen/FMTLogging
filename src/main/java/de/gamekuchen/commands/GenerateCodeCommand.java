package de.gamekuchen.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateCodeCommand extends SlashCommand {
    public GenerateCodeCommand() {
        this.name = "generatecode";
        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.STRING, "code", "Code from User").setRequired(true));
        this.options = optionData;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(true).queue();
        var hook = event.getHook();
        String uuid = (Objects.requireNonNull(event.optString("code")));

        if(preChecks(hook, uuid)) return;

        var uuidParting = new ArrayList<>(List.of(uuid.split("-")));
        String redeemForCode = uuidParting.get(0);

        String[] redeemCodeLetters = redeemForCode.split("");

        int shortUUID = Integer.parseInt(uuidParting.get(1));
        int verifierPart1 = new Random().nextInt(9 - 2) + 2;
        int verifierPart2 = new Random().nextInt(9 - 2) + 2;

        AtomicReference<String> redeemForCyphered = new AtomicReference<>("");
        Arrays.stream(redeemCodeLetters).forEachOrdered(s -> {
            redeemForCyphered.set(redeemForCyphered.get() + moveUpTheAlphabetChar(s.charAt(0), verifierPart1));
        });

        int[] ourShortUUIDArray = Stream.of(String.valueOf(shortUUID).split(""))
                .mapToInt(Integer::parseInt)
                .toArray();
        AtomicReference<String> ourShortUUID = new AtomicReference<>("");
        if(String.valueOf(shortUUID).length() == 3) ourShortUUID.set(String.valueOf(moveDownNumber(0, verifierPart2)));
        Arrays.stream(ourShortUUIDArray).forEach(value -> {
            ourShortUUID.set(ourShortUUID.get() + moveDownNumber(value, verifierPart2));
        });

        hook.sendMessage(String.format("%s-%s-%s%s", redeemForCyphered.get(), ourShortUUID.get(), verifierPart1, verifierPart2)).queue();

    }

    private static boolean preChecks(InteractionHook hook, String uuid) {
        if(!uuid.matches("[A-Z; a-z]{0,256}\\b-[0-9]{4}\\b")) {
            hook.sendMessage("Wrong UUID").queue();
            return true;
        }
        return false;
    }

    public static char moveUpTheAlphabetChar(final char c, int i) {
        int offset, range;
        i--;
        if (c >= 'A' && c <= 'Z') {
            offset = 'A';
            range = 'Z' - 'A' + 1;
        } else if (c >= 'a' && c <= 'z') {
            offset = 'a';
            range = 'z' - 'a' + 1;
        } else {
            throw new InvalidParameterException("Invalid character: " + c);
        }
        int abs = c - offset;
        abs = (abs + i) % range;
        return (char) (abs + offset);
    }

    public static int moveDownNumber(int input, final int moveBy) {
        int range;
        range = 10;
        input = (input + moveBy);
        input--;
        if(input > 9)
            input = input % range;
        return (char) (input);
    }
}

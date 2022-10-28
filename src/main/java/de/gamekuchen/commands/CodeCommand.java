package de.gamekuchen.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.gamekuchen.FMTLogging;
import de.gamekuchen.utils.JDBCUtilsUsers;
import de.gamekuchen.utils.RecAPI;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodeCommand extends SlashCommand {
    private final FMTLogging bot;
    public CodeCommand (FMTLogging bot) {
        this.name = "claimreward";
        this.help = "Verifies you!";
        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.STRING, "rec-username", "Your RecRoom @username").setRequired(true));
        optionData.add(new OptionData(OptionType.STRING, "uuid", "UUID given by RecRoom").setRequired(true));
        this.options = optionData;
        this.bot = bot;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(true).queue();
        var hook = event.getHook();
        var decCode = new AtomicReference<>("");
        var guild = event.getGuild();
        var user = event.getUser();
        String uuid = (Objects.requireNonNull(event.optString("uuid")));
        var username = Objects.requireNonNull(event.optString("rec-username")).toLowerCase(Locale.ROOT);

        if (preChecks(hook, uuid, username, user.getIdLong())) return;

        int userLengthShort = getUserLengthShort(username);

        cypherUsername(decCode, username, userLengthShort);

        StringBuilder charUser = lastFourOfCypher(decCode);


        //get all values from user and verify stuff
        var uuidParting = new ArrayList<>(List.of(uuid.split("-")));

        int recDecCodeFromUser = Integer.parseInt(uuidParting.get(0));
        int recVerifierFromUser = Integer.parseInt(uuidParting.get(1));
        int decCodePlusVerifierFromUser = Integer.parseInt(uuidParting.get(2));

        int ourVerifier = decCodePlusVerifierFromUser - Integer.parseInt(charUser.toString()) ;

        if(verifyingCodesAndUsername(charUser, recDecCodeFromUser, recVerifierFromUser, decCodePlusVerifierFromUser, ourVerifier)) {
            int savedRandomTheFirst = new Random().nextInt(9 - 2) + 2;
            int savedRandomTheSecond = new Random().nextInt(9 - 2) + 2;
            char cypher = moveUpTheAlphabetChar('d', savedRandomTheFirst);
            int[] ourVerifierNumberArray = Stream.of(String.valueOf(ourVerifier).split(""))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            List<Integer> ourVerifierNumberArrayCyphered = cypherMore(savedRandomTheSecond, ourVerifierNumberArray);

            var cypheredArrayToString = new AtomicReference<>("");;
            ourVerifierNumberArrayCyphered.forEach(s -> cypheredArrayToString.set(cypheredArrayToString.get() + s));

            String result = String.format("%s-%s-%s%s", cypher, cypheredArrayToString.get(), savedRandomTheFirst, savedRandomTheSecond);
            AtomicReference<PrivateChannel> pc = new AtomicReference<>();
            event.getUser().openPrivateChannel()
                    .flatMap(privateChannel -> {
                        pc.set(privateChannel);
                       var message = privateChannel.sendMessage(String.format("Are you sure you want to link RR:@%s to your Discord account? Y/N\nThis action cannot be undone!!", username));
                       return message;
                    }).queue(message -> hook.sendMessage("Please look into your Direct Messsages.").queue(), new ErrorHandler()
                            .handle(
                                    ErrorResponse.CANNOT_SEND_TO_USER,
                                    (e -> hook.sendMessage("Failed to send message, you block private messages!").queue())
                            ));

            bot.getEventWaiter().waitForEvent(MessageReceivedEvent.class,
                    e-> e.getChannel().getId().equals(pc.get().getId()) && !e.getAuthor().isBot(),
                    e-> {
                        var authorMessage = e.getMessage().getContentRaw();
                        if(authorMessage.equalsIgnoreCase("y") || authorMessage.equalsIgnoreCase("yes")){
                            pc.get().sendMessage(String.format("Your code is: %s", result)).queue();
                            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName("Verified", true).get(0)).queue();
                            try {
                                JDBCUtilsUsers.dbUserCreate(user.getIdLong(), RecAPI.recAccountID(username));
                            } catch (IOException ex) {
                                hook.sendMessage("Error! Contact Support!\nError Code: CC:85:77").queue();
                                throw new RuntimeException(ex);
                            }
                        } else {
                            pc.get().sendMessage("Dayum bruh").queue();
                        }
                    }, 2, TimeUnit.MINUTES, () -> pc.get().sendMessage("You didn't respond in time!").queue()
            );
        }else {
            hook.sendMessage("The UUID is wrong! Please try again.").queue();
        }
    }

    private static int getUserLengthShort(String username) {
        var userLengthString = String.valueOf(username.length());
        var userLengthShort = username.length();
        if(username.length() >= 10){
            userLengthShort = Integer.parseInt(toCharacterStringListWithCodePoints(userLengthString).get(1));
        }
        return userLengthShort;
    }

    public static void cypherUsername(AtomicReference<String> decCode, String username, int userLengthShort) {
        toCharacterStringListWithCodePoints(username).forEach(s -> {
            switch(s) {
                case "`":
                case "r":
                case "6":
                case "f":
                case "c":
                case "@":
                case "+":
                case ">":
                    if(userLengthShort + 7 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 7));
                    } else{
                        decCode.set(decCode.get() + "7");
                    }
                    break;
                case "1":
                case "0":
                case "i":
                case "k":
                case "m":
                case "^":
                case "\\":
                    if(userLengthShort + 1 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 1));
                    }else {
                        decCode.set(decCode.get() + "1");
                    }
                    break;
                case "2":
                case "y":
                case "b":
                case "$":
                case "}":
                case "h":
                    if(userLengthShort + 9 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 9));
                    } else {
                        decCode.set(decCode.get() + "9");
                    }
                    break;
                case "3":
                case "-":
                case "o":
                case "l":
                case "n":
                case ",":
                case "4":
                case "&":
                    if(userLengthShort + 2 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 2));
                    }else {
                        decCode.set(decCode.get() + "2");
                    }
                    break;
                case "t":
                case "]":
                case "g":
                case "v":
                case "{":
                case "#":
                case "/":
                    if(userLengthShort + 8 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 8));
                    }else {
                        decCode.set(decCode.get() + "8");
                    }
                    break;
                case "5":
                case "=":
                case "p":
                case ";":
                case ".":
                case "%":
                case "*":
                case "|":
                    if(userLengthShort + 3 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 3));
                    }else {
                        decCode.set(decCode.get() + "3");
                    }
                    break;
                case "7":
                case "q":
                case "a":
                case "j":
                case "?":
                case "(":
                case ":":
                case "'":
                    if(userLengthShort + 4 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 4));
                    }else {
                        decCode.set(decCode.get() + "4");
                    }
                    break;
                case "e":
                case "8":
                case "d":
                case "x":
                case "!":
                case "_":
                case "<":
                case "[":
                    if(userLengthShort + 6 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 6));
                    }else {
                        decCode.set(decCode.get() + "6");
                    }
                    break;
                case "9":
                case "w":
                case "u":
                case "s":
                case "z":
                case "~":
                case ")":
                case "\"":
                    if(userLengthShort + 5 < 10){
                        decCode.set(decCode.get() + (userLengthShort + 5));
                    }else {
                        decCode.set(decCode.get() + "5");
                    }
                    break;

            }
        });
    }

    public static boolean preChecks(InteractionHook hook, String uuid, String username, Long userID) {
        if(!RecAPI.recAccountExists(username)){
            hook.sendMessage("This User does not exist. \nPlease make sure you have used your username not your Rec-room Displayname.").setEphemeral(true).queue();
            return true;
        }
        if(!uuid.matches("[0-9]{4}\\b-[0-9]{4}\\b-[0-9]{5}\\b")) {
            hook.sendMessage("Wrong UUID").queue();
            return true;
        }
        try {
            if(JDBCUtilsUsers.dbUserExists(userID)) {
                hook.sendMessage("You already claimed your reward!").queue();
                return true;
            }
        } catch (IOException e) {
            hook.sendMessage("Error! Contact Support!\nError Code: CC:247:75").queue();
            throw new RuntimeException(e);
        }
        return false;
    }

    @NotNull
    public static List<Integer> cypherMore(int savedRandomTheSecond, int[] ourVerifierNumberArray) {
        List<Integer> ourVerifierNumberArrayCyphered = new ArrayList<>();
        for (int i: ourVerifierNumberArray) {
            ourVerifierNumberArrayCyphered.add(moveDownNumber(i, savedRandomTheSecond));
        }
        return ourVerifierNumberArrayCyphered;
    }


    public static boolean verifyingCodesAndUsername(StringBuilder charUser, int recDecCodeFromUser, int recVerifierFromUser, int decCodePlusVerifierFromUser, int ourVerifier) {
        return recDecCodeFromUser == Integer.parseInt(charUser.toString()) && decCodePlusVerifierFromUser - ourVerifier == decCodePlusVerifierFromUser - recVerifierFromUser && recVerifierFromUser == ourVerifier;
    }

    @NotNull
    public static StringBuilder lastFourOfCypher(AtomicReference<String> decCode) {
        List<String> decCodeStringList = toCharacterStringListWithCodePoints(decCode.get());
        var index = decCodeStringList.size() - 4;
        var charUser = new StringBuilder();
        for (int i = 0; i <4 ; i++) {
            charUser.append(decCodeStringList.get(index + i));
        }
        return charUser;
    }

    public static List<String> toCharacterStringListWithCodePoints(String str) {
        if (str == null) {
            return Collections.emptyList();
        }
        return str.codePoints()
                .mapToObj(Character::toString)
                .collect(Collectors.toList());
    }

    public static int moveUpNumber(int input, final int moveBy) {
        input = (input - moveBy);
        input++;
        if(input < 0)
            input = input + 10;

        return (input);
    }
    public static int moveDownNumber(int input, final int moveBy) {
        int range;
        range = 10;
        input = (input + moveBy);
        input--;
        if(input > 9)
            input = input % range;

        return (input);
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

    public static char moveDownChar(final char input, int moveBy) {
        int offset, range;
        if (input >= 'A' && input <= 'Z') {
            offset = 'A';
            range = 'Z' - 'A' + 1;
        } else if (input >= 'a' && input <= 'z') {
            offset = 'a';
            range = 'z' - 'a' + 1;
        } else {
            throw new InvalidParameterException("Invalid character: " + input);
        }

        moveBy = 25 - moveBy;
        int abs = input - offset;
        abs = (abs + moveBy) % range;
        return (char) (abs + offset);
    }
}

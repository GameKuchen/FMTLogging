package de.gamekuchen.utils;

import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VerifierClass {

    public static boolean verify(String recUsername, String uuid) {
        var decCode = new AtomicReference<>("");
        int userLengthShort = getUserLengthShort(recUsername);

        cypherUsername(decCode, recUsername, userLengthShort);

        StringBuilder charUser = lastFourOfCypher(decCode);


        //get all values from user and verify stuff
        var uuidParting = new ArrayList<>(List.of(uuid.split("-")));

        int recDecCodeFromUser = Integer.parseInt(uuidParting.get(0));
        int recVerifierFromUser = Integer.parseInt(uuidParting.get(1));
        int decCodePlusVerifierFromUser = Integer.parseInt(uuidParting.get(2));

        int ourVerifier = decCodePlusVerifierFromUser - Integer.parseInt(charUser.toString()) ;

        return verifyingCodesAndUsername(charUser, recDecCodeFromUser, recVerifierFromUser, decCodePlusVerifierFromUser, ourVerifier);


    }

    public static int uncypherMiddle(String uuid) {
        var uuidParting = new ArrayList<>(List.of(uuid.split("-")));

        int middle = Integer.parseInt(uuidParting.get(1));
        int last = Integer.parseInt(uuidParting.get(2));
        String verifierString = String.valueOf(last);
        String[] verifierParts = verifierString.split("");
        int verifierPart1 = Integer.parseInt(Arrays.stream(verifierParts).collect(Collectors.toList()).get(0));
        int verifierPart2 = Integer.parseInt(Arrays.stream(verifierParts).collect(Collectors.toList()).get(1));
        int[] ourShortUUIDArray = Stream.of(String.valueOf(middle).split(""))
                .mapToInt(Integer::parseInt)
                .toArray();

        AtomicReference<String> ourShortUUID = new AtomicReference<>("");
        if(String.valueOf(middle).length() == 3) ourShortUUID.set(String.valueOf(moveUpNumber(0, verifierPart2)));
        Arrays.stream(ourShortUUIDArray).forEach(value -> {
            ourShortUUID.set(ourShortUUID.get() + moveUpNumber(value, verifierPart2));
        });


        return Integer.parseInt(ourShortUUID.get());


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

    public static boolean preChecks(InteractionHook hook, String uuid, String username) {
        if(!RecAPI.recAccountExists(username)){
            hook.sendMessage("This User does not exist. \nPlease make sure you have used your username not your Rec-room Displayname.").setEphemeral(true).queue();
            return true;
        }
        if(!uuid.matches("[0-9]{4}\\b-[0-9]{4}\\b-[0-9]{5}\\b")) {
            hook.sendMessage("Wrong UUID").queue();
            return true;
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

        return (char) (input);
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

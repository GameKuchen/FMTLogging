package de.gamekuchen.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.gamekuchen.utils.JDBCUtilsUsers;
import de.gamekuchen.utils.RecAPI;
import de.gamekuchen.utils.VerifierClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClaimCommand extends SlashCommand {
    public ClaimCommand() {
        this.name = "setrank";
        this.help = "Will assign you a role on Discord with your in-game rank";
        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.STRING, "code", "Code from RecRoom").setRequired(true));
        optionData.add(new OptionData(OptionType.STRING, "uuid", "UUID given by RecRoom").setRequired(true));
        this.options = optionData;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        Member user = event.getMember();
        event.deferReply(true).queue();
        var hook = event.getHook();
        String uuid = (Objects.requireNonNull(event.optString("code")));
        String uuid1 = (Objects.requireNonNull(event.optString("uuid")));

        if(preChecks(hook, uuid, user.getIdLong())) return;

        var uuidParting = new ArrayList<>(List.of(uuid.split("-")));
        String redeemForCode = uuidParting.get(0);

        String[] redeemCodeLetters = redeemForCode.split("");



        int shortUUID = Integer.parseInt(uuidParting.get(1));
        int verifier = Integer.parseInt(uuidParting.get(2));
        String verifierString = String.valueOf(verifier);
        String[] verifierParts = verifierString.split("");
        int verifierPart1 = Integer.parseInt(Arrays.stream(verifierParts).collect(Collectors.toList()).get(0));
        int verifierPart2 = Integer.parseInt(Arrays.stream(verifierParts).collect(Collectors.toList()).get(1));
        var uuid1Parting = Integer.valueOf(new ArrayList<>(List.of(uuid1.split("-"))).get(1));

        AtomicReference<String> redeemForUncyphered = new AtomicReference<>("");
        Arrays.stream(redeemCodeLetters).forEachOrdered(s -> {

            redeemForUncyphered.set(redeemForUncyphered.get() + moveDownChar(s.charAt(0), verifierPart1));
        });
        var redeemForUncypheredTurnAround = Arrays.stream(redeemForUncyphered.get().split("")).collect(Collectors.toList());
        var shallowCopy = redeemForUncypheredTurnAround.subList(0, redeemForUncypheredTurnAround.size());
        Collections.reverse(shallowCopy);
        redeemForUncyphered.set("");
        shallowCopy.forEach(s->redeemForUncyphered.set(redeemForUncyphered.get() + s));

        int[] ourShortUUIDArray = Stream.of(String.valueOf(shortUUID).split(""))
                .mapToInt(Integer::parseInt)
                .toArray();
        AtomicReference<String> ourShortUUID = new AtomicReference<>("");
        if(String.valueOf(shortUUID).length() == 3) ourShortUUID.set(String.valueOf(moveUpNumber(0, verifierPart2)));
        Arrays.stream(ourShortUUIDArray).forEach(value -> {
            ourShortUUID.set(ourShortUUID.get() + moveUpNumber(value, verifierPart2));
        });

        String recUsername = null;
        try {
            recUsername = RecAPI.recUserName(JDBCUtilsUsers.dbUserGetRecID(event.getUser().getIdLong())).toLowerCase();
        } catch (IOException e) {
            hook.sendMessage("Error! Contact Support!\nError Code: CC2:75:73").queue();
            throw new RuntimeException(e);
        }
        if(!VerifierClass.verify(recUsername, uuid1) || uuid1Parting != VerifierClass.uncypherMiddle(uuid)) {

            hook.sendMessage("You picked the wrong house fool!").queue();
            return;
        }
        removeOldRole(guild, user);
        switch (redeemForUncyphered.get()) {
            case "enone":
                guild.addRoleToMember(user, guild.getRolesByName("EN-1 Private", false).get(0)).queue();
                break;
            case "entwo":
                guild.addRoleToMember(user, guild.getRolesByName("EN-2 Private 2nd Class", false).get(0)).queue();
                break;
            case "enthree":
                guild.addRoleToMember(user, guild.getRolesByName("EN-3 Private 1st Class", false).get(0)).queue();
                break;
            case "enfour":
                guild.addRoleToMember(user, guild.getRolesByName("EN-4 Corporal", false).get(0)).queue();
                break;
            case "enfive":
                guild.addRoleToMember(user, guild.getRolesByName("EN-5 Field Corporal", false).get(0)).queue();
                break;
            case "ensix":
                guild.addRoleToMember(user, guild.getRolesByName("EN-6 Lance Corporal", false).get(0)).queue();
                break;
            case "enseven":
                guild.addRoleToMember(user, guild.getRolesByName("EN-7 Specialist", false).get(0)).queue();
                break;
            case "eneight":
                guild.addRoleToMember(user, guild.getRolesByName("EN-8 Field Specialist", false).get(0)).queue();
                break;
            case "ennine":
                guild.addRoleToMember(user, guild.getRolesByName("EN-9 Sergeant", false).get(0)).queue();
                break;
            case "enten":
                guild.addRoleToMember(user, guild.getRolesByName("EN-10 Gunnery Sergeant", false).get(0)).queue();
                break;
            case "eneleven":
                guild.addRoleToMember(user, guild.getRolesByName("EN-11 Master Sergeant", false).get(0)).queue();
                break;
            case "entwelve":
                guild.addRoleToMember(user, guild.getRolesByName("EN-12 Commanding Sergeant", false).get(0)).queue();
                break;
            case "woone":
                guild.addRoleToMember(user, guild.getRolesByName("WO-1 Warrant Officer 1", false).get(0)).queue();
                break;
            case "wotwo":
                guild.addRoleToMember(user, guild.getRolesByName("WO-2 Warrant Officer 2", false).get(0)).queue();
                break;
            case "wothree":
                guild.addRoleToMember(user, guild.getRolesByName("WO-3 Sergeant Chief", false).get(0)).queue();
                break;
            case "wofour":
                guild.addRoleToMember(user, guild.getRolesByName("WO-4 Chief", false).get(0)).queue();
                break;
            case "wofive":
                guild.addRoleToMember(user, guild.getRolesByName("WO-5 Senior Chief", false).get(0)).queue();
                break;
            case "wosix":
                guild.addRoleToMember(user, guild.getRolesByName("WO-6 Master Chief", false).get(0)).queue();
                break;
            case "ofone":
                guild.addRoleToMember(user, guild.getRolesByName("OF-1 1st Lieutenant", false).get(0)).queue();
                break;
            case "oftwo":
                guild.addRoleToMember(user, guild.getRolesByName("OF-2 2nd Lieutenant", false).get(0)).queue();
                break;
            case "ofthree":
                guild.addRoleToMember(user, guild.getRolesByName("OF-3 Brigadier Lieutenant", false).get(0)).queue();
                break;
            case "offour":
                guild.addRoleToMember(user, guild.getRolesByName("OF-4 Major Lieutenant", false).get(0)).queue();
                break;
            case "offive":
                guild.addRoleToMember(user, guild.getRolesByName("OF-5 Senior Lieutenant", false).get(0)).queue();
                break;
            case "ofsix":
                guild.addRoleToMember(user, guild.getRolesByName("OF-6 Major Captain", false).get(0)).queue();
                break;
            case "ofseven":
                guild.addRoleToMember(user, guild.getRolesByName("OF-7 Captain", false).get(0)).queue();
                break;
            case "ofeight":
                guild.addRoleToMember(user, guild.getRolesByName("OF-8 Senior Captain", false).get(0)).queue();
                break;
            case "ofnine":
                guild.addRoleToMember(user, guild.getRolesByName("OF-9 Major", false).get(0));
                break;
            case "hcone":
                guild.addRoleToMember(user, guild.getRolesByName("HC-1 Lieutenant Commander", false).get(0)).queue();
                break;
            case "hctwo":
                guild.addRoleToMember(user, guild.getRolesByName("HC-2 Commander", false).get(0)).queue();
                break;
            case "hcthree":
                guild.addRoleToMember(user, guild.getRolesByName("HC-3 Field Commander", false).get(0)).queue();
                break;
            case "hcfour":
                guild.addRoleToMember(user, guild.getRolesByName("HC-4 Marshall Commander", false).get(0)).queue();
                break;
            case "hcfive":
                guild.addRoleToMember(user, guild.getRolesByName("HC-5 Major General", false).get(0)).queue();
                break;
            case "hcsix":
                guild.addRoleToMember(user, guild.getRolesByName("HC-6 Director", false).get(0)).queue();
                break;
            case "hcseven":
                guild.addRoleToMember(user, guild.getRolesByName("HC-7 Overseer", false).get(0)).queue();
                break;
            case "hceight":
                guild.addRoleToMember(user, guild.getRolesByName("HC-8 Overseer Lieutenant", false).get(0)).queue();
                break;
            case "hcnine":
                guild.addRoleToMember(user, guild.getRolesByName("HC-9 Facility Overseer", false).get(0)).queue();
                break;
            case "hcten":
                guild.addRoleToMember(user, guild.getRolesByName("HC-10 Directing Overseer", false).get(0)).queue();
                break;
            case "hceleven":
                guild.addRoleToMember(user, guild.getRolesByName("HC-11 Overseeing Director", false).get(0)).queue();
                break;
            case "hctwelve":
                guild.addRoleToMember(user, guild.getRolesByName("HC-12 Directing Director", false).get(0)).queue();
                break;
            case "kone":
                guild.addRoleToMember(user, guild.getRolesByName("K-1 Lieutenant General", false).get(0)).queue();
                break;
            case "ktwo":
                guild.addRoleToMember(user, guild.getRolesByName("K-2 Commander General", false).get(0)).queue();
                break;
            case "kthree":
                guild.addRoleToMember(user, guild.getRolesByName("K-3 Senior General", false).get(0)).queue();
                break;
            case "kfour":
                guild.addRoleToMember(user, guild.getRolesByName("K-4 General Major", false).get(0)).queue();
                break;
            case "kfive":
                guild.addRoleToMember(user, guild.getRolesByName("K-5 General", false).get(0)).queue();
                break;
            case "ksix":
                guild.addRoleToMember(user, guild.getRolesByName("K-6 Directing General", false).get(0)).queue();
                break;
            case "kseven":
                guild.addRoleToMember(user, guild.getRolesByName("K-7 President", false).get(0)).queue();
                break;
            case "keight":
                guild.addRoleToMember(user, guild.getRolesByName("K-8 President General", false).get(0)).queue();
                break;
            case "knine":
                guild.addRoleToMember(user, guild.getRolesByName("K-9 Lieutenant General", false).get(0)).queue();
                break;
            default:
                hook.sendMessage("Error! Contact Support!\nError Code: CC2:228:81").queue();
                break;
        }

        hook.sendMessage("Assigned you your rank!").queue();

    }

    private static void removeOldRole(Guild guild, Member user) {
        List<Role> ngFuckYou = new ArrayList<>();
        ngFuckYou.add(guild.getRolesByName("EN-1 Private", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-2 Private 2nd Class", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-3 Private 1st Class", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-4 Corporal", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-5 Field Corporal", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-6 Lance Corporal", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-7 Specialist", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-8 Field Specialist", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-9 Sergeant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-10 Gunnery Sergeant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-11 Master Sergeant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("EN-12 Commanding Sergeant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("WO-1 Warrant Officer 1", true).get(0));
        ngFuckYou.add(guild.getRolesByName("WO-2 Warrant Officer 2", true).get(0));
        ngFuckYou.add(guild.getRolesByName("WO-3 Sergeant Chief", true).get(0));
        ngFuckYou.add(guild.getRolesByName("WO-4 Chief", true).get(0));
        ngFuckYou.add(guild.getRolesByName("WO-5 Senior Chief", true).get(0));
        ngFuckYou.add(guild.getRolesByName("WO-6 Master Chief", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-1 1st Lieutenant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-2 2nd Lieutenant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-3 Brigadier Lieutenant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-4 Major Lieutenant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-5 Senior Lieutenant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-6 Major Captain", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-7 Captain", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-8 Senior Captain", true).get(0));
        ngFuckYou.add(guild.getRolesByName("OF-9 Major", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-1 Lieutenant Commander", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-2 Commander", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-3 Field Commander", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-4 Marshall Commander", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-5 Major General", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-6 Director", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-7 Overseer", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-8 Overseer Lieutenant", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-9 Facility Overseer", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-10 Directing Overseer", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-11 Overseeing Director", true).get(0));
        ngFuckYou.add(guild.getRolesByName("HC-12 Directing Director", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-1 Lieutenant General", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-2 Commander General", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-3 Senior General", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-4 General Major", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-5 General", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-6 Directing General", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-7 President", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-8 President General", true).get(0));
        ngFuckYou.add(guild.getRolesByName("K-9 Lieutenant General", true).get(0));

        ngFuckYou.forEach(s-> {
            if(user.getRoles().contains(s)) {
                guild.removeRoleFromMember(user, s).queue();
            }
        });
    }

    private static boolean preChecks(InteractionHook hook, String uuid, Long userID) {
        if(!uuid.matches("[A-Z; a-z]{0,256}\\b-[0-9]{4}\\b-[0-9]{2}\\b")) {
            hook.sendMessage("Wrong UUID").queue();
            return true;
        }
        try {
            if(!JDBCUtilsUsers.dbUserExists(userID)){
                hook.sendMessage("Please verify first with the /claimreward command!").queue();
                return true;
            }
        } catch (IOException e) {
            hook.sendMessage("Error! Contact Support!\nError Code: CC2:247:76").queue();
            throw new RuntimeException(e);
        }

        return false;
    }

    public static char moveDownChar(final char input, int moveBy) {
        int offset, range;
        moveBy = moveBy - 2;
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

    public static int moveUpNumber(int input, final int moveBy) {
        input = (input - moveBy);
        input++;
        if(input < 0)
            input = input + 10;

        return (input);
    }

}

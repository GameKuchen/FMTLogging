package de.gamekuchen.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class GetPropertyValues {
    static String result = "";
    static InputStream inputStream;
    public static String botToken;
    public static String botName;
    public static String botStatus;
    public static String botStatusValue;
    public static String kickLog;
    public static String banLog;
    public static String guildID;
    public static String roomModID;
    public static String seniorModID;
    public static String hostID;
    public static String seniorHostID;
    public static String ownerID;

    public static void getPropValues(String mode) throws IOException {
        if(mode.equals("production")) {
            mode = "production_config.properties";
        }else{
            mode = "dev_config.properties";
        }

        try {
            Properties prop = new Properties();
            inputStream = GetPropertyValues.class.getClassLoader().getResourceAsStream(mode);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + mode + "' not found in the classpath");
            }

            Date time = new Date(System.currentTimeMillis());

            botToken = prop.getProperty("botToken");
            botName = prop.getProperty("botName");
            botStatus = prop.getProperty("botStatus");
            botStatusValue = prop.getProperty("botStatusValue");
            kickLog = prop.getProperty("kickLog");
            banLog = prop.getProperty("banLog");
            guildID = prop.getProperty("guildID");
            roomModID = prop.getProperty("roomModID");
            seniorModID = prop.getProperty("seniorModID");
            hostID = prop.getProperty("hostID");
            seniorHostID = prop.getProperty("seniorHostID");
            ownerID = prop.getProperty("ownerID");
            System.out.printf("Bot was ran at %s!%n", time);
        } catch (Exception e) {
            System.out.printf("Exception: %s", e);
        } finally {
            inputStream.close();
        }
    }
}

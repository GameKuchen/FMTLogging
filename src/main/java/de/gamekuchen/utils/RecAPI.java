package de.gamekuchen.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gamekuchen.FMTLogging;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecAPI {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String recDisplayName(String playerAtName) throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format("https://accounts.rec.net/account?username=%s", playerAtName))
                .build(); // defaults to GET

        Response response = client.newCall(request).execute();

        RecUserRecord recUserRecord = mapper.readValue(response.body().byteStream(), RecUserRecord.class);
        response.close();
        return recUserRecord.displayName;
    }

    public static String recUserName(long recID) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format("https://accounts.rec.net/account/%s", recID))
                .build(); // defaults to GET

        Response response = null;
        RecUserRecord recUserRecord = null;
        try {
            response = client.newCall(request).execute();

            recUserRecord = mapper.readValue(response.body().byteStream(), RecUserRecord.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        response.close();

        return recUserRecord.username;
    }

    public static int recAccountID(String playerAtName) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format("https://accounts.rec.net/account?username=%s", playerAtName))
                .build(); // defaults to GET

        try (Response response = client.newCall(request).execute();){
            var recUserRecord = mapper.readValue(response.body().byteStream(), RecUserRecord.class);
            response.close();

            return recUserRecord.accountId;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean recAccountExists(String playerAtName) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format("https://accounts.rec.net/account?username=%s", playerAtName))
                .build(); // defaults to GET

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(response.code() == 200){
            boolean successful = response.isSuccessful();
            response.close();
            return successful;
        }else if (response.code() == 404){
            response.close();
            return false;
        }else {
            FMTLogging.logger.error("RecRoomApi returned weird StatusCode");
            FMTLogging.logger.error(String.format("Returned StatusCode: %s", response.code()));
            response.close();
            return false;
        }
    }

    public static String recProfileImage(String playerAtName) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format("https://accounts.rec.net/account?username=%s", playerAtName))
                .build(); // defaults to GET
        try {
            Response response = client.newCall(request).execute();
            RecUserRecord recUserRecord = mapper.readValue(response.body().byteStream(), RecUserRecord.class);
            response.close();
           return String.format("https://img.rec.net/%s", recUserRecord.profileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "https://miro.medium.com/max/1838/1*cLQUX8jM2bMdwMcV2yXWYA.jpeg";
    }

    public static RecUserRecordList queryProfilesByDisplayName (String displayName) {
        OkHttpClient client = new OkHttpClient();
        var request = new Request.Builder()
                .url(String.format("https://accounts.rec.net/account/search?name=%s", displayName))
                .build();
        try(var response = client.newCall(request).execute()) {
            RecUserRecordList recUserRecord = mapper.readValue(response.body().byteStream(), RecUserRecordList.class);
            response.close();


            return recUserRecord;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

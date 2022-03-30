package com.cris1343.lcoapitest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Http {
    public static String apiKey = "";

    public static JSONObject get(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    url + (url.contains("?") ? "&" : "?") + "format=json").openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("Authorization", "Token " + apiKey);
            con.addRequestProperty("Content-Type", "application/json");
            con.setReadTimeout(60000);

            Utility.log("Connecting to \"" + con.getURL().toString() + "\"");
            con.connect();

            int response = con.getResponseCode();

            if (response == -1) {
                Utility.log("ERROR");
                Utility.log("No response message");
                return null;
            }

            Utility.log("Returned " + response + " " + con.getResponseMessage());

            InputStream input;
            if (con.getErrorStream() == null)
                input = con.getInputStream();
            else
                input = con.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            String text = reader.lines().collect(Collectors.joining());

            Utility.log("JSON:");
            Utility.log(text);

            return new JSONObject(text);
        } catch (Exception e) {
            Utility.log("ERROR");
            Utility.log("Returned exception: " + e.getClass().getSimpleName());
            return null;
        }
    }
}

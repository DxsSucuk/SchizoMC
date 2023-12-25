package de.presti.schizomc.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.presti.schizomc.SchizoMC;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestUtility {

    static HttpClient httpClient = HttpClient.newHttpClient();
    static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.52 Safari/537.36 Schizophrenia/WithYourMom";

    private static InputStream GET(String url) {
        try {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URL(url).toURI())
                .header("User-Agent", USER_AGENT)
                .build();

            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream()).body();
        } catch (Exception exception) {
            SchizoMC.getInstance().getLogger().warning("Error while sending GET request to " + url + "\nException: " + exception.getMessage());
        }

        return null;
    }

    public static JsonElement getJSON(String url) {
        try (InputStream inputStream = GET(url)) {
            if (inputStream == null) return new JsonObject();

            return new JsonParser().parse(new InputStreamReader(inputStream));
        } catch (Exception exception) {
            SchizoMC.getInstance().getLogger().warning("Error while getting JSON from " + url + "\nException: " + exception.getMessage());
        }

        return new JsonObject();
    }
}
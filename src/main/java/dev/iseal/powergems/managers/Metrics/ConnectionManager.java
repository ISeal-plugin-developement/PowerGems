package dev.iseal.powergems.managers.Metrics;

import dev.iseal.powergems.misc.ExceptionHandler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class ConnectionManager {

    private String token = "-1";

    private static ConnectionManager instance;
    public static ConnectionManager getInstance() {
        if (instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    public void sendData(String endpoint, String payload) {
        if (Objects.equals(token, "-1"))
            authenticate();
        initConnection(endpoint, "POST", payload);
    }

    private void authenticate() {
        // The endpoint to authenticate the user
        String endpoint = "auth/getID/";
        token = initConnection(endpoint, "GET", "");
    }

    private String initConnection(String endpoint, String method, String payload) {
        try {
            // Creating a URL object
            HttpURLConnection connection = getHttpURLConnection(endpoint, method, payload);

            // Retrieving the response code
            int responseCode = connection.getResponseCode();

            Bukkit.getLogger().info("Connected!");

            // Processing the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                ExceptionHandler.getInstance().dealWithException(new IOException("The API returned a non-200 result. Check any updates on the discord if it is down"), Level.WARNING, "API_ERROR_CODE_"+responseCode);
            }
        } catch (Exception e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "API_CONN_FAILED");
        }
        return null;
    }

    private @NotNull HttpURLConnection getHttpURLConnection(String endpoint, String method, String payload) throws IOException {
        URL url = new URL("https://analytics.iseal.dev/api/v1/" + endpoint);

        Bukkit.getLogger().info("Connecting to API, this could take a few seconds if your internet is slow!");

        // Opening a connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Setting the timeout - 3 seconds so if server goes down again it doesn't take too long
        connection.setConnectTimeout(3000);

        // Setting the request method
        connection.setRequestMethod(method);

        if (!token.equals("-1"))
            // Adding token
            connection.setRequestProperty("Authorization", token);

        if (method.equals("POST"))
            // Setting the content type
            connection.setRequestProperty("Content-Type", "application/json");

        if (!payload.equals("")) {
            // Allowing output
            connection.setDoOutput(true);
            // Send request body
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);
            out.flush(); // Ensure all data is sent
            out.close(); // Close the stream
        }
        return connection;
    }

    public void invalidateToken() {
        token = "-1";
    }
}

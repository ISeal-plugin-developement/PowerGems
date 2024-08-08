package dev.iseal.powergems.managers.Metrics;

import com.google.gson.Gson;
import dev.iseal.powergems.misc.ExceptionHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
        if (token.equals("-1"))
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
            URL url = new URL("https://analythics.iseal.dev/api/v1/" + endpoint);

            // Opening a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allowing output
            connection.setDoOutput(true);

            // Setting the request method
            connection.setRequestMethod(method);

            // Adding token
            connection.addRequestProperty("Authentication", token);

            // Send request body
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);

            // Retrieving the response code
            int responseCode = connection.getResponseCode();

            // Processing the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("API Response: " + response.toString());
                return response.toString();
            } else {
                System.out.println("API Call Failed. Response Code: " + responseCode);
                throw new Exception("API_RESPONSE_INVALID");
            }
        } catch (Exception e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "API_CONN_FAILED");
        }
        return null;
    }

}

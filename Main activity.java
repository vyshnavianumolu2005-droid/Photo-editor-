package com.example.yourapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button btnFetch;
    private TextView tvResult;

    // Replace with your actual API URL
    private static final String API_URL = "https://jsonplaceholder.typicode.com/todos/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFetch = findViewById(R.id.btnFetch);
        tvResult = findViewById(R.id.tvResult);

        btnFetch.setOnClickListener(v -> fetchApiData());
    }

    private void fetchApiData() {
        tvResult.setText("Loading...");

        // Network operations must run on a background thread in Android
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String resultText;
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String title = jsonObject.getString("title");
                    resultText = "Title: " + title;
                } else {
                    resultText = "Error: Server returned code " + responseCode;
                }
            } catch (Exception e) {
                resultText = "Failed: " + e.getMessage();
            }

            // Return result back to the UI main thread
            String finalResult = resultText;
            handler.post(() -> tvResult.setText(finalResult));
        });
    }
}

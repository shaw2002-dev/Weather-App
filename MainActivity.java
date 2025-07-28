package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView citynameText,temperatureText,HumidityText,descriptionText,windText;
    private ImageView weatherIcon;
    private Button refreshButton;
    private EditText citynameInput;
    private static final String API_KEY = "7510d71a2699ae02e6ecaef694662b63";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        citynameText = findViewById(R.id.city);
        temperatureText = findViewById(R.id.temperature);
        HumidityText = findViewById(R.id.humidityText);
        descriptionText = findViewById(R.id.descriptionText);
        windText = findViewById(R.id.windText);
        weatherIcon = findViewById(R.id.weatherIcon);
        refreshButton = findViewById(R.id.fetchWeatherButton);
        citynameInput = findViewById(R.id.cityInput);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = citynameInput.getText().toString();
                if(!cityName.isEmpty()){
                    FetchWeatherData(cityName);
                }
                else{
                    citynameInput.setError("Please enter a city name");
                }
            }
        });

        FetchWeatherData("Mumbai");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void FetchWeatherData(String cityName) {

        String url =  "https://api.openweathermap.org/data/2.5/weather?q="+ cityName +"&appid="+ API_KEY +"&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->
                {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    try{
                        Response response = client.newCall(request).execute();
                        String result = response.body().string();
                        runOnUiThread(() -> updateUi(result));
                    } catch ( IOException e)
                    {
                        e.printStackTrace();
                    }

                }


                );


    }

    private void updateUi(String result) {
        if(result != null){
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");

                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String resourceName = "ic_"+iconCode;
                int resId = getResources().getIdentifier(resourceName,"drawable", getPackageName());
                weatherIcon.setImageResource(resId);

                citynameText.setText(jsonObject.getString("name"));
               temperatureText.setText(String.format("%.0f°",temperature));
                HumidityText.setText(String.format("%.0f%%",humidity));
                windText.setText(String.format("%.0f km/hr",windSpeed));
                descriptionText.setText(description);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
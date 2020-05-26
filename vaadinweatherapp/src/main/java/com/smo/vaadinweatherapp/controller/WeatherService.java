package com.smo.vaadinweatherapp.controller;

import com.vaadin.flow.component.notification.Notification;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
@Data
public class WeatherService {
    private OkHttpClient client;
    private Response response;
    private String cityName;
    String unit;
    private String API = "2499848550b82cb10ca28d58f5c8fb9f";

    private JSONObject getWeather() {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?q=" + getCityName() + "&units=" + getUnit() + "&appid=" + API)
                .build();
        try {
            response = client.newCall(request).execute();
            if (getResponse().message().equals("Not Found")) {
                Notification.show("City not found.").setPosition(Notification.Position.MIDDLE);
            } else {
                return new JSONObject(Objects.requireNonNull(response.body()).string());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray returnWeatherArray() throws JSONException {
        return Objects.requireNonNull(getWeather()).getJSONArray("weather");
    }

    public JSONObject returnMain() throws JSONException {
        return Objects.requireNonNull(getWeather()).getJSONObject("main");
    }

    public JSONObject returnWind() throws JSONException {
        return Objects.requireNonNull(getWeather()).getJSONObject("wind");
    }

    public JSONObject returnClouds() throws JSONException {
        return Objects.requireNonNull(getWeather()).getJSONObject("clouds");
    }

    public JSONObject returnSys() throws JSONException {
        return Objects.requireNonNull(getWeather()).getJSONObject("sys");
    }

}

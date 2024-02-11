package org.kuba.api.communicator;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.kuba.model.weather.WeatherEntity;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherInfoCommunicator {
    @Value("${weather.api.url}")
    private String weatherMapApiUrl;
    @Value("${weather.api.key}")
    private String apiKey;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private Optional<WeatherEntity> getWeatherForCity(String city) {
        try {
            URI uri = new URI(weatherMapApiUrl + "/current.json?"  + "&key=" + apiKey + "&q="+city +"&aqi=no" );

            HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Accept", "application/json").build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                var data =  getDataWeOnlyNeedFromResponse(responseBody);
                return Optional.of(data);
            } else {
                log.error("Failed to fetch weather data for {}: Status code: {}", city, response.statusCode());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            log.error("Failed to load weather data for {}: {}", city, ex.getMessage());
            return Optional.empty();
        }
    }
    public WeatherEntity getDataWeOnlyNeedFromResponse(String jsonResponse){
        try{
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(jsonResponse);
            JsonNode locationNode = jsonNode.get("location");
            String city = locationNode.get("name").asText();

            JsonNode currentWeatherNode = jsonNode.get("current");
            double temperature = currentWeatherNode.get("temp_c").asDouble();
            double pressure = currentWeatherNode.get("pressure_mb").asDouble();
            int humidity = currentWeatherNode.get("humidity").asInt();

            return new WeatherEntity(city, temperature, pressure, humidity);
        }catch(JsonProcessingException ex){
            log.error("Failed while processing json... {}", ex.getMessage());
            return null;
        }
    }
    public List<WeatherEntity> getWeatherForBigCitiesInEurope() {
        String[] cities = {"Berlin", "Paris", "Madrid", "Rome", "London", "Amsterdam", "Vienna", "Athens", "Prague", "Warsaw"};
        Arrays.sort(cities);
        return Arrays.stream(cities)
                .map(this::getWeatherForCity)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

}

package org.kuba.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.kuba.api.communicator.WeatherInfoCommunicator;
import org.kuba.model.weather.WeatherEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
@Slf4j
@CrossOrigin
public class WeatherController {
    @Autowired
    private WeatherInfoCommunicator weatherInfoCommunicator;

    @GetMapping("/city/all")
    public ResponseEntity<List<WeatherEntity>> getWeatherByCity(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        List<WeatherEntity> weatherData = weatherInfoCommunicator.getWeatherForBigCitiesInEurope();
        weatherData.stream().sorted();
        return ResponseEntity.ok(weatherData);
    }
}


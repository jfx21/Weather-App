package org.kuba.model.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherEntity {
    @JsonProperty("city")
    private String city;
    @JsonProperty("temp")
    private double temp;
    @JsonProperty("pressure")
    private double pressure;
    @JsonProperty("humidity")
    private int humidity;

//    @Embedded
//    @JsonProperty("main")
//    private MainWeatherInfo main;
}

package org.kuba.model.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MainWeatherInfo {
    @JsonProperty("city")
    private String city;
    @JsonProperty("temp")
    private double temp;
    @JsonProperty("pressure")
    private double pressure;
    @JsonProperty("humidity")
    private int humidity;
}

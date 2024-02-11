package org.kuba.model.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;
public record LoginResponse(
    @JsonProperty String email,
    @JsonProperty String username,
    @JsonIgnore ResponseCookie jwtCookie) {
}

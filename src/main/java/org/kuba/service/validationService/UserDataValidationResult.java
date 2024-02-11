package org.kuba.service.validationService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter(onMethod = @__(@JsonIgnore))
public class UserDataValidationResult {
  @JsonProperty private boolean isUserInputCorrect = true;

  @JsonProperty
  private UserValidationExceptions validationExceptions = new UserValidationExceptions();
}

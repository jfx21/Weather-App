package org.kuba.service.validationService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Setter
@Getter(onMethod = @__(@JsonIgnore))
public class UserValidationExceptions {
  @JsonProperty private List<String> validationExceptions = new ArrayList<String>();
}

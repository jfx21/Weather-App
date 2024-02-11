package org.kuba.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
  public JsonUtil() {}

  public String toJson(Object value) {
    String json = null;
    try {
      json = new ObjectMapper().writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      log.error("Error: ", ex);
    }
    return json;
  }
}

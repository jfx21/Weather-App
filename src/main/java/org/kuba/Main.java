package org.kuba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class Main {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Main.class);
    app.run();
  }
}

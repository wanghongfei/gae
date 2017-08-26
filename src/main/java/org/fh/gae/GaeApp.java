package org.fh.gae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GAE - General Advertising Engine, 通用广告投放引擎
 */
@SpringBootApplication
public class GaeApp {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GaeApp.class);
        app.addListeners(new GaeAppEventListener());

        app.run(args);
    }


}

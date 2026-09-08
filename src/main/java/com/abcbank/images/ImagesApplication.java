package com.abcbank.images;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Spring Boot entry point — no custom bootstrapping beyond the default 
 * @SpringBootApplication component scan; all real setup lives in the config/ and security/ packages. */

@SpringBootApplication
public class ImagesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImagesApplication.class, args);
	}

}

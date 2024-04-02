package com.optimagrowth.configserver;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import com.optimagrowth.configserver.io.EnvFileReader;

@SpringBootApplication
@EnableConfigServer
public class ConfigurationServerApplication {

	public static void main(String[] args) throws IOException {
		EnvFileReader.read();

		SpringApplication.run(ConfigurationServerApplication.class, args);
	}

}

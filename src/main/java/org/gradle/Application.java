package org.gradle;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAutoConfiguration
@RestController
public class Application {
	@Autowired
	TestBizService service;

	@RequestMapping("/")
	public String home() {
		StringBuilder resSb = new StringBuilder("Hello Docker World." + "<br />Welcome to <a href='http://waylau.com'>waylau.com</a></li><br/>");
		resSb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date()));
		resSb.append(service.runMultiThread());
		return resSb.toString();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

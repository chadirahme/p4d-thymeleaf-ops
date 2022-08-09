package com.p4d.ops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = "com.p4d.ops.*,com.p4d.ops.service.impl")
public class P4dThymeleafOpsApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(P4dThymeleafOpsApplication.class, args);
	}

}

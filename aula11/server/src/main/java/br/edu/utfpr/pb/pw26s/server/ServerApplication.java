package br.edu.utfpr.pb.pw26s.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource resourceBundleMessageSource =
				new ResourceBundleMessageSource();
		resourceBundleMessageSource.setBasename("messages/messages");
		resourceBundleMessageSource.setDefaultEncoding("ISO-8859-1");
		resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
		return resourceBundleMessageSource;
	}

}

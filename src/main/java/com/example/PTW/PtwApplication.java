package com.example.PTW;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PtwApplication implements WebMvcConfigurer {

	public static void main(String[] args)  {
		SpringApplication.run(PtwApplication.class, args);
		System.out.println("I am running");
	}
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");

                registry.addMapping("/**").allowedOrigins("http://localhost:5173")

				// Replace with your frontend URL
				.allowedMethods("GET", "POST", "PUT", "DELETE").allowCredentials(true);
		System.out.println("heyy your application run properly");
    }
   


}

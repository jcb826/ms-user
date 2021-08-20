package tourGuide.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import tourGuide.consumer.GpsGateway;



@Configuration
public class TourGuideModule {


	@Bean
	public RestTemplate getRestemplate() {
		return new RestTemplate();
	}


	
}

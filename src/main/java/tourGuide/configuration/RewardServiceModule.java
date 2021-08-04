package tourGuide.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import rewardCentral.RewardCentral;
import tourGuide.consumer.GpsGateway;

import tourGuide.service.UserService;

@Configuration
public class RewardServiceModule {


	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	@Bean
	public RestTemplate getRestemplate() {
		return new RestTemplate();
	}
	
}

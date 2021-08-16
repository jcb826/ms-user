package tourGuide.consumer;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tourGuide.model.User;
import tourGuide.model.VisitedLocation;

@Component
public class RewardGateway {
    private final RestTemplate restTemplate;

    public RewardGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public ResponseEntity<User> calculateRewards (User user){
        // appel du micro service
        return restTemplate.getForEntity("localhost:8090/reward/",User.class);
    }
}

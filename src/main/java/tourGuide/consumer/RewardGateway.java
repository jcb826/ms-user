package tourGuide.consumer;

import org.springframework.http.HttpEntity;
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
        HttpEntity<User> httpEntity= new HttpEntity<>(user);
        return restTemplate.postForEntity("http://localhost:8092/reward/",httpEntity,User.class);
    }
}

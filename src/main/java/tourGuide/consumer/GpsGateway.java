package tourGuide.consumer;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tourGuide.model.VisitedLocation;

import java.util.UUID;

@Component
public class GpsGateway {

    private final RestTemplate restTemplate;

    public GpsGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public ResponseEntity <VisitedLocation> getUserLocation (UUID id){
        // appel du micro service
        return restTemplate.getForEntity("localhost:8083/gps/{uuid}"+id,VisitedLocation.class);
    }
}

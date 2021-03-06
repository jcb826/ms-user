package tourGuide.consumer;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;

import java.util.UUID;

@Component
public class GpsGateway {

    private final RestTemplate restTemplate;


    public GpsGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<VisitedLocation> getUserLocation(UUID id) {
        return restTemplate.getForEntity("http://localhost:8090/gps/location/{uuid}/", VisitedLocation.class, id.toString());
    }

    public ResponseEntity<Attraction[]> getAttractions() {

        return restTemplate.getForEntity("http://localhost:8090/gps/attractions", Attraction[].class);
    }

    public ResponseEntity<Attraction[]> getNearByAttractions(UUID id) {
        return restTemplate.getForEntity("http://localhost:8090/gps/getnearbyattractions/{uuid}/",Attraction[].class, id.toString());
    }
}

package tourGuide.service;

import org.springframework.stereotype.Service;
import tourGuide.consumer.GpsGateway;
import tourGuide.model.User;
import tourGuide.model.VisitedLocation;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, User> internalUserMap = new HashMap<>();
    private final GpsGateway gpsGateway;

    public UserService(GpsGateway gpsGateway) {
        this.gpsGateway = gpsGateway;
    }

    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }
    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }
    public VisitedLocation trackUserLocation(User user) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        VisitedLocation visitedLocation = gpsGateway.getUserLocation(user.getUserId()).getBody();
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }


}

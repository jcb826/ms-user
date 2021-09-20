package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.consumer.GpsGateway;
import tourGuide.consumer.RewardGateway;
import tourGuide.model.User;
import tourGuide.model.VisitedLocation;
import tourGuide.service.TourGuideService;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {


    @Autowired
    TourGuideService tourGuideService;

    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    RewardGateway rewardGateway;

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {

        VisitedLocation visitedLocation = tourGuideService.getUserLocation(tourGuideService.getUser(userName));
        return JsonStream.serialize(visitedLocation.location);


    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(userName));
    }

    @RequestMapping("/users")
    public List<User> getUsers() {
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);
        List<User> users = tourGuideService.getAllUsers();
       // tourGuideService.tracker.stopTracking();
        return users;
    }


}

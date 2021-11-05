package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tourGuide.consumer.GpsGateway;
import tourGuide.consumer.RewardGateway;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
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
        return users;
    }

    @PutMapping("/preferences/{userName}")
    public void updateUser(@PathVariable() String userName, @RequestBody UserPreferences userPreferences) {
        User user = tourGuideService.getUser(userName);
        user.setUserPreferences(userPreferences);
        tourGuideService.updateUser(userName, user);
    }


}

package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.VisitedLocation;
import tourGuide.service.TourGuideService;

@RestController
public class UserController {


    @Autowired
    TourGuideService tourGuideService;


    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {

        VisitedLocation visitedLocation = tourGuideService.getUserLocation(tourGuideService.getUser(userName));
        return JsonStream.serialize(visitedLocation.location);


    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(userName));
    }


}

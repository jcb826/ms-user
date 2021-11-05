package tourGuide.msuser;

import org.junit.jupiter.api.Assertions;
import org.apache.commons.lang3.time.StopWatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tourGuide.consumer.GpsGateway;
import tourGuide.consumer.RewardGateway;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Attraction;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.model.VisitedLocation;
import tourGuide.service.TourGuideService;

import java.util.*;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class MsUserApplicationIT {
    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    RewardGateway rewardGateway;
    @Autowired
    TourGuideService tourGuideService;


    @Test
    void contextLoads() {
    }

    @Test
    public void getUserLocation() throws InterruptedException {

        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user.getVisitedLocations();
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.shutdown();
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        Assertions.assertTrue(visitedLocations.get(0).userId.equals(user.getUserId()));

    }

    @Test
    public void highVolumeTrackLocation() throws InterruptedException {
        Locale.setDefault(new Locale("en", "US"));

        // Users should be incremented up to 100,000, and test finishes within 15 minutes
        InternalTestHelper.setInternalUserNumber(100);
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

        List<User> allUsers = tourGuideService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        allUsers.parallelStream().forEach(u -> tourGuideService.trackUserLocation(u));
        tourGuideService.shutdown();
        stopWatch.stop();


        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }




    @Test
    public void userGetRewards() throws InterruptedException {
        Locale.setDefault(new Locale("en", "US"));


        InternalTestHelper.setInternalUserNumber(0);


        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsGateway.getAttractions().getBody()[0];
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        tourGuideService.trackUserLocation(user);
        tourGuideService.shutdown();
        List<UserReward> userRewards = user.getUserRewards();

        Assertions.assertTrue(userRewards.size() == 1);
    }



    @Test
    public void getNearbyAttractions() throws InterruptedException {
        Locale.setDefault(new Locale("en", "US"));

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.shutdown();
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        VisitedLocation  visitedLocation1 = visitedLocations.get(0);

        Attraction[] attractions = gpsGateway.getNearByAttractions(user.getUserId()).getBody();

        //tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(5, attractions.length);
    }


}


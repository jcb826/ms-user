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

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class MsUserApplicationTests {
    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    RewardGateway rewardGateway;


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
    public void addUser() {

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrivedUser = tourGuideService.getUser(user.getUserName());
        User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

        //  tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(user, retrivedUser);
        Assertions.assertEquals(user2, retrivedUser2);
    }




    @Test
    public void highVolumeTrackLocation() throws InterruptedException {
        Locale.setDefault(new Locale("en", "US"));

        // Users should be incremented up to 100,000, and test finishes within 15 minutes
        InternalTestHelper.setInternalUserNumber(100000);
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




}


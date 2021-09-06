package tourGuide.msuser;

import org.junit.jupiter.api.Assertions;
import org.apache.commons.lang3.time.StopWatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tourGuide.consumer.GpsGateway;
import tourGuide.consumer.RewardGateway;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.VisitedLocation;
import tourGuide.service.TourGuideService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
    public void getUserLocation() {
		/*
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());


 */
        Locale.setDefault(new Locale("en", "US"));
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();
        Assertions.assertTrue(visitedLocation.userId.equals(user.getUserId()));

    }


    @Test
    public void addUser() {
/*
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
 */
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrivedUser = tourGuideService.getUser(user.getUserName());
        User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(user, retrivedUser);
        Assertions.assertEquals(user2, retrivedUser2);
    }


        @Test
        public void highVolumeTrackLocation() {
            Locale.setDefault(new Locale("en", "US"));

            // Users should be incremented up to 100,000, and test finishes within 15 minutes
            InternalTestHelper.setInternalUserNumber(10000);
            TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

            List<User> allUsers = tourGuideService.getAllUsers();

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            /*
            for (User user : allUsers) {
                tourGuideService.trackUserLocation(user);
            }

             */
            allUsers.parallelStream().forEach(u-> tourGuideService.trackUserLocation(u));
            stopWatch.stop();
            tourGuideService.tracker.stopTracking();

            System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
            Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
        }




/*
    @Test
    public void highVolumeTrackLocation() {
        Locale.setDefault(new Locale("en", "US"));

        // Users should be incremented up to 100,000, and test finishes within 15 minutes
        InternalTestHelper.setInternalUserNumber(1000);
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);

        List<User> allUsers = tourGuideService.getAllUsers();
        List<User> list1= allUsers.subList(0,250);
        List<User> list2= allUsers.subList(251,500);
        List<User> list3= allUsers.subList(501,750);
        List<User> list4= allUsers.subList(751,999);
        StopWatch stopWatch = new StopWatch();
        Long chrono= System.currentTimeMillis();
        stopWatch.start();

        CompletableFuture.supplyAsync(()->tourGuideService.multiThreading(list1))
                .thenAccept(visitedLocation -> System.out.println("Thread 1 done"));
        CompletableFuture.supplyAsync(()->tourGuideService.multiThreading(list2))
                .thenAccept(visitedLocation -> System.out.println("Thread 2 done"));
        CompletableFuture.supplyAsync(()->tourGuideService.multiThreading(list3))
                .thenAccept(visitedLocation -> System.out.println("Thread 3 done"));
        CompletableFuture.supplyAsync(()->tourGuideService.multiThreading(list4))
                .thenAccept(visitedLocation -> System.out.println("Thread 4 done"));

        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println(System.currentTimeMillis()-chrono);
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

 */




}


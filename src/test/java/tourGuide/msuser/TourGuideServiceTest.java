package tourGuide.msuser;

import org.junit.jupiter.api.Assertions;
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
import tripPricer.Provider;

import java.util.*;

@SpringBootTest
public class TourGuideServiceTest {

    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    RewardGateway rewardGateway;



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


        Assertions.assertEquals(user, retrivedUser);
        Assertions.assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getAllUsers() {
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);


        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();


        Assertions.assertTrue(allUsers.contains(user));
        Assertions.assertTrue(allUsers.contains(user2));
    }


    @Test
    public void getTripDeals() {

        TourGuideService tourGuideService = new TourGuideService(gpsGateway, rewardGateway);
        InternalTestHelper.setInternalUserNumber(0);


        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user);

        Assertions.assertEquals(5, providers.size());
    }


}

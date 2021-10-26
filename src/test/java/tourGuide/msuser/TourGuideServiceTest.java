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

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
@SpringBootTest
public class TourGuideServiceTest {

    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    TourGuideService tourGuideService;

    @Test
    public void userGetRewards() {
        Locale.setDefault(new Locale("en", "US"));


        InternalTestHelper.setInternalUserNumber(0);


        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsGateway.getAttractions().getBody()[0];
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();

        Assertions.assertTrue(userRewards.size() == 1);
    }


}

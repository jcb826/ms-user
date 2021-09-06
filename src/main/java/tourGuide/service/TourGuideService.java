package tourGuide.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.consumer.GpsGateway;
import tourGuide.consumer.RewardGateway;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Location;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.model.VisitedLocation;
import tourGuide.tracker.Tracker;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//dans ms-user
// dans pour chaque methode de cette classe creer son endPoint
@Service
public class TourGuideService {
    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    public final Tracker tracker;
    //private final GpsUtil gpsUtil;
    //private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    private final Map<String, User> internalUserMap = new HashMap<>();
    private final GpsGateway gpsGateway;
    private final RewardGateway rewardGateway;
    boolean testMode = true;
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

    public TourGuideService(/*GpsUtil gpsUtil, RewardsService rewardsService*/GpsGateway gpsGateway, RewardGateway rewardGateway) {
        this.gpsGateway = gpsGateway;
        this.rewardGateway = rewardGateway;


        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
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

    public void updateUser(String userName, User user) {
        internalUserMap.put(userName, user);
    }


    // reward

    public VisitedLocation trackUserLocation(User user) {

        VisitedLocation visitedLocation = gpsGateway.getUserLocation(user.getUserId()).getBody();
        user.addToVisitedLocations(visitedLocation);
        // implementer dans le controleur de reward calculateRewards(user);
        User userUpdated = rewardGateway.calculateRewards(user).getBody();
        updateUser(user.getUserName(), userUpdated);

        return visitedLocation;
    }

    public boolean multiThreading(List<User> users) {
       // int i=0;
        for (User user : users) {
            trackUserLocation(user);
        //    System.out.println(i);
          //  i++;
        }
        return true;
    }


/*
    public VisitedLocation trackUserLocation(User user) {
        AtomicReference<VisitedLocation> visitedLocation = new AtomicReference<>(new VisitedLocation());
        CompletableFuture.supplyAsync(()->gpsGateway.getUserLocation(user.getUserId()).getBody())
                .thenAccept(visitedLocation2 -> visitedLocation.set(visitedLocation2));
        AtomicReference<User> userUpdated = new AtomicReference<>(new User());
        CompletableFuture.supplyAsync(()->rewardGateway.calculateRewards(user).getBody())
                .thenAccept(user2 -> userUpdated.set(user2));
        user.addToVisitedLocations(visitedLocation.get());
      //  System.out.println(userUpdated.get());
        updateUser(user.getUserName(), userUpdated.get());
        return visitedLocation.get();
    }

 */


    public List<UserReward> getUserRewards(String username) {

        return this.getUser(username).getUserRewards();
    }

    // user
    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    // user
    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    // user
    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }


}

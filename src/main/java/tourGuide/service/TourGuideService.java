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
import tripPricer.Provider;
import tripPricer.TripPricer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class TourGuideService {
    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    private final TripPricer tripPricer = new TripPricer();
    private final Map<String, User> internalUserMap = new HashMap<>();
    private final GpsGateway gpsGateway;
    private final RewardGateway rewardGateway;
    boolean testMode = true;
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(500);
    public TourGuideService(GpsGateway gpsGateway, RewardGateway rewardGateway) {
        this.gpsGateway = gpsGateway;
        this.rewardGateway = rewardGateway;


        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }

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




    public VisitedLocation trackUserLocation(User user) {
        CompletableFuture.runAsync(()->{
            VisitedLocation visitedLocation = gpsGateway.getUserLocation(user.getUserId()).getBody();
            user.addToVisitedLocations(visitedLocation);
            rewardGateway.calculateRewards(user,visitedLocation);
            int i=0;
        },executorService);



       return null;

    }

    public boolean multiThreading(List<User> users) {

        for (User user : users) {
            trackUserLocation(user);

        }
        return true;
    }





    public List<UserReward> getUserRewards(String username) {

        return this.getUser(username).getUserRewards();
    }


    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }


    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }


    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }


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
public void shutdown() throws InterruptedException {

        executorService.shutdown();
        try {
            if(!executorService.awaitTermination(15, TimeUnit.MINUTES)){

                executorService.shutdownNow();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
            executorService.shutdownNow();
        }

}


}

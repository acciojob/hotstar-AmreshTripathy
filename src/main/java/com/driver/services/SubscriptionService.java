package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        int userId = subscriptionEntryDto.getUserId();
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreensRequired = subscriptionEntryDto.getNoOfScreensRequired();

        int amount = getTotalAmountAccordingToSubscription(subscriptionType, noOfScreensRequired);

        User user = userRepository.findById(userId).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setNoOfScreensSubscribed(noOfScreensRequired);
        subscription.setStartSubscriptionDate(new Date());
        subscription.setTotalAmountPaid(amount);

        user.setSubscription(subscription);
        userRepository.save(user);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId) throws Exception {

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();

        if (subscriptionType.equals(SubscriptionType.ELITE))
            throw new Exception("Already the best Subscription");

        int totalAmount = 0;

        if (subscriptionType.equals(SubscriptionType.PRO)) {
            totalAmount = getTotalAmountAccordingToSubscription(SubscriptionType.ELITE, subscription.getNoOfScreensSubscribed());

            subscription.setSubscriptionType(SubscriptionType.ELITE);
        } else {
            totalAmount = getTotalAmountAccordingToSubscription(SubscriptionType.PRO, subscription.getNoOfScreensSubscribed());

            subscription.setSubscriptionType(SubscriptionType.PRO);
        }
        int pastAmount = subscription.getTotalAmountPaid();

        subscription.setTotalAmountPaid(totalAmount);

        user.setSubscription(subscription);
        userRepository.save(user);

        return totalAmount - pastAmount;
    }

    private int getTotalAmountAccordingToSubscription(SubscriptionType subscriptionType, int noOfScreensSubscribed) {
        if (subscriptionType.equals(SubscriptionType.ELITE)) {
            return 1000 + (350 * noOfScreensSubscribed);
        } else if (subscriptionType.equals(SubscriptionType.PRO)) {
            return 800 + (250 * noOfScreensSubscribed);
        } else {
            return 500 + (200 * noOfScreensSubscribed);
        }
    }

    public Integer calculateTotalRevenueOfHotstar() {

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        int totalRevenue = 0;

        for (Subscription subscription : subscriptionRepository.findAll()) {
            totalRevenue += subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}

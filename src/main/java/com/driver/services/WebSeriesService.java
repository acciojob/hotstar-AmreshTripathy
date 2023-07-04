package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.SubscriptionType;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        String seriesName = webSeriesEntryDto.getSeriesName();
        int ageLimit = webSeriesEntryDto.getAgeLimit();
        double rating = webSeriesEntryDto.getRating();
        SubscriptionType subscriptionType = webSeriesEntryDto.getSubscriptionType();
        Integer productionHouseId = webSeriesEntryDto.getProductionHouseId();

        ProductionHouse productionHouse = productionHouseRepository.findById(productionHouseId).get();
        WebSeries webSeries = webSeriesRepository.findBySeriesName(seriesName);

        if (webSeries != null)
            throw new Exception("Series is already present");

        webSeries.setSeriesName(seriesName);
        webSeries.setAgeLimit(ageLimit);
        webSeries.setRating(rating);
        webSeries.setSubscriptionType(subscriptionType);
        webSeries.setProductionHouse(productionHouse);

        webSeries = webSeriesRepository.save(webSeries);

        setTotalRatingOfProductionHouse(productionHouse, rating);
        productionHouse.getWebSeriesList().add(webSeries);
        productionHouseRepository.save(productionHouse);

        return webSeries.getId();
    }

    private void setTotalRatingOfProductionHouse(ProductionHouse productionHouse, double curRating) {
        double total = 0;
        int count = 0;

        for (WebSeries webSeries : productionHouse.getWebSeriesList()) {
            total += webSeries.getRating();
            count++;
        }

        double finalRating = (total + curRating) / (count + 1);
        productionHouse.setRatings(finalRating);
    }
}

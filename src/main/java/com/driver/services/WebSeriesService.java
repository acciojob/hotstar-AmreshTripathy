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

        if (checkSeriesNameAlreadyPresent(seriesName))
            throw new Exception("Series is already present");

        ProductionHouse productionHouse = productionHouseRepository.findById(productionHouseId).get();

        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();

        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(seriesName);
        webSeries.setAgeLimit(ageLimit);
        webSeries.setRating(rating);
        webSeries.setSubscriptionType(subscriptionType);

        webSeries = webSeriesRepository.save(webSeries);

        setTotalRatingOfProductionHouse(productionHouse, rating);
        webSeriesList.add(webSeries);
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

    private boolean checkSeriesNameAlreadyPresent(String seriesName) {

        for (WebSeries webSeries : webSeriesRepository.findAll()) {
            if (webSeries.getSeriesName().equalsIgnoreCase(seriesName))
                return true;
        }

        return false;
    }

}

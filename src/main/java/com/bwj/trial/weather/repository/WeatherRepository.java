package com.bwj.trial.weather.repository;

import java.util.List;
import java.util.Map;

import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.model.DataPoint;

public interface WeatherRepository {

    List<AirportData> getAirportDataList();

    AtmosphericInformation getAtmosphericInformation(String iata);

    Map<String, AtmosphericInformation> getAtmosphericInformations();

    Map<AirportData, Integer> getRequestFrequency();

    Map<Double, Integer> getRadiusFreq();

    AirportData getAirportData(String iataCode);

    void updateRequestFrequency(String iata, Double radius);

    void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp);

    void addAirport(AirportData airportData);

    void addAtmosphericInformation(String iata, AtmosphericInformation atmosphericInformation);

    void deleteAirport(AirportData airportData);
}

package com.bwj.trial.weather.repository;

import java.util.List;
import java.util.Map;

import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.model.DataPoint;

public interface WeatherRepository {

    
    /** get airport data
     * 
     * @return List<AirportData>
     */
    List<AirportData> getAirportDataList();

    /**
     * get atmospheric information by airport iata key code
     * 
     * @param iata
     * @return AtmosphericInformation
     */
    AtmosphericInformation getAtmosphericInformation(String iata);

    /**
     * request all existing atmospheric data
     * 
     * @return Map<String, AtmosphericInformation>
     */
    Map<String, AtmosphericInformation> getAtmosphericInformations();

    /**
     * return all request frequency
     * 
     * @return Map<AirportData, Integer>
     */
    Map<AirportData, Integer> getRequestFrequency();

    /** 
     * return all redius freq data
     * 
     * @return Map<Double, Integer>
     */
    Map<Double, Integer> getRadiusFreq();

    /**
     * return exist airport by given iata key 
     * 
     * @param iataCode
     * 
     * @return AirportData
     */
    AirportData getAirportData(String iataCode);
    
    /**
     * update atmospheric information by given data
     * 
     * @param ai
     * @param pointType
     * @param dp
     * @return
     */
    boolean updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp);

    /** 
     * update how often requests are requested
     * 
     * @param iata
     * @param radius
     */
    void updateRequestFrequency(String iata, Double radius);

    /**
     * add new airport information in existing list
     * 
     * @param airportData
     */
    void addAirport(AirportData airportData);

    /**
     * add new atmospheric
     * 
     * @param iata
     * @param atmosphericInformation
     */
    void addAtmosphericInformation(String iata, AtmosphericInformation atmosphericInformation);

    /**
     * @param airportData
     */
    void deleteAirport(AirportData airportData);
}

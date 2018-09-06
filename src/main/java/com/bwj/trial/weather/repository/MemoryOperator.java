package com.bwj.trial.weather.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;

public enum MemoryOperator {

    INSTANCE;
    
    
    /**
     * all known airports
     */
    private final List<AirportData> airportData;

    /**
     * atmospheric information for each airport, idx corresponds with airportData
     */
    private final ConcurrentHashMap<String, AtmosphericInformation> atmosphericInformation;

    /**
     * Internal performance counter to better understand most requested information,
     * this map can be improved but for now provides the basis for future
     * performance optimizations. Due to the stateless deployment architecture we
     * don't want to write this to disk, but will pull it off using a REST request
     * and aggregate with other performance metrics
     */
    private final ConcurrentHashMap<AirportData, Integer> requestFrequency;

    private final ConcurrentHashMap<Double, Integer> radiusFreq;
    
    MemoryOperator() {
        airportData = Collections.synchronizedList(new ArrayList<>());
        atmosphericInformation = new ConcurrentHashMap<>();
        requestFrequency = new ConcurrentHashMap<>();
        radiusFreq = new ConcurrentHashMap<>();
    }

    public  List<AirportData> getAirportData() {
        return airportData;
    }

    public ConcurrentHashMap<String, AtmosphericInformation> getAtmosphericInformation() {
        return atmosphericInformation;
    }

    public ConcurrentHashMap<AirportData, Integer> getRequestFrequency() {
        return requestFrequency;
    }

    public ConcurrentHashMap<Double, Integer> getRadiusFreq() {
        return radiusFreq;
    }
    
    public void addAirportData(AirportData ad) {
        synchronized (airportData) {
            airportData.add(ad);
        }
    }
    
    public void addAtmosphericInformation(String iata, AtmosphericInformation ai) {
        synchronized (atmosphericInformation) {
            atmosphericInformation.put(iata, ai);
        }
    }
    
    public void removeAirport(AirportData ad) {
        synchronized (airportData) {
            getAirportData().remove(ad);
            getAtmosphericInformation().remove(ad.getIata());
        }
    }
    
    public void updateRequestFrequency(AirportData ad) {
        synchronized (requestFrequency) {
            int index = requestFrequency.getOrDefault(airportData, 0) + 1;
            requestFrequency.put(ad, index);
        }
    }
    
    public void updateRadiusFreq(Double radius) {
        synchronized (radiusFreq) {
            int index = radiusFreq.getOrDefault(radius, 0);
            radiusFreq.put(radius, index);
        }
    }

}

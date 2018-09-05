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
    private volatile List<AirportData> airportData = Collections.synchronizedList(new ArrayList<>());

    /**
     * atmospheric information for each airport, idx corresponds with airportData
     */
    private ConcurrentHashMap<String, AtmosphericInformation> atmosphericInformation = new ConcurrentHashMap<>();

    /**
     * Internal performance counter to better understand most requested information,
     * this map can be improved but for now provides the basis for future
     * performance optimizations. Due to the stateless deployment architecture we
     * don't want to write this to disk, but will pull it off using a REST request
     * and aggregate with other performance metrics
     */
    private ConcurrentHashMap<AirportData, Integer> requestFrequency = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Double, Integer> radiusFreq = new ConcurrentHashMap<>();

    public synchronized List<AirportData> getAirportData() {
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

}

package com.bwj.trial.weather.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;

public enum MemoryOperator {

    INSTANCE;

    /**
     * all known airports
     */
    private List<AirportData> airportData = new ArrayList<>();

    /**
     * atmospheric information for each airport, idx corresponds with airportData
     */
    private Map<String, AtmosphericInformation> atmosphericInformation = new HashMap<String, AtmosphericInformation>();

    /**
     * Internal performance counter to better understand most requested information,
     * this map can be improved but for now provides the basis for future
     * performance optimizations. Due to the stateless deployment architecture we
     * don't want to write this to disk, but will pull it off using a REST request
     * and aggregate with other performance metrics {@link #ping()}
     */
    private Map<AirportData, Integer> requestFrequency = new HashMap<AirportData, Integer>();

    private Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

    public List<AirportData> getAirportData() {
        return airportData;
    }

    public Map<String, AtmosphericInformation> getAtmosphericInformation() {
        return atmosphericInformation;
    }

    public Map<AirportData, Integer> getRequestFrequency() {
        return requestFrequency;
    }

    public Map<Double, Integer> getRadiusFreq() {
        return radiusFreq;
    }

}

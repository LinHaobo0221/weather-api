package com.bwj.trial.weather.repository;

import java.util.List;
import java.util.Map;

import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.model.DataPoint;
import com.bwj.trial.weather.model.DataPointType;

public class WeatherRepositoryImpl implements WeatherRepository {

    @Override
    public AirportData getAirportData(String iataCode) {
        synchronized (MemoryOperator.INSTANCE.getAirportData()) {
            return MemoryOperator.INSTANCE.getAirportData().parallelStream().filter(ap -> ap.getIata().equals(iataCode))
                    .findFirst().orElse(null);
        }
    }

    @Override
    public void updateRequestFrequency(String iata, Double radius) {
        AirportData airportData = getAirportData(iata);

        Map<AirportData, Integer> map = MemoryOperator.INSTANCE.getRequestFrequency();
        map.put(airportData, map.getOrDefault(airportData, 0) + 1);

        Map<Double, Integer> freqMap = MemoryOperator.INSTANCE.getRadiusFreq();
        freqMap.put(radius, freqMap.getOrDefault(radius, 0));
    }

    @Override
    public Map<String, AtmosphericInformation> getAtmosphericInformations() {
        return MemoryOperator.INSTANCE.getAtmosphericInformation();
    }

    @Override
    public AtmosphericInformation getAtmosphericInformation(String iata) {
        return getAtmosphericInformations().get(iata);
    }

    @Override
    public List<AirportData> getAirportDataList() {
        return MemoryOperator.INSTANCE.getAirportData();
    }

    @Override
    public Map<AirportData, Integer> getRequestFrequency() {
        return MemoryOperator.INSTANCE.getRequestFrequency();
    }

    @Override
    public Map<Double, Integer> getRadiusFreq() {
        return MemoryOperator.INSTANCE.getRadiusFreq();
    }

    @Override
    public boolean updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) {

        DataPointType filterDataPointType = DataPointType.getAllDataPointTypes().parallelStream()
                .filter(dataPointType -> dataPointType.getValue().equalsIgnoreCase(pointType)).findFirst().orElse(null);

        if (filterDataPointType == null) {
            return false;
        }

        filterDataPointType.updateAtmosphericInformation(ai, dp);

        return true;

    }

    @Override
    public synchronized void addAirport(AirportData airportData) {
        MemoryOperator.INSTANCE.getAirportData().add(airportData);
    }

    @Override
    public void addAtmosphericInformation(String iata, AtmosphericInformation atmosphericInformation) {
        MemoryOperator.INSTANCE.getAtmosphericInformation().put(iata, atmosphericInformation);
    }

    @Override
    public void deleteAirport(AirportData airportData) {
        synchronized (MemoryOperator.INSTANCE.getAirportData()) {
            MemoryOperator.INSTANCE.getAirportData().remove(airportData);
            MemoryOperator.INSTANCE.getAtmosphericInformation().remove(airportData.getIata());
        }
    }

}

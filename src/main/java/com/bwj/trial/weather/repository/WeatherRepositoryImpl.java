package com.bwj.trial.weather.repository;

import java.util.List;
import java.util.Map;

import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.model.DataPoint;

public class WeatherRepositoryImpl implements WeatherRepository {

    @Override
    public AirportData getAirportData(String iataCode) {
        return MemoryOperator.INSTANCE.getAirportData().parallelStream().filter(ap -> ap.getIata().equals(iataCode))
                .findFirst().orElse(null);
    }

    @Override
    public void updateRequestFrequency(String iata, Double radius) {
        AirportData airportData = getAirportData(iata);

        MemoryOperator.INSTANCE.updateRequestFrequency(airportData);
        MemoryOperator.INSTANCE.updateRadiusFreq(radius);
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
        MemoryOperator.INSTANCE.addAirportData(airportData);
    }

    @Override
    public void addAtmosphericInformation(String iata, AtmosphericInformation atmosphericInformation) {
        MemoryOperator.INSTANCE.addAtmosphericInformation(iata, atmosphericInformation);
    }

    @Override
    public void deleteAirport(AirportData airportData) {
        MemoryOperator.INSTANCE.removeAirport(airportData);
    }

}

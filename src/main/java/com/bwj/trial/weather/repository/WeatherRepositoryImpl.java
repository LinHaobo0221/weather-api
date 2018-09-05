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
    public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) {

        DataPointType dataPointType = DataPointType.valueOf(pointType.toUpperCase());
        switch (dataPointType) {
        case WIND:
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
            }
            break;
        case TEMPERATURE:
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
            }
            break;
        case HUMIDTY:
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
            }
            break;
        case PRESSURE:
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
            }
            break;
        case CLOUDCOVER:
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
            }
            break;
        case PRECIPITATION:
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
            }
            break;
        }
    }

    @Override
    public void addAirport(AirportData airportData) {
        synchronized (MemoryOperator.INSTANCE.getAirportData()) {
            MemoryOperator.INSTANCE.getAirportData().add(airportData);
        }
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

package com.bwj.trial.weather.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.bwj.trial.weather.WeatherException;
import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.model.DataPoint;
import com.bwj.trial.weather.repository.WeatherRepository;

public class WeatherServiceImpl implements WeatherService {

    /**
     * earth radius in KM
     */
    public static final double EARTH_RADIUS = 6372.8;

    private WeatherRepository weatherRepos;

    @Inject
    public WeatherServiceImpl(WeatherRepository weatherRepository) {
        super();
        this.weatherRepos = weatherRepository;
    }

    @Override
    public Map<String, Object> getPingResult() {

        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, AtmosphericInformation> allInformation = weatherRepos.getAtmosphericInformations();

        // datasize
        long datasize = allInformation.keySet().parallelStream().map(iata -> allInformation.get(iata))
                .filter(item -> item.isValidate())
                .filter(item -> item.getLastUpdateTime() > System.currentTimeMillis() - 86400000).count();

        result.put("datasize", datasize);

        // freqMap
        Map<String, Double> freqMap = new HashMap<>();

        int total = weatherRepos.getRequestFrequency().size();
        if (total == 0) {
            result.put("iata_freq", freqMap);
        } else {
            List<AirportData> dataList = weatherRepos.getAirportDataList();
            
            synchronized (dataList) {
                for (AirportData airportData : dataList) {
                    
                    int index = weatherRepos.getRequestFrequency().getOrDefault(airportData, 0);
                    double freq = BigDecimal.valueOf(index).subtract(BigDecimal.valueOf(total)).doubleValue();

                    freqMap.put(airportData.getIata(), freq);
                }
            }
            result.put("iata_freq", freqMap);
        }

        int size = weatherRepos.getRadiusFreq().keySet().stream().max(Double::compare).orElse(1000.0).intValue() + 1;

        // hist array
        int[] hist = new int[size];
        for (Map.Entry<Double, Integer> e : weatherRepos.getRadiusFreq().entrySet()) {
            int i = BigDecimal.valueOf(e.getKey().intValue()).scaleByPowerOfTen(-1).intValue();
            hist[i] += e.getValue();
        }
        result.put("radius_freq", hist);

        return result;
    }

    @Override
    public List<AtmosphericInformation> getWetherList(String iata, String radiusParam) {

        double radius = radiusParam == null || radiusParam.trim().isEmpty() ? 0
                : new BigDecimal(radiusParam).doubleValue();

        weatherRepos.updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> result = new ArrayList<>();

        if (radius == 0) {
            result.add(weatherRepos.getAtmosphericInformation(iata));

        } else {

            AirportData ad = weatherRepos.getAirportData(iata);

            List<AirportData> airportDataList = weatherRepos.getAirportDataList();

            synchronized (airportDataList) {
                for (AirportData airportData : airportDataList) {

                    if (this.calculateDistance(ad, airportData) <= radius) {

                        AtmosphericInformation atmosphericInformation = weatherRepos.getAtmosphericInformation(airportData.getIata());

                        if (atmosphericInformation.isValidate()) {
                            result.add(atmosphericInformation);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {

        AtmosphericInformation ai = weatherRepos.getAtmosphericInformation(iataCode);

        return weatherRepos.updateAtmosphericInformation(ai, pointType, dp);

    }

    @Override
    public Set<String> getIataCodes() {

        List<AirportData> dataList = weatherRepos.getAirportDataList();

        synchronized (dataList) {
            return weatherRepos.getAirportDataList().parallelStream().map(AirportData::getIata)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public AirportData getAirportDataByIata(String iataCode) {
        return weatherRepos.getAirportData(iataCode);
    }

    @Override
    public boolean addAirport(String iataCode, String latitude, String longitude) {

        if (weatherRepos.getAirportData(iataCode) != null) {
            return false;
        }

        AirportData airport = new AirportData(iataCode, new BigDecimal(latitude), new BigDecimal(longitude));
        weatherRepos.addAirport(airport);

        AtmosphericInformation ai = new AtmosphericInformation();
        weatherRepos.addAtmosphericInformation(iataCode, ai);

        return true;
    }

    @Override
    public boolean deleteAirport(String iata) {

        AirportData airportData = weatherRepos.getAirportData(iata);
        if (airportData == null) {
            return false;
        }

        weatherRepos.deleteAirport(airportData);

        return true;
    }

    private double calculateDistance(AirportData dataOne, AirportData dataTwo) {

        double deltaLat = Math.toRadians(dataTwo.getLatitude().subtract(dataOne.getLatitude()).doubleValue());
        double deltaLon = Math.toRadians(dataTwo.getLongitude().subtract(dataOne.getLongitude()).doubleValue());

        double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(dataOne.getLatitude().doubleValue()) * Math.cos(dataTwo.getLatitude().doubleValue());

        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS * c;
    }

}

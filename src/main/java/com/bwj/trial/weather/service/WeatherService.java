package com.bwj.trial.weather.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bwj.trial.weather.WeatherException;
import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.model.DataPoint;
import com.bwj.trial.weather.repository.DataPointType;

public interface WeatherService {

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the
     * requested airport information and return a list of matching atmosphere
     * information.
     *
     * @param iata
     *            the iataCode
     * @param radiusParam
     *            the radius in km
     * @return a list of atmospheric information
     */
    List<AtmosphericInformation> getWetherList(String iata, String radiusParam);

    /**
     * Retrieve service health including total size of valid data points and request
     * frequency information.
     *
     * @return health stats for the service as a string
     */
    Map<String, Object> getPingResult();

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode
     *            the 3 letter IATA code
     * @param pointType
     *            the point type {@link DataPointType}
     * @param dp
     *            a datapoint object holding pointType data
     * @throws WeatherException
     *             if the update can not be completed
     */
    boolean addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException;

    /**
     * Retrieve airport data, including latitude and longitude for a particular
     * airport
     *
     * @param iataCode
     *            the 3 letter airport code
     * @return an HTTP Response with a json representation of {@link AirportData}
     */
    Set<String> getIataCodes();

    /**
     * returns airport data, including latitude and longitude for a particular
     * airport
     *
     * @param iataCode
     *            the 3 letter airport code
     * @return {@link AirportData}
     */
    AirportData getAirportDataByIata(String iataCode);

    /**
     * Add a new airport to the known airport list.
     *
     * @param iataCode
     *            the 3 letter airport code of the new airport
     * @param latitude
     *            the airport's latitude in degrees
     * @param longitude
     *            the airport's longitude in degrees
     * @return newly added airport data
     */
    boolean addAirport(String iataCode, String latitude, String longitude);

    /**
     * Remove an airport from the known airport list
     *
     * @param iata
     *            the 3 letter airport code
     * @return boolean for the delete operation
     */
    boolean deleteAirport(String iata);

}

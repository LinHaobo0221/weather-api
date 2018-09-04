package com.bwj.trial.weather.endpoint;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.service.WeatherService;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check
 * health stats. Currently, all data is held in memory. The end point deploys to
 * a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger(RestWeatherQueryEndpoint.class.getName());

    /**
     * shared gson json to object factory
     */
    public static final Gson gson = new Gson();


    private WeatherService weatherService;


    public RestWeatherQueryEndpoint(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Retrieve service health including total size of valid data points and request
     * frequency information.
     *
     * @return health stats for the service as a string
     */
    @GET
    @Path("/ping")
    @Override
    public String ping() {

        Map<String, Object> resultMap = weatherService.getPingResult();

        return gson.toJson(resultMap);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the
     * requested airport information and return a list of matching atmosphere
     * information.
     *
     * @param iata         the iataCode
     * @param radiusString the radius in km
     * @return a list of atmospheric information
     */
    @GET
    @Path("/weather/{iata}/{radius}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response weather(@PathParam("iata") String iata, @PathParam("radius") String radiusString) {

        List<AtmosphericInformation> retval = weatherService.getWetherList(iata, radiusString);

        return Response.status(Response.Status.OK).entity(retval).build();
    }

}

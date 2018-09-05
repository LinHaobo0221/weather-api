package com.bwj.trial.weather.endpoint;

import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bwj.trial.weather.WeatherException;
import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.DataPoint;
import com.bwj.trial.weather.service.WeatherService;
import com.google.gson.Gson;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {

    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    /**
     * shared gson json to object factory
     */
    public final static Gson gson = new Gson();

    final WeatherService weatherService;

    @Inject
    public RestWeatherCollectorEndpoint(WeatherService weatherService) {
        super();
        this.weatherService = weatherService;
    }

    @GET
    @Path("/ping")
    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @POST
    @Path("/weather/{iata}/{pointType}")
    @Override
    public Response updateWeather(@PathParam("iata") String iataCode, @PathParam("pointType") String pointType,
            String datapointJson) {
        try {
            if (iataCode == null || "".equals(iataCode)) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            weatherService.addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));

        } catch (WeatherException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirports() {

        Set<String> result = weatherService.getIataCodes();

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirport(@PathParam("iata") String iata) {

        AirportData ad = weatherService.getAirportDataByIata(iata);

        return Response.status(Response.Status.OK).entity(ad).build();
    }

    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    @Override
    public Response addAirport(@PathParam("iata") String iata, @PathParam("lat") String latString,
            @PathParam("long") String longString) {

        if (iata == null || latString == null || longString == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        AirportData airportData = weatherService.addAirport(iata, latString, longString);
        if (airportData == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();

    }

    @DELETE
    @Path("/airport/{iata}")
    @Override
    public Response deleteAirport(@PathParam("iata") String iata) {

        if (iata == null || "".equals(iata)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean result = weatherService.deleteAirport(iata);
        if (!result) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/exit")
    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }

}

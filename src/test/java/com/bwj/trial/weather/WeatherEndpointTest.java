package com.bwj.trial.weather;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bwj.trial.weather.endpoint.RestWeatherCollectorEndpoint;
import com.bwj.trial.weather.endpoint.RestWeatherQueryEndpoint;
import com.bwj.trial.weather.endpoint.WeatherCollectorEndpoint;
import com.bwj.trial.weather.endpoint.WeatherQueryEndpoint;
import com.bwj.trial.weather.model.AirportData;
import com.bwj.trial.weather.model.AtmosphericInformation;
import com.bwj.trial.weather.model.DataPoint;
import com.bwj.trial.weather.repository.MemoryOperator;
import com.bwj.trial.weather.repository.WeatherRepository;
import com.bwj.trial.weather.repository.WeatherRepositoryImpl;
import com.bwj.trial.weather.service.WeatherService;
import com.bwj.trial.weather.service.WeatherServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WeatherEndpointTest {

	private WeatherQueryEndpoint _query;

	private WeatherCollectorEndpoint _update;

	private Gson _gson = new Gson();

	private DataPoint _dp;

	@Before
	public void setUp() throws Exception {

		WeatherRepository weatherRepository = new WeatherRepositoryImpl();
		WeatherService WeatherService = new WeatherServiceImpl(weatherRepository);

		_query = new RestWeatherQueryEndpoint(WeatherService);
		_update = new RestWeatherCollectorEndpoint(WeatherService);

		MemoryOperator.INSTANCE.getAirportData().clear();
		MemoryOperator.INSTANCE.getAtmosphericInformation().clear();

		MemoryOperator.INSTANCE.getAirportData()
				.add(new AirportData("BOS", new BigDecimal(42.364347), new BigDecimal(-71.005181)));
		MemoryOperator.INSTANCE.getAirportData()
				.add(new AirportData("EWR", new BigDecimal(40.6925), new BigDecimal(-71.005181)));
		MemoryOperator.INSTANCE.getAirportData()
				.add(new AirportData("JFK", new BigDecimal(40.639751), new BigDecimal(-73.778925)));

		MemoryOperator.INSTANCE.getAtmosphericInformation().put("BOS", new AtmosphericInformation());
		MemoryOperator.INSTANCE.getAtmosphericInformation().put("EWR", new AtmosphericInformation());
		MemoryOperator.INSTANCE.getAtmosphericInformation().put("JFK", new AtmosphericInformation());

		_dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
		_update.updateWeather("BOS", "wind", _gson.toJson(_dp));
		_query.weather("BOS", "0").getEntity();
	}

	@Test
	public void testPing() throws Exception {
		String ping = _query.ping();
		JsonElement pingResult = new JsonParser().parse(ping);
		assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
		assertEquals(3, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGet() throws Exception {
		List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
		assertEquals(ais.get(0).getWind(), _dp);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetNearby() throws Exception {
		_update.updateWeather("JFK", "wind", _gson.toJson(_dp));
		_dp.setMean(40);

		List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
		assertEquals(1, ais.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() throws Exception {

		DataPoint windDp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22)
				.build();
		_update.updateWeather("BOS", "wind", _gson.toJson(windDp));
		_query.weather("BOS", "0").getEntity();

		String ping = _query.ping();
		JsonElement pingResult = new JsonParser().parse(ping);
		assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

		DataPoint cloudCoverDp = new DataPoint.Builder().withCount(4).withFirst(10).withMedian(60).withLast(100)
				.withMean(50).build();
		_update.updateWeather("BOS", "cloudcover", _gson.toJson(cloudCoverDp));

		List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
		assertEquals(ais.get(0).getWind(), windDp);
		assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
	}

}
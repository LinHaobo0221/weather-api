package com.bwj.trial.weather.model;

import java.util.Arrays;
import java.util.List;

/**
 * The various types of data points we can collect.
 *
 * @author code test administrator
 */
public enum DataPointType {
    WIND("WIND") {
        @Override
        public void updateAtmosphericInformation(AtmosphericInformation ai, DataPoint dp) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
            }
        }
    },
    TEMPERATURE("TEMPERATURE") {
        @Override
        public void updateAtmosphericInformation(AtmosphericInformation ai, DataPoint dp) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
            }
        }
    },
    HUMIDTY("HUMIDTY") {
        @Override
        public void updateAtmosphericInformation(AtmosphericInformation ai, DataPoint dp) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
            }
        }
    },
    PRESSURE("PRESSURE") {
        @Override
        public void updateAtmosphericInformation(AtmosphericInformation ai, DataPoint dp) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
            }
        }
    },
    CLOUDCOVER("CLOUDCOVER") {
        @Override
        public void updateAtmosphericInformation(AtmosphericInformation ai, DataPoint dp) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
            }
        }
    },
    PRECIPITATION("PRECIPITATION") {
        @Override
        public void updateAtmosphericInformation(AtmosphericInformation ai, DataPoint dp) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
            }
        }
    };
    
    private String value;

    DataPointType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public abstract void updateAtmosphericInformation(AtmosphericInformation ai, DataPoint dp);
    
    public static List<DataPointType> getAllDataPointTypes() {
       return  Arrays.asList(DataPointType.values());
    }
}

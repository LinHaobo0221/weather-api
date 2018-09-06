package com.bwj.trial.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * A simple airport loader which reads a file from disk and sends entries to the
 * webservice
 * <p>
 * TODO: Implement the Airport Loader
 *
 * @author code test administrator
 */
public class AirportLoader {

    /**
     * end point to supply updates
     */
    private final WebTarget collect;

    private static final String SEPARATOR = ",";

    public AirportLoader() {
        Client client = ClientBuilder.newClient();

        collect = client.target("http://localhost:9090/collect/airport/");
    }

    public void upload(BufferedReader reader) throws IOException {

        reader.lines().map(line -> Arrays.asList(line.split(SEPARATOR))).forEach(line -> {
            collect.path(line.get(4).replaceAll("\"", ""))
            .path(line.get(6))
            .path(line.get(7))
            .request().post(Entity.entity("", MediaType.APPLICATION_JSON));
        });

        reader.close();

    }

    public static void main(String args[]) {

        URL fileUrl = AirportLoader.class.getClassLoader().getResource("airports.dat");

        File dataFile = new File(fileUrl.getPath());

        AirportLoader loader = new AirportLoader();

        FileInputStream stream = null;
        BufferedReader reader = null;

        try {
            stream = new FileInputStream(dataFile);
            reader = new BufferedReader(new InputStreamReader(stream));
            loader.upload(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

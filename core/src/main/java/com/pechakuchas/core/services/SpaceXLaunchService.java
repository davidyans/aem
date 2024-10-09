package com.pechakuchas.core.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pechakuchas.core.models.SpaceXModel;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Component(
        service = SpaceXLaunchService.class,
        scope = ServiceScope.SINGLETON
)
public class SpaceXLaunchService {

    private static final Logger LOG = LoggerFactory.getLogger(SpaceXLaunchService.class);
    private static final String API_URL = "https://api.spacexdata.com/v4/launches";

    // Method that returns the list of SpaceXLaunch objects
    public List<SpaceXModel> getLaunches() {
        List<SpaceXModel> launchesList = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        int count_flag = 0;

        try {
            // Send the request and receive the response
            HttpResponse<String> apiResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Process the response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse.body());

            if (rootNode.isArray()) {
                for (JsonNode launch : rootNode) {
                    // Extract data for each launch and create SpaceXLaunch objects
                    String name = launch.get("name").asText();
                    String dateLocal = launch.get("date_local").asText();
                    Boolean success = launch.get("success") != null ? launch.get("success").asBoolean() : null;
                    String details = launch.get("details") != null ? launch.get("details").asText() : "N/A";

                    // Add the launch to the list
                    SpaceXModel spaceXLaunch = new SpaceXModel(name, dateLocal, success, details);
                    launchesList.add(spaceXLaunch);

                    count_flag++;

                    if (count_flag >= 30) break;

                    //System.out.println("ARRAY LISTTTTTTTT -|||||  " + spaceXLaunch.getName() + " ---- " + spaceXLaunch.getDateLocal()
                    //+ " ---- " + spaceXLaunch.getSuccess() + " ---- " + spaceXLaunch.getDetails());
                }
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Error calling SpaceX API", e);
        }

        return launchesList;
    }
}

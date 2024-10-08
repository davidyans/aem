package com.pechakuchas.core.servlets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.apache.jackrabbit.JcrConstants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths(
        value = {"/pechakuchas/api/launches"}
)
public class CHServlet extends SlingSafeMethodsServlet {

    private static final String API_URL = "https://api.spacexdata.com/v5/launches";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {

        final Resource resource = request.getResource();
        ValueMap valueMap = resource.getValueMap();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(API_URL);

        try (CloseableHttpResponse apiResponse = httpClient.execute(httpGet)) {
            String weatherData = EntityUtils.toString(apiResponse.getEntity(), "UTF-8");

            response.setContentType("application/json");
            response.getWriter().write(weatherData);
        } catch (Exception e) {
            response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error calling weather API");
        }

        response.getWriter().write("\nTitle = " + valueMap.get(JcrConstants.JCR_NAME));
    }
}
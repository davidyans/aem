package com.pechakuchas.core.servlets;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.pechakuchas.core.models.SpaceXModel;
import com.pechakuchas.core.services.SpaceXLaunchService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = Servlet.class)
@SlingServletPaths(
        value = {"/pechakuchas/api/launches"}
)
public class SpaceXContentFragmentServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SpaceXContentFragmentServlet.class);
    private static final String MODEL_PATH = "/conf/pechakuchas/settings/dam/cfm/models/weather-cf-model";
    private static final String PARENT_PATH = "/content/dam/pechakuchas/content-fragments/en/";
    private static final String CONTENT_FRAGMENT_FOLDER = "/content/dam/pechakuchas/content-fragments/en";

    @Reference
    private SpaceXLaunchService spaceXLaunchService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Fetch launches from SpaceX API
            List<SpaceXModel> launches = spaceXLaunchService.getLaunches();

            // Get the ResourceResolver from the request
            ResourceResolver resourceResolver = request.getResourceResolver();

            // Load the Content Fragment model template
            Resource templateOrModelRsc = resourceResolver.getResource(MODEL_PATH);

            // Deletes all content fragments
            try {
                Resource parentResource = resourceResolver.getResource(CONTENT_FRAGMENT_FOLDER);
                if (parentResource != null) {
                    Iterator<Resource> children = parentResource.listChildren();
                    while (children.hasNext()) {
                        Resource child = children.next();
                        resourceResolver.delete(child);
                    }
                    resourceResolver.commit();
                } else {
                    System.out.println("No se encontró el recurso padre en la ruta: " + CONTENT_FRAGMENT_FOLDER);
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            }

            if (templateOrModelRsc == null) {
                LOG.error("Content Fragment model template not found at: {}", MODEL_PATH);
                response.sendError(SlingHttpServletResponse.SC_NOT_FOUND, "Content Fragment model template not found");
                return;
            }

            // Get the parent resource where content fragments will be created
            Resource parentRsc = resourceResolver.getResource(PARENT_PATH);
            if (parentRsc == null) {
                LOG.error("Parent resource not found at: {}", PARENT_PATH);
                response.sendError(SlingHttpServletResponse.SC_NOT_FOUND, "Parent resource not found");
                return;
            }

            // Adapt the resource to a FragmentTemplate
            FragmentTemplate tpl = templateOrModelRsc.adaptTo(FragmentTemplate.class);
            if (tpl == null) {
                LOG.error("Could not adapt resource to FragmentTemplate");
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not adapt resource to FragmentTemplate");
                return;
            }

            // Create content fragments for each launch
            for (SpaceXModel launch : launches) {
                try {
                    // Create a new content fragment for each launch
                    String fragmentTitle = "cf-spaceX"; // Unique title
                    ContentFragment newFragment = tpl.createFragment(parentRsc, fragmentTitle, "SpaceX Launch");

                    if (newFragment != null) {
                        // Fill the weather (text) field
                        ContentElement nameElement = newFragment.getElement("name");
                        if (nameElement != null) {
                            nameElement.setContent(launch.getName(), "text/plain");

                            System.out.println("======= name" + launch.getDetails());
                        }

                        ContentElement dateElement = newFragment.getElement("datelocal");
                        if (dateElement != null) {
                            dateElement.setContent(launch.getDateLocal(), "text/plain");
                        }

                        ContentElement successElement = newFragment.getElement("success");
                        if (successElement != null) {
                            successElement.setContent(launch.getSuccess().toString(), "text/plain");
                        }

                        ContentElement detailsElement = newFragment.getElement("details");
                        if (detailsElement != null) {
                            detailsElement.setContent(launch.getDetails(), "text/plain");
                        }

                        // Commit changes to the resource resolver
                        resourceResolver.commit();
                        LOG.info("Content Fragment created successfully: {}", fragmentTitle);
                    } else {
                        LOG.error("Failed to create content fragment for launch: {}", launch.getName());
                    }

                } catch (ContentFragmentException | PersistenceException e) {
                    LOG.error("Error while creating content fragment for launch: {}", launch.getName(), e);
                }
            }

            response.getWriter().write("Content Fragments created successfully.");
        } catch (Exception e) {
            LOG.error("Error in creating content fragments", e);
            response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in creating content fragments");
        }
    }
}
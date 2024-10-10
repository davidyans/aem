package com.pechakuchas.core.servlets;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.ContentFragmentManager;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.framework.Constants;

import javax.servlet.Servlet;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import com.adobe.cq.dam.cfm.ContentFragment;

@Component(
        service = { Servlet.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=Java Stream Console Servlet with HttpClient",
                "sling.servlet.methods=GET",
                "sling.servlet.paths=/bin/streamconsole/execute3"
        }
)
public class FragmentServlet extends SlingAllMethodsServlet {

}
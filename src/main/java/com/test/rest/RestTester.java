package com.test.rest;

import com.credentials.Credentials;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by oksdud on 27.07.2016.
 */
public class RestTester {
    public static Boolean GLASSFISH_ON = false;
    public static Client client;
    public static String current;
    public static String resources;
    public static String applicationName;
    public static String warAbsolutePath;
    public static String nameConfig = "ecom-dev.properties";

    public static void getCurrentDirs() {
        try {
            current = new File(".").getCanonicalPath();
            current = current.replaceAll("\\\\", "/");
            resources = current + "/src/test/resources";
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static Context ctx;

    private static EJBContainer container;
    public static Map<String, NewCookie> cookieMap;

    public static Map<String, NewCookie> getCookieMap() {
        return cookieMap;
    }

    public static Context getContext() {
        return ctx;
    }

    public static void startContainer() {
        System.setProperty("openejb.validation.output.level", "VERBOSE");
        System.setProperty("openejb.jpa.auto-scan", "true");
        System.setProperty("openejb.embedded.initialcontext.close", "DESTROY");

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.openejb.client.LocalInitialContextFactory");

        String pathToGlassfishRoot = System.getenv("GLASSFISH_HOME");

        if (pathToGlassfishRoot == null || pathToGlassfishRoot.isEmpty()) {
            System.out.println("You have input environment variable GLASSFISH_HOME");
            GLASSFISH_ON = false;
        } else GLASSFISH_ON = true;
        if (!GLASSFISH_ON) return;

        client = ClientBuilder.newClient().register(JacksonFeature.class);


        pathToGlassfishRoot = pathToGlassfishRoot + File.separator + "glassfish" + File.separator;
        properties.put(
                "org.glassfish.ejb.embedded.glassfish.installation.root",
                pathToGlassfishRoot
        );
        String pathToDomain = pathToGlassfishRoot + "domains" + File.separator + "backend-dom1";
        properties.put(
                "org.glassfish.ejb.embedded.glassfish.instance.root",
                pathToDomain
        );
        properties.put(
                "org.glassfish.ejb.embedded.glassfish.configuration.file",
                pathToDomain + File.separator + "config" + File.separator + "domain.xml"
        );

        properties.put(EJBContainer.APP_NAME, applicationName);

        if (warAbsolutePath != null && !warAbsolutePath.isEmpty()) {
            final File file = new File(warAbsolutePath);
            if (!file.mkdirs() && !file.exists()) {
                throw new RuntimeException("can't create " + file.getAbsolutePath());
            }
            // properties.put(DeploymentsResolver.CLASSPATH_INCLUDE, pathToDomain + File.separator +warAbsolutePath);
            properties.put(EJBContainer.MODULES, warAbsolutePath);//war.getAbsolutePath()
        }
        String fileName = "";

        fileName = System.getenv("lpb.ecom.config");

        if (fileName == null || fileName.isEmpty()) {
            System.out.println("You have input environment variable lpb.ecom.config with value path to file ecom-dev.properties");
            try {
                fileName = pathToDomain + File.separator + "config" + File.separator + nameConfig;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        System.setProperty("user.script", fileName);

        container = EJBContainer.createEJBContainer(properties);//
        ctx = container.getContext();
/*
        try {

            transactionalCaller = (Caller)
                    ctx.lookup("java:global/ecom-rest/test/TransactionBean");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
*/

    }

    public static URI getBaseURI(String url) {
        return UriBuilder.fromUri(url).build();
    }

    public static void setSessionUserNamePassword(String urlForLogin) {
        if (!GLASSFISH_ON) return;
        try {
            autorization(urlForLogin, Credentials.getUserName(Credentials.MERCHANT), Credentials.MERCHANT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void setSessionUserNamePassword(String urlForLogin, String userName, String password) {
        if (!GLASSFISH_ON) return;
        try {
            autorization(urlForLogin, userName, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void autorization(String urlForLogin, String userName, String key) {
        try {
            WebTarget target = RestTester.getClient().target(getBaseURI(urlForLogin));
            Response clientResponse = target
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json("{\"name\":\"" + userName + "\" ,\"password\":\"" + Credentials.getUserPassword(key) + "\"}"), Response.class);
            cookieMap = clientResponse.getCookies();
            System.out.println("Received cookie: " + cookieMap.get("JSESSIONID").getValue());

        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            if (e.getMessage() == null) {
                System.out.println("Module ecom-rest undeployed");
            }
            System.out.println(e.getCause().toString());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                RestTester.GLASSFISH_ON = false;
            }

        }

    }

    public static void checkResource(
            String urlForResource,
            String nameRequest,
            String pathInResource,
            String parameterToResource,
            Entity entity
    ) {
        if (!GLASSFISH_ON) return;
        try {
            // setArguments(urlForResource,urlForLogin,"get",null,"id","21213832");
            WebTarget target = RestTester.getClient().target(getBaseURI(urlForResource));
            if (pathInResource != null) {
                if (parameterToResource == null)
                    target = target.path(pathInResource);
                else
                    target = target.path("{" + pathInResource + "}")
                            .resolveTemplate(pathInResource, parameterToResource);
            }
            System.out.println("Received cookie: " + cookieMap);
            Invocation.Builder invocationBuilder;
            if (cookieMap.isEmpty()) {
                invocationBuilder = target
                        .request(MediaType.APPLICATION_JSON);

            } else {
                invocationBuilder = target
                        .request(MediaType.APPLICATION_JSON)
                        .cookie("JSESSIONID", cookieMap.get("JSESSIONID").getValue());
            }
            Response clientResponse = null;
            switch (nameRequest) {
                case "GET":
                case "get":
                    clientResponse = invocationBuilder.get();
                    break;
                case "PUT":
                case "put":
                    clientResponse = invocationBuilder.put(entity);
                    break;
                case "POST":
                case "post":
                    clientResponse = invocationBuilder.post(Entity.entity(entity, MediaType.APPLICATION_JSON), Response.class);
                    break;
                default:
                    nameRequest = "get";
                    clientResponse = invocationBuilder.get();
                    break;
            }
            String jsonResponse = clientResponse.readEntity(String.class);
            System.out.println("Received response from persister: " + jsonResponse);
        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            System.out.println(e.getCause());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                RestTester.GLASSFISH_ON = false;
            }

        }

        return;
    }

    public static Boolean getGlassfishOn() {
        return GLASSFISH_ON;
    }

    public static Client getClient() {
        return client;
    }

    public static String getCurrent() {
        return current;
    }

    public static String getResources() {
        return resources;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static String getNameConfig() {
        return nameConfig;
    }

    public static void setGlassfishOn(Boolean glassfishOn) {
        GLASSFISH_ON = glassfishOn;
    }

    public static void setClient(Client client) {
        RestTester.client = client;
    }

    public static void setCurrent(String current) {
        RestTester.current = current;
    }

    public static void setResources(String resources) {
        RestTester.resources = resources;
    }

    public static void setApplicationName(String applicationName) {
        RestTester.applicationName = applicationName;
    }

    public static void setNameConfig(String nameConfig) {
        RestTester.nameConfig = nameConfig;
    }

    public static String getWarAbsolutePath() {
        return warAbsolutePath;
    }

    public static void setWarAbsolutePath(String warAbsolutePath) {
        RestTester.warAbsolutePath = warAbsolutePath;
    }

    public static void main(String[] args) {

    }
}

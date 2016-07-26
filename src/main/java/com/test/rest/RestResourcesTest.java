package com.test.rest;


import com.credentials.Credentials;
import com.test.forTransaction.Caller;
import com.test.json.JacksonFeature;
import com.test.orderTests.Order;
import com.test.orderTests.OrderRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
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
 * Created by oksdud on 28.06.2016.
 */
@RunWith(OrderRunner.class)
@Slf4j
public class RestResourcesTest {
    private static Boolean GLASSFISH_ON = false;
    private static String merchantCode = "3720000";
    private static Client client;
    private static String current;
    private static String resources;
    private static String nameXmlFile;
    private static String urlForResource;
    private static String urlForLogin;
    private static String applicationName;
    private static String nameConfig = "ecom-dev.properties";
    private static String nameRequest;
    private static String pathInResource;
    private static String parameterToResource;

    static {
        try {
            current = new File(".").getCanonicalPath();
            current = current.replaceAll("\\\\", "/");
            resources = current + "/src/main/resources";
            nameXmlFile = resources + "/data.xml";
            nameXmlFile = nameXmlFile.replaceAll("\\\\", "/");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static Entity<?> entity = null;

    public RestResourcesTest(String applicationName, String urlForResource, String urlForLogin, String nameConfig, String nameRequest, Entity<?> entity,
                             String pathInResource,
                             String parameterToResource

    ) {
        this.urlForResource = urlForResource;
        this.urlForLogin = urlForLogin;
        this.applicationName = applicationName;
        this.nameConfig = nameConfig;
        this.nameRequest = nameRequest;
        this.entity = entity;
        this.pathInResource = pathInResource;
        this.parameterToResource = parameterToResource;
    }

    private static String jsonParams = "";

    /*
    @EJB
    private static PaymentHelper paymentHelper;
*/
    @EJB
    private static Caller transactionalCaller;

    public static Context ctx;
    private static EJBContainer container;

    public static Context getContext() {
        return ctx;
    }

    @BeforeClass
    public static void prepareData() {
        Map<String, Object> properties = new HashMap<String, Object>();

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

    /*
        test SessionResource
    */
    private static Map<String, NewCookie> cookieMap;

    @Test
    @Order(order = 0)
    public void setSessionUserNamePassword() {
        if (!GLASSFISH_ON) return;
        try {
            autorization(Credentials.getUserName(Credentials.ADMIN), Credentials.ADMIN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    @Order(order = 1)
    public boolean checkResource() {
        if (!GLASSFISH_ON) return false;
        try {
            WebTarget target = client.target(getBaseURI(urlForResource));
            if(pathInResource!=null){
               if(parameterToResource==null)
                  target=target.path(pathInResource);
               else
                   target=target.path("{"+pathInResource+"}").resolveTemplate(pathInResource,parameterToResource);
            }
            System.out.println("Received cookie: " + cookieMap);
            Invocation.Builder invocationBuilder = target
                    .request(MediaType.APPLICATION_JSON)
                    .cookie("JSESSIONID", cookieMap.get("JSESSIONID").getValue());
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
                GLASSFISH_ON = false;
            }

        }

        return true;
    }


    private void autorization(String userName, String key) {
        try {
            WebTarget target = client.target(getBaseURI(urlForLogin));
            Response clientResponse = target
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json("{\"name\":\"" + userName + "\" ,\"password\":\"" + Credentials.getUserPassword(key) + "\"}"), Response.class);
            cookieMap = clientResponse.getCookies();
            System.out.println("Received cookie: " + cookieMap.get("JSESSIONID").getValue());

        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            System.out.println(e.getCause().toString());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                GLASSFISH_ON = false;
            }

        }

    }


    //@Test
    //@Order(order = 3)
/*
    public void checkPaymentStateResource() {
        if (!GLASSFISH_ON) return;
        try {
            System.out.println("nameXmlFile = " + nameXmlFile);
            WebTarget target = client.target(getBaseURI("http://localhost:8080/payment"))
                    .path("/status").path("/{nameFile}")
                    .resolveTemplate("nameFile", nameXmlFile);
            Object paymentDTO = new PaymentDTO();
            System.out.println("Received cookie: " + cookieMap);
            Response clientResponse = target
                    .request(MediaType.APPLICATION_JSON)
                    .cookie("JSESSIONID", cookieMap.get("JSESSIONID").getValue())
                    .post(Entity.entity(paymentDTO, MediaType.APPLICATION_JSON), Response.class);
            String jsonResponse = clientResponse.readEntity(String.class);
            System.out.println("Received response from persister: " + jsonResponse);
        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            System.out.println(e.getCause());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                GLASSFISH_ON = false;
            }

        }


    }
*/

    //@Test
    //@Order(order = 4)
/*
    public void checkPaymentDepositResource() {
        if (!GLASSFISH_ON) return;
        try {
            System.out.println("nameXmlFile = " + nameXmlFile);
            WebTarget target = client.target(getBaseURI("http://localhost:8080/payment"))
                    .path("/deposit").path("/{nameFile}")
                    .resolveTemplate("nameFile", nameXmlFile);
            Object paymentDTO = new PaymentDTO();
            System.out.println("Received cookie: " + cookieMap);
            Response clientResponse = target
                    .request(MediaType.APPLICATION_JSON)
                    .cookie("JSESSIONID", cookieMap.get("JSESSIONID").getValue())
                    .post(Entity.entity(paymentDTO, MediaType.APPLICATION_JSON), Response.class);
            String jsonResponse = clientResponse.readEntity(String.class);
            System.out.println("Received response from persister: " + jsonResponse);
        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            System.out.println(e.getCause());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                GLASSFISH_ON = false;
            }

        }


    }
*/

    /*
        only for admin testing method PaymentResource changeStateExistingPayment
    */
    //@Test
    //@Order(order = 1)
/*
    public void checkPaymentResource() {
        if (!GLASSFISH_ON) return;
        try {
            WebTarget target = client.target(getBaseURI())
                    .path("/{idPayment}")
                    .path("/state")
                    .path("/{state}")
                    .resolveTemplate("idPayment", "21195528")
                    .resolveTemplate("state", "5");
            Object paymentDTO = new PaymentDTO();
            System.out.println("Received cookie: " + cookieMap);
            Response clientResponse = target
                    .request(MediaType.APPLICATION_JSON)
                    .cookie("JSESSIONID", cookieMap.get("JSESSIONID").getValue())
                    .put(Entity.entity(paymentDTO, MediaType.APPLICATION_JSON), Response.class);
            String jsonResponse = clientResponse.readEntity(String.class);
            System.out.println("Received response from persister: " + jsonResponse);
        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            System.out.println(e.getCause());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                GLASSFISH_ON = false;
            }

        }


    }
*/
/*
    only for merchant-user testing method PaymentResource findApprovedPaymentsCount
*/

    //  String urlForGetApprovedPayments = "http://localhost:8080/rest/payment";

    //@Test
    //@Order(order = 2)
/*
    public void testCountApprovedPayments() {
        if (!GLASSFISH_ON) return;
        try {
            autorization(Credentials.getUserName(Credentials.MERCHANT), Credentials.MERCHANT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            WebTarget target = client.target(getBaseURI(urlForGetApprovedPayments))
                    .path("approvedPaymentsCount");
            Long count = 0L;
            Response clientResponse = target
                    .request(MediaType.APPLICATION_JSON)
                    .cookie("JSESSIONID", cookieMap.get("JSESSIONID").getValue())
                    .get();
            String jsonResponse = clientResponse.readEntity(String.class);
            System.out.println("Received response from persister: " + jsonResponse);
        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            System.out.println(e.getCause());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                GLASSFISH_ON = false;
            }

        }

    }
*/
    private static URI getBaseURI(String url) {
        return UriBuilder.fromUri(url).build();
    }

    @AfterClass
    public static void destroyContainer() {
        if (client != null) {
            client.close();
        }

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

}

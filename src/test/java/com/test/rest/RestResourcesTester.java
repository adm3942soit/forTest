package com.test.rest;


import com.credentials.Credentials;
import com.test.orderTests.Order;
import com.test.orderTests.OrderRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.naming.Context;
import javax.ws.rs.client.*;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;

/**
 * Created by oksdud on 28.06.2016.
 */
@RunWith(OrderRunner.class)
@Slf4j

public class RestResourcesTester {
    private static String nameXmlFile;
    private static String urlForResource;
    private static String urlForLogin;
    private static String nameRequest;
    private static String pathInResource;
    private static String parameterToResource;
    private static Client client;
    public static String getNameXmlFile() {
        return nameXmlFile;
    }

    public static void setNameXmlFile(String nameXmlFile) {
        RestResourcesTester.nameXmlFile = nameXmlFile;
    }

    public static String getUrlForResource() {
        return urlForResource;
    }

    public static void setUrlForResource(String urlForResource) {
        RestResourcesTester.urlForResource = urlForResource;
    }

    public static String getUrlForLogin() {
        return urlForLogin;
    }

    public static void setUrlForLogin(String urlForLogin) {
        RestResourcesTester.urlForLogin = urlForLogin;
    }

    public static String getNameRequest() {
        return nameRequest;
    }

    public static void setNameRequest(String nameRequest) {
        RestResourcesTester.nameRequest = nameRequest;
    }

    public static String getPathInResource() {
        return pathInResource;
    }

    public static void setPathInResource(String pathInResource) {
        RestResourcesTester.pathInResource = pathInResource;
    }

    public static String getParameterToResource() {
        return parameterToResource;
    }

    public static void setParameterToResource(String parameterToResource) {
        RestResourcesTester.parameterToResource = parameterToResource;
    }

    public static Entity<?> getEntity() {
        return entity;
    }

    public static void setEntity(Entity<?> entity) {
        RestResourcesTester.entity = entity;
    }

    public static String getUrlPaymentResource() {
        return urlPaymentResource;
    }

    public static void setUrlPaymentResource(String urlPaymentResource) {
        RestResourcesTester.urlPaymentResource = urlPaymentResource;
    }

    public static String getUrlSessionResource() {
        return urlSessionResource;
    }

    public static void setUrlSessionResource(String urlSessionResource) {
        RestResourcesTester.urlSessionResource = urlSessionResource;
    }


    static {
        RestTester.getCurrentDirs();
        try {
            nameXmlFile = RestTester.resources + "/data.xml";
            nameXmlFile = nameXmlFile.replaceAll("\\\\", "/");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static Entity<?> entity = null;

    public static void setArguments(String applicationName, String urlForResource, String urlForLogin, String nameConfig, String nameRequest, Entity<?> entity,
                               String pathInResource,
                               String parameterToResource

    ) {
        setUrlForResource(urlForResource);
        setUrlForLogin(urlForLogin);
        setNameRequest(nameRequest);
        setEntity(entity);
        setPathInResource(pathInResource);
        setParameterToResource(parameterToResource);
        RestTester.setApplicationName(applicationName);;
        RestTester.setNameConfig(nameConfig);
    }
    public static void setArguments(String urlForResource, String urlForLogin, String nameRequest, Entity<?> entity,
                                    String pathInResource,
                                    String parameterToResource

    ) {
        setUrlForResource(urlForResource);
        setUrlForLogin(urlForLogin);
        setNameRequest(nameRequest);
        setEntity(entity);
        setPathInResource(pathInResource);
        setParameterToResource(parameterToResource);
    }

    private static String urlPaymentResource="http://localhost:8080/rest/payment";
    private static String urlSessionResource="http://localhost:8080/rest/session";

    public static void setResources(String applicationName,String nameConfig

    ) {
        RestTester.setApplicationName(applicationName);;
        RestTester.setNameConfig(nameConfig);
    }


    public static Context getContext() {
        return RestTester.getContext();
    }

    @BeforeClass
    public static void prepareData() {
        setResources("ecom-rest","ecom-dev.properties");
        RestTester.setWarAbsolutePath(null);
        RestTester.startContainer();
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


    @Test
    @Order(order = 0)
    public void setSessionUserNamePassword() {
        if (!RestTester.GLASSFISH_ON) return;
        try {
            //setArguments(urlPaymentResource, urlSessionResource,"get",null,"id","21213832");
            //RestTester.setSessionUserNamePassword(urlForLogin, Credentials.getUserName(Credentials.MERCHANT), Credentials.MERCHANT);
            String userName=Credentials.getUserName(Credentials.MERCHANT);
            String password=Credentials.getUserPassword(Credentials.MERCHANT);
            RestTester.checkResource(urlSessionResource,"post",null,null,
                    "{\"name\":\"" +userName  + "\" ,\"password\":\"" + password + "\"}", String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    @Order(order = 1)
    public void checkResource() {
        if (!RestTester.GLASSFISH_ON) return;
        try {
            setArguments(urlPaymentResource, urlSessionResource,"get",null,"id","21213832");
            RestTester.checkResource(urlForResource,"get","id","21213832",null, String.class);
        } catch (Exception e) {
            System.out.println("\n\tGot exception: " + e.getMessage());
            if(e.getMessage()==null){
                System.out.println("Module ecom-rest undeployed");
            }

            System.out.println(e.getCause());
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.out.println("Glassfish server not started");
                RestTester.GLASSFISH_ON = false;
            }

        }


        return ;
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
        if (RestTester.client != null) {
            RestTester.client.close();
        }

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

}

package com.test.xml;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Created by Oksana Dudnik on 12.05.2016.
 */

public class ConverterFromXMLToObject {


    private static String current;
    private static String resources;

    static {
        try {
            current = new File(".").getCanonicalPath();
            current = current.replaceAll("\\\\", "/");
            resources = current + "/src/test/resources";
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static String nameXmlFile = resources + File.separator + "data.xml";

    public ConverterFromXMLToObject() {

    }

    public static <E> E fromXML(String nameXMLFile, Class<E> clazz) {
        try {
            File file = new File(nameXMLFile);
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz.getClass());

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            E object = (E)jaxbUnmarshaller.unmarshal(file);
            return object;

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    return null;
    }
public static void main(String[] args){
    File file = new File(nameXmlFile);
}
    public String getNameXmlFile() {
        return nameXmlFile;
    }
}

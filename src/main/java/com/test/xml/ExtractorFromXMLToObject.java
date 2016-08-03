package com.test.xml;


import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Created by Oksana Dudnik on 12.05.2016.
 */
@Stateless(name = "ExtractorFromXMLToObject")
public class ExtractorFromXMLToObject {
    public ExtractorFromXMLToObject() {

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
}

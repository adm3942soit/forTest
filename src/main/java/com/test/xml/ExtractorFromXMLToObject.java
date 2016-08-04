package com.test.xml;


import javax.ejb.Stateless;
import javax.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import java.io.*;

import static javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES;
import static javax.xml.stream.XMLInputFactory.SUPPORT_DTD;

/**
 * Created by Oksana Dudnik on 12.05.2016.
 */
@Stateless(name = "ExtractorFromXMLToObject")
public class ExtractorFromXMLToObject {
    public ExtractorFromXMLToObject() {

    }
    private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    static {
        inputFactory.setProperty(SUPPORT_DTD, false);
        inputFactory.setProperty(IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    public Object fromXML(String nameXMLFile, Class<?> clazz) {
        File file = new File(nameXMLFile);
        try(final Reader sr = new FileReader(file)) {

            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
           // E object = (E)jaxbUnmarshaller.unmarshal(file);
            return JAXBIntrospector.getValue(jaxbUnmarshaller.unmarshal(inputFactory.createXMLStreamReader(sr),clazz));

        } catch (Exception e) {
            e.printStackTrace();
        }
    return null;
    }
    public void toXML(String nameXMLFile, Class<?> clazz, Object objData) {
        File file = new File(nameXMLFile);
        try(final Reader sr = new FileReader(file)) {

            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
           jaxbMarshaller.marshal(objData, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

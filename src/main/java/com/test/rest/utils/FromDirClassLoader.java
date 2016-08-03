package com.test.rest.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by oksdud on 03.08.2016.
 */
public class FromDirClassLoader extends ClassLoader {
    public FromDirClassLoader() {
        super();
    }

    public  Object loadClassFromDir(String className) {
        try {
            Class clazz = super.getParent().loadClass(className);
            Object o = clazz.newInstance();
            return o;
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }
    public Object loadClassFromDir(String dirName, String className){
        // Create a File object on the root of the directory containing the class file
        File file = new File(dirName);

        try {
            // Convert File to a URL
            URL url = file.toURL();          // file:/c:/myclasses/
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);

            // Load in the class; MyClass.class should be located in
            // the directory file:/c:/myclasses/com/mycompany
            Class clazz = cl.loadClass(className);//"com.mycompany.MyClass"
            Object object = clazz.newInstance();
            return object;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch(InstantiationException ex){
            ex.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }
}

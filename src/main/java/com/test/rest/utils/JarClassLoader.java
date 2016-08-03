package com.test.rest.utils;

/**
 * Created by oksdud on 03.08.2016.
 */
public class JarClassLoader extends MultiClassLoader {
    private JarResources jarResources;

    public JarClassLoader(String jarName) {
        // Create the JarResource and suck in the jar file.
        jarResources = new JarResources(jarName);
    }

    protected byte[] loadClassBytes(String className) {
        // Support the MultiClassLoader's class name munging facility.
        className = formatClassName(className);
        // Attempt to get the class data from the JarResource.
        return (jarResources.getResource(className));
    }

    public static Object getObjectClass(String nameJar, Class clazz) {
            /*
         * Create the jar class loader and use the first argument
         * passed in from the command line as the jar file to use.
         */
        JarClassLoader jarLoader = new JarClassLoader(nameJar);

        try {
             /* Load the class from the jar file and resolve it. */
            Class c = jarLoader.loadClass(nameJar, true);
        /*
         * Create an instance of the class.
         *
         * Note that created object's constructor-taking-no-arguments
         * will be called as part of the object's creation.
         */
            Object o = c.newInstance();
        /* Are we using a class we specifically know about? */
/*
                if (o instanceof clazz) {
                    // Yep, lets call a method  we know about.  *//*

                    TestClass tc = (TestClass) o;
                    tc.doSomething();
                }
*/
            return o;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}

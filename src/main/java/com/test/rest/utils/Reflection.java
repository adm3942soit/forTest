/**
 * Copyright (c) 2011 by Alistair A. Israel
 *
 * This software is made available under the terms of the MIT License.
 *
 * Created Jan 10, 2011
 */
package com.test.rest.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * @author Alistair A. Israel
 */
public final class Reflection {

    /**
     *
     */
    private Reflection() {
        // Utility classes should not have a public or default constructor.
    }

    /**
     * Quietly get the field value from the target object.
     *
     * @param obj
     *        the target object
     * @param field
     *        the {@link Field} to set
     * @return the field value
     */
    public static Object getField(final Object obj, final Field field) {
        final boolean accessible = field.isAccessible();
        if (!accessible) {
            field.setAccessible(true);
        }
        try {
            return field.get(obj);
        } catch (final IllegalArgumentException e) {
            System.out.println("IllegalArgumentException setting field " + field.getName() + " on " + obj);
        } catch (final IllegalAccessException e) {
            System.out.println("IllegalAccessException setting field " + field.getName() + " on " + obj);
        } finally {
            if (!accessible) {
                field.setAccessible(false);
            }
        }
     return null;
    }


    /**
     * Invoke a method, if necessary making it accessible first (for non-public
     * methods).
     *
     * @param obj
     *        the target object
     * @param method
     *        the method
     * @param args
     *        the method arguments
     * @return the return value
     */
    public static Object invoke(final Object obj, final Method method, final Object... args) {
        final boolean accesible = method.isAccessible();
        if (!accesible) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(obj, args);
        } catch (final IllegalArgumentException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        } catch (final IllegalAccessException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        } catch (final InvocationTargetException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        } finally {
            if (!accesible) {
                method.setAccessible(false);
            }
        }
    return null;
    }
    /**
     * @param clazz
     *        the Class whose hierarchy should be traversed
     * @return {@link Iterable}
     */
    public Iterable<Class<?>> iterateClassHierarchy(final Class<?> clazz) {
        return new Iterable<Class<?>>() {
            public Iterator<Class<?>> iterator() {
                return new ClassHierarchyIterator(clazz);
            }
        };
    }

    /**
     * @author Alistair.Israel
     */
    private static final class ClassHierarchyIterator extends ReadOnlyIterator<Class<?>> {

        private Class<?> clazz;

        /**
         * @param clazz
         *        the starting Class
         */
        private ClassHierarchyIterator(final Class<?> clazz) {
            this.clazz = clazz;
        }

        /**
         * {@inheritDoc}
         *
         * @see Iterator#hasNext()
         */
        public boolean hasNext() {
            return clazz.getSuperclass() != null;
        }

        /**
         * {@inheritDoc}
         *
         * @see Iterator#next()
         */
        public Class<?> next() {
            final Class<?> next = clazz;
            clazz = clazz.getSuperclass();
            return next;
        }
    }

}

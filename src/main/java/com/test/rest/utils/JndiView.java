package com.test.rest.utils;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Reference;
import org.springframework.jndi.JndiCallback;
import org.springframework.jndi.JndiTemplate;
import org.springframework.util.ReflectionUtils;

/**
 * Created by oksdud on 03.08.2016.
 */
public class JndiView {

    private static final Logger logger = Logger.getLogger(JndiView.class.getCanonicalName());

    /**
     * {@value #JAVA_GLOBAL}
     */
    public static final String JAVA_GLOBAL = "java:global";
    private static final JndiTemplate jndiTemplate = new JndiTemplate();

    public static List<JndiEntry> browse(final String path) throws NamingException {
        final JndiCallback<List<JndiEntry>> contextCallback = new JndiCallback<List<JndiEntry>>() {
            @Override
            public List<JndiEntry> doInContext(final Context context) throws NamingException {
                if (JAVA_GLOBAL.equals(path)) {
                    // Do a little trick to handle "java:global"
                    final NamingEnumeration<Binding> root = context.listBindings("");
                    Context javaGlobalContext = null;
                    while (root.hasMore()) {
                        final Binding binding = root.next();
                        if (JAVA_GLOBAL.equals(binding.getName())) {
                            final Object obj = binding.getObject();
                            if (obj instanceof Context) {
                                javaGlobalContext = (Context) obj;
                            }
                            break;
                        }
                    }
                    if (javaGlobalContext != null) {
                        return examineBindings(javaGlobalContext, path, javaGlobalContext.listBindings(""));
                    }
                    logger.warning("Unable to browse \"" + JAVA_GLOBAL + "\" namespace!");
                    return emptyList();
                }
                return examineBindings(context, path, context.listBindings(path));
            }
        };
        return jndiTemplate.execute(contextCallback);
    }

    private static List<JndiEntry> examineBindings(final Context ctx, final String path,
                                            final NamingEnumeration<Binding> bindings) throws NamingException {
        if (null == bindings) {
            throw new NullPointerException("bindings is null!");
        }
        final List<JndiEntry> entries = new ArrayList<JndiEntry>();
        while (bindings.hasMore()) {
            final Binding binding = bindings.next();
            final String name = binding.getName();
            final String className = binding.getClassName();

            //logger.finest("name: " + name + " [" + className + "]");
            System.out.println("name: " + name + " [" + className + "]");
            final JndiEntry entry = new JndiEntry(name, className);
            final Object obj = binding.getObject();
            if (obj instanceof Context) {
                entry.setContext(true);
                String link = name;
                if (!path.isEmpty()) {
                    link = path + "/" + name;
                }
                entry.setLink(link);
            } else if (obj instanceof Reference) {
                final Reference ref = (Reference) obj;
                entry.setTargetClassName(ref.getClassName());
            } else if ("org.glassfish.javaee.services.ResourceProxy".equals(className)) {
                // SUPPRESS CHECKSTYLE AvoidInlineConditionals
                final Object lookup = ctx.lookup(path.isEmpty() ? name : path + "/" + name);
                if (lookup != null) {
                    final String lookedUpClassName = lookup.getClass().getName();
                  //  logger.finest("lookup(\"" + name + "\") returned " + lookedUpClassName);
                    System.out.println("lookup(\"" + name + "\") returned " + lookedUpClassName);
                    entry.setTargetClassName(lookedUpClassName);
                }
            } else if ("com.sun.ejb.containers.JavaGlobalJndiNamingObjectProxy".equals(className)) {
                inspectJndiNamingObjectProxy(entry, obj);
            }
            entries.add(entry);
        }
        return entries;
    }

    private static void inspectJndiNamingObjectProxy(final JndiEntry entry, final Object obj) {
        final Field f = ReflectionUtils.findField(obj.getClass(), "intfName");
        if (f != null) {
            final Object v = Reflection.getField(obj, f);
            //logger.finest("intfName: " + v);
            System.out.println("intfName: " + v);
            entry.setTargetClassName(v.toString());
        }
    }
}

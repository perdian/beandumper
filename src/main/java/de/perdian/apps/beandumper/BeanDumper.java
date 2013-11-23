package de.perdian.apps.beandumper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dumps a textual representation of an object graph.
 *
 * @author Christian Robert
 */

public class BeanDumper {

    private static final Logger log = LoggerFactory.getLogger(BeanDumper.class);

    private Appendable myTarget = null;
    private BeanDumperFormatHandler myFormatHandler = null;

    public BeanDumper() {
        this(new StringBuilder(), null);
    }

    public BeanDumper(BeanDumperFormatHandler formatHandler) {
        this(new StringBuilder(), formatHandler);
    }

    public BeanDumper(Appendable target) {
        this(target, null);
    }

    public BeanDumper(Appendable target, BeanDumperFormatHandler formatHandler) {
        this.setTarget(target);
        this.setFormatHandler(formatHandler);
    }

    public Appendable dump(Object bean) throws IOException {
        this.dump(bean, new BeanDumperContext(this.getFormatHandler()));
        return this.getTarget();
    }

    public Appendable dump(Object bean, String prefix) throws IOException {
        this.dump(bean, new BeanDumperContext(this.getFormatHandler()).pushPrefix(prefix));
        return this.getTarget();
    }

    private void dump(Object bean, BeanDumperContext context) throws IOException {
        if(this.getFormatHandler() != null) {
            this.getFormatHandler().appendDocumentPrefix(this.getTarget());
        }
        this.dumpObject(bean, context);
        if(this.getFormatHandler() != null) {
            this.getFormatHandler().appendDocumentPostfix(this.getTarget());
        }
    }

    private void dumpObject(Object object, BeanDumperContext context) throws IOException {
        if(this.checkIgnoreObject(object, context)) {
            return; // Ignore
        } else if(object == null) {
            this.getTarget().append(context.formatPrefix());
            this.getTarget().append(context.formatValue("<null>"));
            this.getTarget().append("\n");
        } else if(object instanceof Collection) {
            this.dumpCollection((Collection<?>)object, context);
        } else if(object instanceof Object[]) {
            this.dumpList(Arrays.asList((Object[])object), true, context);
        } else if(object instanceof Map) {
            this.dumpMap((Map<?, ?>)object, context);
        } else if(this.checkSimpleBean(object)) {
            this.dumpBeanValue(object, context);
        } else {
            this.dumpBeanGraph(object, context);
        }
    }

    private void dumpBeanValue(Object bean, BeanDumperContext context) throws IOException {
        this.getTarget().append(context.formatPrefix());
        if(!this.checkDirectValue(bean)) {
            this.getTarget().append(context.formatValue("[" + bean.getClass().getName() + "] ", BeanDumperFormat.INFO));
        }
        try {
            this.getTarget().append(context.formatValue(this.formatBeanAsString(bean)));
        } catch(Exception e) {
            this.getTarget().append(context.formatValue("[Cannot invoke toString] " + e, BeanDumperFormat.ERROR));
        }
        this.getTarget().append("\n");
    }

    private String formatBeanAsString(Object bean) {
        if(bean instanceof File) {
            return ((File)bean).getAbsolutePath();
        } else {
            return bean.toString();
        }
    }

    private void dumpBeanGraph(Object bean, BeanDumperContext context) throws IOException {
        BeanDumperContextStack.Entry stackEntry = context.lookupObjectEntry(bean);
        if(stackEntry != null) {

            StringBuilder valueBuilder  = new StringBuilder();
            valueBuilder.append("[Recursion of ");
            valueBuilder.append(bean.getClass().getName());
            valueBuilder.append(" starting at '").append(stackEntry.getPrefix());
            valueBuilder.append("']");

            this.getTarget().append(context.formatPrefix());
            this.getTarget().append(context.formatValue(valueBuilder, BeanDumperFormat.VIRTUAL));
            this.getTarget().append("\n");

        } else {
            List<PropertyDescriptor> propertyDescriptors = this.checkSimpleBean(bean) ? null : this.createPropertyDescriptors(bean);
            if(propertyDescriptors == null || propertyDescriptors.isEmpty()) {
                this.dumpBeanValue(bean, context);
            } else {
                context.pushObject(bean);
                try {
                    if(context.hasPrefix()) {
                      this.dumpBeanValue(bean, context);
                    }
                    for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        context.pushPrefix((context.hasPrefix() ? "." : "") + propertyDescriptor.getName());
                        try {
                            propertyDescriptor.getReadMethod().setAccessible(true);
                            Object propertyValue = propertyDescriptor.getReadMethod().invoke(bean);
                            this.dumpObject(propertyValue, context);
                        } catch(Throwable e) {
                            Throwable displayException = e;
                            if(e instanceof InvocationTargetException) {
                                displayException = e.getCause();
                            }
                            this.getTarget().append(context.formatPrefix(BeanDumperFormat.ERROR));
                            this.getTarget().append(context.formatValue("[Cannot retrieve value] " + displayException, BeanDumperFormat.ERROR));
                            this.getTarget().append("\n");
                        } finally {
                            context.popPrefix();
                        }
                    }
                } finally {
                    context.popObject();
                }
            }
        }
    }

    private void dumpCollection(Collection<?> collection, BeanDumperContext context) throws IOException {
        if(collection instanceof List) {
            this.dumpList((List<?>)collection, false, context);
        } else {
            this.dumpCollectionContent(collection, context);
        }
    }

    private void dumpCollectionContent(Collection<?> collection, BeanDumperContext context) throws IOException {

        StringBuilder infoContent = new StringBuilder();
        infoContent.append("[Collection, size=").append(collection.size());
        infoContent.append(", class=").append(collection.getClass().getName());
        infoContent.append("]");

        this.getTarget().append(context.formatPrefix(BeanDumperFormat.VIRTUAL));
        this.getTarget().append(context.formatValue(infoContent, BeanDumperFormat.VIRTUAL));
        this.getTarget().append("\n");

        context.pushObject(collection);
        try {
            context.pushFormats(BeanDumperFormat.VIRTUAL);
            try {
                Iterator<?> collectionIterator = collection.iterator();
                for(int i=0; collectionIterator.hasNext(); i++) {
                    context.pushPrefix("[" + i + "]");
                    try {
                        this.dumpObject(collectionIterator.next(), context);
                    } finally {
                        context.popPrefix();
                    }
                }
            } finally {
                context.popFormats();
            }
        } finally {
            context.popObject();
        }

    }

    private void dumpMap(Map<?, ?> map, BeanDumperContext context) throws IOException {

        if(context.hasPrefix()) {
            StringBuilder infoContent = new StringBuilder();
            infoContent.append("[Map, size=").append(map.size());
            infoContent.append(", class=").append(map.getClass().getName());
            infoContent.append("]");
            this.getTarget().append(context.formatPrefix(BeanDumperFormat.VIRTUAL));
            this.getTarget().append(context.formatValue(infoContent, BeanDumperFormat.VIRTUAL));
            this.getTarget().append("\n");
        }

        context.pushObject(map);
        try {
            List<Object> keyList = new ArrayList<Object>(map.keySet());
            Collections.sort(keyList, new Comparator<Object>() {
                @Override public int compare(Object o1, Object o2) {
                    String s1 = String.valueOf(o1);
                    String s2 = String.valueOf(o2);
                    boolean valid1 = BeanDumper.this.checkValidKeyString(s1);
                    boolean valid2 = BeanDumper.this.checkValidKeyString(s2);
                    if(valid1 && !valid2) {
                        return -1;
                    } else if(!valid1 && valid2) {
                        return 1;
                    } else {
                        return s1.compareTo(s2);
                    }
                }
            });
            for(Object key : keyList) {
                this.dumpMapEntry(key, map.get(key), context);
            }
        } finally {
            context.popObject();
        }

    }

    private void dumpMapEntry(Object mapEntryKey, Object mapEntryValue, BeanDumperContext context) throws IOException {

        String mapEntryKeyString = String.valueOf(mapEntryKey);
        StringBuilder prefixBuilder = new StringBuilder();
        boolean validKeyString = this.checkValidKeyString(mapEntryKeyString);
        if(context.hasPrefix() || !validKeyString) {
            prefixBuilder.append("[");
            if(mapEntryKey == null) {
                prefixBuilder.append("null");
            } else if(mapEntryKey instanceof String) {
                prefixBuilder.append("'").append(mapEntryKeyString).append("'");
            } else {
                prefixBuilder.append(mapEntryKeyString);
            }
            prefixBuilder.append("]");
        } else {
            prefixBuilder.append(mapEntryKeyString);
        }

        BeanDumperFormat[] formats = mapEntryKey instanceof String && validKeyString ? null : new BeanDumperFormat[] { BeanDumperFormat.VIRTUAL };
        context.pushFormats(formats);
        try {
            context.pushPrefix(prefixBuilder.toString());
            try {
                this.dumpObject(mapEntryValue, context);
            } finally {
                context.popPrefix();
            }
        } finally {
            context.popFormats();
        }
    }

    boolean checkValidKeyString(String value) {
        if(value == null) {
            return true;
        } else {
            return value.indexOf(".") < 0;
        }
    }

    private void dumpList(List<?> collection, boolean arrayWrapper, BeanDumperContext context) throws IOException {

        StringBuilder infoContent = new StringBuilder();
        if(arrayWrapper) {
            infoContent.append("[Array, length=").append(collection.size()).append("]");
        } else {
            infoContent.append("[List, size=").append(collection.size());
            infoContent.append(", class=").append(collection.getClass().getName());
            infoContent.append("]");
        }

        this.getTarget().append(context.formatPrefix(BeanDumperFormat.VIRTUAL));
        this.getTarget().append(context.formatValue(infoContent, BeanDumperFormat.VIRTUAL));
        this.getTarget().append("\n");
        context.pushObject(collection);
        try {
            for(int i=0; i < collection.size(); i++) {
              context.pushPrefix("[" + i + "]");
              try {
                  this.dumpObject(collection.get(i), context);
              } finally {
                  context.popPrefix();
              }
            }
        } finally {
            context.popObject();
        }

    }

    // -------------------------------------------------------------------------
    // ---  Helpers  -----------------------------------------------------------
    // -------------------------------------------------------------------------

    private List<PropertyDescriptor> createPropertyDescriptors(Object bean) {
        List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptorsFromBean = beanInfo == null ? null : beanInfo.getPropertyDescriptors();
            if(propertyDescriptorsFromBean != null) {
                for(PropertyDescriptor propertyDescriptor : propertyDescriptorsFromBean) {
                    if(this.checkPropertyDescriptor(propertyDescriptor)) {
                        propertyDescriptors.add(propertyDescriptor);
                    }
                }
            }
        } catch(IntrospectionException e) {
            log.debug("Cannot introspect class: " + bean.getClass(), e);
        }
        Collections.sort(propertyDescriptors, new Comparator<PropertyDescriptor>() {
            @Override public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return propertyDescriptors;
    }

    // -------------------------------------------------------------------------
    // ---  Checks  ------------------------------------------------------------
    // -------------------------------------------------------------------------

    private boolean checkPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        Method readMethod = propertyDescriptor.getReadMethod();
        if(readMethod == null) {
            return false;
        } else if(readMethod.getParameterTypes().length > 0) {
            return false;
        } else if(Object.class.equals(readMethod.getDeclaringClass())) {
            return false;
        } else if(Enum.class.equals(readMethod.getDeclaringClass())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSimpleBean(Object bean) {
        return (bean instanceof java.lang.String)
            || (bean instanceof java.lang.Class<?>)
            || (bean instanceof java.lang.ClassLoader)
            || (bean instanceof java.lang.Number)
            || (bean instanceof java.lang.Thread)
            || (bean instanceof java.io.File)
            || (bean instanceof java.util.Date)
            || (bean instanceof java.util.Locale)
            || this.checkSimpleBeanClassName(bean.getClass().getName())
        ;
    }

    private boolean checkSimpleBeanClassName(String className) {
        return className.startsWith("org.springframework.beans")
            || className.startsWith("org.springframework.web.context")
            || className.startsWith("org.apache.tiles")
        ;
    }

    private boolean checkDirectValue(Object bean) {
        return (bean instanceof String)
            || (bean instanceof Number)
        ;
    }

    private boolean checkIgnoreObject(Object bean, BeanDumperContext context) {
        return false;
    }


    // -------------------------------------------------------------------------
    // ---  Property access methods  -------------------------------------------
    // -------------------------------------------------------------------------

    private Appendable getTarget() {
        return this.myTarget;
    }
    private void setTarget(Appendable target) {
        this.myTarget = target;
    }

    public BeanDumperFormatHandler getFormatHandler() {
        return this.myFormatHandler;
    }
    public void setFormatHandler(BeanDumperFormatHandler formatHandler) {
        this.myFormatHandler = formatHandler;
    }

}
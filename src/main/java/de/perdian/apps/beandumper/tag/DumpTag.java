/*
 * BeanDumper
 * Copyright 2013 Christian Robert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.beandumper.tag;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import de.perdian.apps.beandumper.BeanDumper;
import de.perdian.apps.beandumper.impl.HtmlFormatHandler;

public class DumpTag extends TagSupport {

    static final long serialVersionUID = 1L;

    private Object myRoot = null;
    private String myPrefix = null;

    @Override
    public int doEndTag() throws JspException {
        try {

            Object rootObject = this.createRootObject();

            JspWriter jspWriter = this.pageContext.getOut();
            BeanDumper beanDumper = new BeanDumper(jspWriter);
            beanDumper.setFormatHandler(new HtmlFormatHandler());
            beanDumper.dump(rootObject, this.getPrefix());
            return super.doEndTag();

        } catch(IOException e) {
            throw new JspException("Cannot dump content", e);
        }
    }

    private Object createRootObject() {
        if(this.getRoot() != null) {
            return this.getRoot();
        } else {
            return this.createDefaultRootObject();
        }
    }

    private Map<String, Object> createDefaultRootObject() {
        Map<String, Object> objectMap = new TreeMap<String, Object>();
        Enumeration<?> attributeNames = this.pageContext.getRequest().getAttributeNames();
        while(attributeNames.hasMoreElements()) {
            String attributeName = (String)attributeNames.nextElement();
            objectMap.put(attributeName, this.pageContext.getRequest().getAttribute(attributeName));
        }
        return objectMap;
    }

    // -------------------------------------------------------------------------
    // ---  Property access methods  -------------------------------------------
    // -------------------------------------------------------------------------

    public Object getRoot() {
        return this.myRoot;
    }
    public void setRoot(Object root) {
        this.myRoot = root;
    }

    public String getPrefix() {
        return this.myPrefix;
    }
    public void setPrefix(String prefix) {
        this.myPrefix = prefix;
    }

}
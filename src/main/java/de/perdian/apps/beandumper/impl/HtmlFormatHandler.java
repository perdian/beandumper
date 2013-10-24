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
package de.perdian.apps.beandumper.impl;

import java.io.IOException;

import de.perdian.apps.beandumper.BeanDumperFormat;
import de.perdian.apps.beandumper.BeanDumperFormatHandler;

public class HtmlFormatHandler implements BeanDumperFormatHandler {

    private String myPreElementClass = null;
    private String myPreElementStyle = null;
    private String myLineElementName = "span";
    private String myLineElementClass = null;
    private String myLineElementStyle = null;

    @Override
    public Appendable appendValuePrefix(Appendable target, BeanDumperFormat format) throws IOException {
        target.append("<").append(this.getLineElementName());
        if(this.getLineElementClass() != null) {
            target.append(" class=\"").append(this.getLineElementClass()).append("\"");
        }
        target.append(" style=\"");
        if(this.getLineElementStyle() != null) {
            target.append(this.getLineElementStyle()).append(" ");
        }
        target.append(this.resolveCssStyle(format)).append("\"");
        return target.append(">");
    }

    @Override
    public Appendable appendValuePostfix(Appendable target, BeanDumperFormat format) throws IOException {
        return target.append("</").append(this.getLineElementName()).append(">");
    }

    @Override
    public Appendable appendDocumentPrefix(Appendable target) throws IOException {
        target.append("<pre");
        if(this.getPreElementClass() != null) {
            target.append(" class=\"").append(this.getPreElementClass()).append("\"");
        }
        target.append(" style=\"border: 1px solid black; background-color: white; padding: 10px; font-family: Consolas, Courier New, Monospace; font-size: 12px; line-height: 1;");
        if(this.getPreElementStyle() != null) {
            target.append(" ").append(this.getPreElementStyle());
        }
        return target.append("\">\n");
    }

    @Override
    public Appendable appendDocumentPostfix(Appendable target) throws IOException {
        return target.append("</pre>\n");
    }

    @Override
    public Appendable appendValue(Appendable target, CharSequence value) throws IOException {
        for(int i=0; i < value.length(); i++) {
            switch(value.charAt(i)) {
                case '<': target.append("&lt;"); break;
                case '>': target.append("&gt;"); break;
                case '&': target.append("&amp;"); break;
                default: target.append(value.charAt(i)); break;
            }
        }
        return target;
    }

    private CharSequence resolveCssStyle(BeanDumperFormat format) {
        switch(format) {
          case ERROR:   return "color: #aa0000;";
          case INFO:    return "color: #aaaaaa";
          case VIRTUAL: return "color: #444444; font-style: italic;";
          default:      return "";
        }
    }

    // -------------------------------------------------------------------------
    // ---  Property access methods  -------------------------------------------
    // -------------------------------------------------------------------------

    public String getPreElementClass() {
        return this.myPreElementClass;
    }
    public void setPreElementClass(String preElementClass) {
        this.myPreElementClass = preElementClass;
    }

    public String getPreElementStyle() {
        return this.myPreElementStyle;
    }
    public void setPreElementStyle(String preElementStyle) {
        this.myPreElementStyle = preElementStyle;
    }

    public String getLineElementName() {
        return this.myLineElementName;
    }
    public void setLineElementName(String lineElementName) {
        this.myLineElementName = lineElementName;
    }

    public String getLineElementClass() {
        return this.myLineElementClass;
    }
    public void setLineElementClass(String lineElementClass) {
        this.myLineElementClass = lineElementClass;
    }

    public String getLineElementStyle() {
        return this.myLineElementStyle;
    }
    public void setLineElementStyle(String lineElementStyle) {
        this.myLineElementStyle = lineElementStyle;
    }

}
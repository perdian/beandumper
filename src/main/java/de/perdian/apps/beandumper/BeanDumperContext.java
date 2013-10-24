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
package de.perdian.apps.beandumper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class BeanDumperContext {

    private String myCurrentPrefix = "";
    private BeanDumperFormatHandler myFormatHandler = null;
    private List<String> myPrefixStack = null;
    private BeanDumperContextStack myObjectStack = null;
    private List<BeanDumperFormat[]> myFormatStack = null;

    BeanDumperContext(BeanDumperFormatHandler formatHandler) {
        this.setPrefixStack(new LinkedList<String>());
        this.setObjectStack(new BeanDumperContextStack());
        this.setFormatStack(new LinkedList<BeanDumperFormat[]>());
        this.setFormatHandler(formatHandler);
    }

    BeanDumperContext pushPrefix(String text) {
        String oldPrefix = this.getPrefixStack().isEmpty() ? "" : this.getPrefixStack().get(0);
        String newPrefix = oldPrefix + text;
        this.getPrefixStack().add(0, newPrefix);
        this.setCurrentPrefix(newPrefix);
        return this;
    }

    void popPrefix() {
        this.getPrefixStack().remove(0);
        this.setCurrentPrefix(this.getPrefixStack().isEmpty() ? "" : this.getPrefixStack().get(0));
    }

    boolean hasPrefix() {
        return this.getCurrentPrefix() != null && this.getCurrentPrefix().length() > 0;
    }

    BeanDumperContext pushObject(Object object) {
        this.getObjectStack().push(this.getCurrentPrefix(), object);
        return this;
    }

    void popObject() {
        this.getObjectStack().pop();
    }

    BeanDumperContextStack.Entry lookupObjectEntry(Object object) {
        return this.getObjectStack().lookupEntry(object);
    }

    BeanDumperContext pushFormats(BeanDumperFormat... formats) {
        this.getFormatStack().add(0, formats);
        return this;
    }

    void popFormats() {
        this.getFormatStack().remove(0);
    }

    // -------------------------------------------------------------------------
    // ---  Formatter  ---------------------------------------------------------
    // -------------------------------------------------------------------------

    CharSequence formatPrefix(BeanDumperFormat... additionalFormats) throws IOException {
        StringBuilder prefixBuilder = new StringBuilder();
        if(this.getCurrentPrefix() != null && this.getCurrentPrefix().length() > 0) {
            prefixBuilder.append(this.getCurrentPrefix()).append(" = ");
        }
        return this.format(prefixBuilder, additionalFormats);
    }

    CharSequence formatValue(CharSequence value, BeanDumperFormat... additionalFormats) throws IOException {
        return this.format(value, additionalFormats);
    }

    private CharSequence format(CharSequence value, BeanDumperFormat... additionalFormats) throws IOException {
        BeanDumperFormatHandler formatHandler = this.getFormatHandler();
        List<BeanDumperFormat> formats = this.consolidateFormats(additionalFormats);
        if(formatHandler == null) {
            return value;
        } else {
            StringBuilder resultValue = new StringBuilder();
            for(int i=0; i < formats.size(); i++) {
                formatHandler.appendValuePrefix(resultValue, formats.get(i));
            }
            formatHandler.appendValue(resultValue, value);
            for(int i=formats.size() - 1; i >= 0; i--) {
                formatHandler.appendValuePostfix(resultValue, formats.get(i));
            }
            return resultValue;
        }
    }

    private List<BeanDumperFormat> consolidateFormats(BeanDumperFormat... additionalFormats) {
        Set<BeanDumperFormat> formatList = new LinkedHashSet<BeanDumperFormat>();
        if(this.getFormatStack() != null) {
            for(BeanDumperFormat[] formats : this.getFormatStack()) {
                if(formats != null) {
                    formatList.addAll(Arrays.asList(formats));
                }
            }
        }
        if(additionalFormats != null) {
            formatList.addAll(Arrays.asList(additionalFormats));
        }
        return new ArrayList<BeanDumperFormat>(formatList);
    }

    // -------------------------------------------------------------------------
    // ---  Property access methods  -------------------------------------------
    // -------------------------------------------------------------------------

    private BeanDumperFormatHandler getFormatHandler() {
        return this.myFormatHandler;
    }
    private void setFormatHandler(BeanDumperFormatHandler formatHandler) {
        this.myFormatHandler = formatHandler;
    }

    private BeanDumperContextStack getObjectStack() {
        return this.myObjectStack;
    }
    private void setObjectStack(BeanDumperContextStack objectStack) {
        this.myObjectStack = objectStack;
    }

    private List<String> getPrefixStack() {
        return this.myPrefixStack;
    }
    private void setPrefixStack(List<String> prefixStack) {
        this.myPrefixStack = prefixStack;
    }

    private String getCurrentPrefix() {
        return this.myCurrentPrefix;
    }
    private void setCurrentPrefix(String currentPrefix) {
        this.myCurrentPrefix = currentPrefix;
    }

    private List<BeanDumperFormat[]> getFormatStack() {
        return this.myFormatStack;
    }
    private void setFormatStack(List<BeanDumperFormat[]> formatStack) {
        this.myFormatStack = formatStack;
    }

}
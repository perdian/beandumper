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

import java.util.LinkedList;
import java.util.List;

class BeanDumperContextStack {

    private List<BeanDumperContextStack.Entry> myEntries = null;

    BeanDumperContextStack() {
        this.setEntries(new LinkedList<BeanDumperContextStack.Entry>());
    }

    void push(String prefix, Object object) {
        this.getEntries().add(0, new Entry(prefix, object));
    }

    void pop() {
        this.getEntries().remove(0);
    }

    Entry lookupEntry(Object object) {
        if(object != null) {
            for(BeanDumperContextStack.Entry entry : this.getEntries()) {
                if(object == entry.getObject() || object.equals(entry.getObject())) {
                  return entry;
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // ---  Inner classes  -----------------------------------------------------
    // -------------------------------------------------------------------------

    static class Entry {

        private String myPrefix = null;
        private Object myObject = null;

        Entry(String prefix, Object object) {
            this.setPrefix(prefix);
            this.setObject(object);
        }

        // ---------------------------------------------------------------------
        // ---  Property access methods  ---------------------------------------
        // ---------------------------------------------------------------------

        String getPrefix() {
            return this.myPrefix;
        }
        private void setPrefix(String prefix) {
            this.myPrefix = prefix;
        }

        Object getObject() {
            return this.myObject;
        }
        private void setObject(Object object) {
            this.myObject = object;
        }

    }

    // -------------------------------------------------------------------------
    // ---  Property access methods  -------------------------------------------
    // -------------------------------------------------------------------------

    private List<BeanDumperContextStack.Entry> getEntries() {
        return this.myEntries;
    }
    private void setEntries(List<BeanDumperContextStack.Entry> entries) {
        this.myEntries = entries;
    }

}
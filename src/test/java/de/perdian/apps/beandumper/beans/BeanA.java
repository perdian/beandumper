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
package de.perdian.apps.beandumper.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanA {

    private String myString = "foo";
    private String myNullString = null;
    private int myIntPrimitive = 42;
    private Integer myInteger = Integer.valueOf(42);
    private Integer myIntegerNull = null;
    private BeanB myBeanB = new BeanB();
    private List<BeanB> myListOfB = null;
    private Set<BeanC> mySetOfC = null;
    private Map<Object, Object> myMap = null;

    public BeanA() {

//        List<BeanB> listOfB = new ArrayList<BeanB>();
//        listOfB.add(new BeanB());
//        listOfB.add(new BeanB());
//        this.setListOfB(listOfB);
//
//        Set<BeanC> setOfC = new HashSet<BeanC>();
//        setOfC.add(new BeanC());
//        setOfC.add(null);
//        setOfC.add(new BeanC());
//        this.setSetOfC(setOfC);

        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("object1c", new BeanC());
        map.put("object2b", new BeanB());
        map.put("string1", "value1");
        map.put("recursion", this);
        map.put("stringNull", null);
        map.put(Thread.currentThread(), "ThreadValue");
        this.setMap(map);

    }

    // -------------------------------------------------------------------------
    // ---  Property access methods  -------------------------------------------
    // -------------------------------------------------------------------------

    public String getString() {
        return this.myString;
    }
    public void setString(String string) {
        this.myString = string;
    }

    public String getNullString() {
        return this.myNullString;
    }
    public void setNullString(String nullString) {
        this.myNullString = nullString;
    }

    public int getIntPrimitive() {
        return this.myIntPrimitive;
    }
    public void setIntPrimitive(int intPrimitive) {
        this.myIntPrimitive = intPrimitive;
    }

    public Integer getInteger() {
        return this.myInteger;
    }
    public void setInteger(Integer integer) {
        this.myInteger = integer;
    }

    public Integer getIntegerNull() {
        return this.myIntegerNull;
    }
    public void setIntegerNull(Integer integerNull) {
        this.myIntegerNull = integerNull;
    }

    public List<BeanB> getListOfB() {
        return this.myListOfB;
    }
    public void setListOfB(List<BeanB> listOfB) {
        this.myListOfB = listOfB;
    }

    public BeanB getBeanB() {
        return this.myBeanB;
    }
    public void setBeanB(BeanB beanB) {
        this.myBeanB = beanB;
    }

    public Set<BeanC> getSetOfC() {
        return this.mySetOfC;
    }
    public void setSetOfC(Set<BeanC> setOfC) {
        this.mySetOfC = setOfC;
    }

    public Map<Object, Object> getMap() {
        return this.myMap;
    }
    public void setMap(Map<Object, Object> map) {
        this.myMap = map;
    }

}
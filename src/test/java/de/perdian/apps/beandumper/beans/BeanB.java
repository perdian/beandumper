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

import java.util.Arrays;

public class BeanB {

    private Thread myThread = Thread.currentThread();
    private BeanD myBeanD1 = new BeanD(this);
    private BeanD myBeanD2 = new BeanD("foo");
    private BeanD myBeanD3 = new BeanD(Arrays.asList("s1", "s2", "s3"));
    private BeanC myBeanC = new BeanC();

    // -------------------------------------------------------------------------
    // ---  Property access methods  -------------------------------------------
    // -------------------------------------------------------------------------

    public Thread getThread() {
        return this.myThread;
    }
    public void setThread(Thread thread) {
        this.myThread = thread;
    }

    public BeanD getBeanD1() {
        return this.myBeanD1;
    }
    public void setBeanD1(BeanD beanD1) {
        this.myBeanD1 = beanD1;
    }

    public BeanD getBeanD2() {
        return this.myBeanD2;
    }
    public void setBeanD2(BeanD beanD2) {
        this.myBeanD2 = beanD2;
    }

    public BeanD getBeanD3() {
        return this.myBeanD3;
    }
    public void setBeanD3(BeanD beanD3) {
        this.myBeanD3 = beanD3;
    }

    public BeanC getBeanC() {
        return this.myBeanC;
    }
    public void setBeanC(BeanC beanC) {
        this.myBeanC = beanC;
    }

}
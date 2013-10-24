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

import de.perdian.apps.beandumper.beans.BeanA;
import de.perdian.apps.beandumper.impl.HtmlFormatHandler;

public class BeanDumperSuite {

    public static void main(String[] args) throws Exception {

        System.err.println(new BeanDumper(new HtmlFormatHandler()).dump(new BeanA()));
//        System.err.println(new BeanDumper().dump(new BeanA()));

    }

}

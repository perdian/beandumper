package de.perdian.apps.beandumper;

import java.io.IOException;

/**
 * Dumps a textual representation of an object graph.
 *
 * @author Christian Robert
 */

public class BeanDumper {

    public void dump(Object bean, Appendable target) throws IOException {
        this.dump(bean, target, "");
    }

    public void dump(Object bean, Appendable target, String prefix) throws IOException {
    }

}
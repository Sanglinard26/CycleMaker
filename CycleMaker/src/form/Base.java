/*
 * Creation : 26 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Base extends Element {

    public Base(Dataset time, Dataset dataset, double duration, double amplitude) {

        this.duration = time.getDatas().get(time.getDatas().size() - 1);
        this.amplitude = Double.NEGATIVE_INFINITY;

        this.firstIndex = 0;
        this.lastIndex = time.getDatas().size() - 1;

        for (double value : dataset.getDatas()) {
            this.amplitude = Math.max(amplitude, value);
        }
    }

}

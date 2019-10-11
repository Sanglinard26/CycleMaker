/*
 * Creation : 26 août 2018
 */
package form;

import form.Cycle.Dataset;
import form.Cycle.Time;

public final class Base extends Element {

    private static final long serialVersionUID = 1L;

    public Base(Time time, Dataset dataset) {

        this.duration = time.get(time.size() - 1);
        this.amplitude = Float.NEGATIVE_INFINITY;

        this.firstIndex = 0;
        this.lastIndex = time.size() - 1;

        this.t1 = 0;
        this.t2 = time.get(lastIndex);

        for (float value : dataset.getDatas()) {
            this.amplitude = Math.max(amplitude, value);
        }
    }

    @Override
    public float diffEndFromBeginValue() {
        return 0;
    }

}

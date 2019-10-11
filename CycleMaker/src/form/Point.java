/*
 * Creation : 2 sept. 2018
 */
package form;

import form.Cycle.Dataset;

public final class Point extends Element {

    private static final long serialVersionUID = 1L;

    public Point(Dataset dataset, float value) {

        this.duration = 0;
        this.amplitude = 0;
        this.nbPoint = 1;

        dataset.addData(value);

        this.firstIndex = dataset.getDatas().size() - 1;
        this.lastIndex = this.firstIndex;
    }

    @Override
    public float diffEndFromBeginValue() {
        return 0;
    }

}

/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Rampe extends Element {

    private static final long serialVersionUID = 1L;

    public Rampe(Dataset dataset, double duration, double amplitude) {

        this.duration = duration;
        this.amplitude = amplitude;

        final int nPoint = (int) (this.duration / te);
        final double gradient = this.amplitude / nPoint;

        if (!dataset.getDatas().isEmpty()) {
            this.firstIndex = dataset.getDatas().size();

            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
            this.nbPoint++;

            for (int i = 1; i <= nPoint; i++) {
                dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + gradient);
                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;
        }

    }

    @Override
    public double diffEndFromBeginValue() {
        return this.amplitude;
    }

}

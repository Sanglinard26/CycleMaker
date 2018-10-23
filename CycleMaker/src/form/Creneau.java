/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Creneau extends Element {

    private static final long serialVersionUID = 1L;

    public Creneau(Dataset dataset, double duration, double amplitude) {

        this.duration = duration;
        this.amplitude = amplitude;

        final int nPoint = (int) (this.duration / te);

        if (!dataset.getDatas().isEmpty()) {

            this.firstIndex = dataset.getDatas().size();

            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
            this.nbPoint++;

            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + amplitude);
            this.nbPoint++;

            for (int i = 2; i <= nPoint; i++) {
                dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;
        }

    }

    @Override
    public double DiffEndFromBeginValue() {
        return this.amplitude;
    }

}

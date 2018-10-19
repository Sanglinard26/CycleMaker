/*
 * Creation : 26 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Stationnaire extends Element {

    private static final long serialVersionUID = 1L;

    public Stationnaire(Dataset dataset, double duration) {

        this.duration = duration - te;
        this.amplitude = 0;

        final int nPoint = (int) (this.duration / te);

        if (!dataset.getDatas().isEmpty()) {
            this.firstIndex = dataset.getDatas().size();

            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);
            this.nbPoint++;

            for (int i = 0; i <= nPoint; i++) {
                dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);
                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;
        }

    }

    @Override
    public double DiffEndFromBeginValue() {
        return 0;
    }

}

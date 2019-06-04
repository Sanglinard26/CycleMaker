/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Trapeze extends Element {

    private static final long serialVersionUID = 1L;

    public Trapeze(Dataset dataset, double duration, double tpsRampe, double amplitude) {

        this.duration = duration;
        this.amplitude = amplitude;

        final double tPlateau = this.duration - (tpsRampe * 2);
        final double gradient = this.amplitude / (tpsRampe / te);

        final int nPoint = (int) (this.duration / te);
        final int nPointPlateau = (int) (tPlateau / te);
        final int nPointRampe = (nPoint - nPointPlateau) / 2;

        if (!dataset.getDatas().isEmpty()) {

            this.firstIndex = dataset.getDatas().size();

            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
            this.nbPoint++;

            for (int i = 1; i <= nPoint; i++) {
                if (i <= nPointRampe) {
                    dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + gradient);
                } else if (i > nPointRampe && i <= nPoint - nPointRampe) {
                    dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
                } else {
                    dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) - gradient);
                }

                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;
        }

    }

    @Override
    public double diffEndFromBeginValue() {
        return 0;
    }

}

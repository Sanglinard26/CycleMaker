/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Sinus extends Element {

    private static final long serialVersionUID = 1L;

    public Sinus(Dataset dataset, double amplitude, double frequence) {

        this.amplitude = amplitude;
        this.duration = (1 / frequence) + te;

        final int nPoint = (int) ((1 / frequence) / te);

        if (!dataset.getDatas().isEmpty()) {
            final double valDepart = dataset.getDatas().get(dataset.getDatas().size() - 1);

            this.firstIndex = dataset.getDatas().size();

            double valSin = 0;

            for (int i = 0; i <= nPoint; i++) {
                valSin = valDepart + Math.sin(2 * Math.PI * frequence * (i * te)) * (this.amplitude / 2);
                dataset.addData(valSin);
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

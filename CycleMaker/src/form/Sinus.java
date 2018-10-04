/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Sinus extends Element {

    private double frequence;

    public Sinus(Dataset time, Dataset dataset, double amplitude, double frequence) {

        this.amplitude = amplitude;
        this.frequence = frequence;
        this.duration = (1 / frequence) + te;

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {
            final double valDepart = dataset.getDatas().get(dataset.getDatas().size() - 1);

            this.firstIndex = dataset.getDatas().size();

            for (double t = 0; t <= this.duration; t = t + te) {
                time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
                dataset.addData(valDepart + Math.sin(2 * Math.PI * this.frequence * t) * (this.amplitude / 2));
                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;

            this.t1 = time.getDatas().get(firstIndex);
            this.t2 = time.getDatas().get(lastIndex);

        }

    }

}

/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Rampe extends Element {

    public Rampe(Dataset time, Dataset dataset, double duration, double amplitude) {

        this.duration = duration - te;
        this.amplitude = amplitude;

        final double gradient = this.amplitude / ((this.duration + te) / te);

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {
            this.firstIndex = dataset.getDatas().size();

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
            this.nbPoint++;

            for (double t = 0; t <= this.duration; t = t + te) {
                time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
                dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + gradient);
                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;

            this.t1 = time.getDatas().get(firstIndex);
            this.t2 = time.getDatas().get(lastIndex);
        }

    }

}

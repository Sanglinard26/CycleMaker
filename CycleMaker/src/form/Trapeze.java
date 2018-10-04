/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Trapeze extends Element {

    public Trapeze(Dataset time, Dataset dataset, double duration, double tpsRampe, double amplitude) {

        this.duration = duration - te;
        this.amplitude = amplitude;

        final double tPlateau = this.duration - (tpsRampe * 2);
        final double gradient = this.amplitude / (tpsRampe / te);

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {

            this.firstIndex = dataset.getDatas().size();

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
            this.nbPoint++;

            for (double t = 0; t <= this.duration; t = t + te) {
                time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
                if (t <= tpsRampe) {
                    dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + gradient);
                } else if (t > tpsRampe && t <= tpsRampe + tPlateau) {
                    dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
                } else {
                    dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) - gradient);
                }

                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;

            this.t1 = time.getDatas().get(firstIndex);
            this.t2 = time.getDatas().get(lastIndex);
        }

    }

}

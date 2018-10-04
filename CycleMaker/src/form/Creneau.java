/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Creneau extends Element {

    public Creneau(Dataset time, Dataset dataset, double duration, double amplitude) {

        this.duration = duration - te;
        this.amplitude = amplitude;

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {

            this.firstIndex = dataset.getDatas().size();

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
            this.nbPoint++;

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + amplitude);
            this.nbPoint++;

            for (double t = 0; t <= this.duration; t = t + te) {
                time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
                dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);
                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;

            this.t1 = time.getDatas().get(firstIndex);
            this.t2 = time.getDatas().get(lastIndex);
        }

    }

}

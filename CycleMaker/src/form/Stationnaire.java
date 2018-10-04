/*
 * Creation : 26 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Stationnaire extends Element {

    public Stationnaire(Dataset time, Dataset dataset, double duration) {

        this.duration = duration - te;
        this.amplitude = 0;

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {
            this.firstIndex = dataset.getDatas().size();

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);
            this.nbPoint++;

            for (double t = 0; t <= this.duration; t = t + te) {
                time.addData(time.getDatas().get(time.getDatas().size() - 1) + te);
                dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);
                this.nbPoint++;
            }

            this.lastIndex = dataset.getDatas().size() - 1;

            this.t1 = time.getDatas().get(firstIndex);
            this.t2 = time.getDatas().get(lastIndex);
        }

    }

}

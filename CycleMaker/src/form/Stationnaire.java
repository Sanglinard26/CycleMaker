/*
 * Creation : 26 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Stationnaire extends Element {

    public Stationnaire(Dataset time, Dataset dataset, double duration) {

        this.duration = duration;
        this.amplitude = 0;
        this.nbPoint = 2;

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {
            this.firstIndex = dataset.getDatas().size();

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + moveTime);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + this.duration - moveTime);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);

            this.lastIndex = dataset.getDatas().size() - 1;
        }

    }

}

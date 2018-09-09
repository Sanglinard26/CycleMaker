/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Rampe extends Element {

    public Rampe(Dataset time, Dataset dataset, double duration, double amplitude) {

        this.duration = duration;
        this.amplitude = amplitude;
        this.nbPoint = 2;

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {
            this.firstIndex = dataset.getDatas().size();

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + moveTime);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + this.duration - moveTime);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);

            this.lastIndex = dataset.getDatas().size() - 1;
            
            this.t1 = time.getDatas().get(firstIndex);
            this.t2 = time.getDatas().get(lastIndex);
        }

    }

}

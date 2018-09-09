/*
 * Creation : 17 août 2018
 */
package form;

import form.Cycle.Dataset;

public final class Trapeze extends Element {

    public Trapeze(Dataset time, Dataset dataset, double duration, double tpsRampe, double amplitude) {

        this.duration = duration;
        this.amplitude = amplitude;
        this.nbPoint = 4;

        if (!time.getDatas().isEmpty() && !dataset.getDatas().isEmpty()) {
            double tPlateau = this.duration - (tpsRampe * 2);

            this.firstIndex = dataset.getDatas().size();

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + moveTime);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + tpsRampe);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + this.amplitude);

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + tPlateau - moveTime);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) + 0);

            time.addData(time.getDatas().get(time.getDatas().size() - 1) + tpsRampe);
            dataset.addData(dataset.getDatas().get(dataset.getDatas().size() - 1) - this.amplitude);

            this.lastIndex = dataset.getDatas().size() - 1;
            
            this.t1 = time.getDatas().get(firstIndex);
            this.t2 = time.getDatas().get(lastIndex);
        }

    }

}

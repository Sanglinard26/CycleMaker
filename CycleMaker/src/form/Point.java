/*
 * Creation : 2 sept. 2018
 */
package form;

import form.Cycle.Dataset;

public final class Point extends Element {

    public Point(Dataset time, Dataset dataset, double offsetTime, double value) {

        this.duration = 0;
        this.amplitude = 0;
        this.nbPoint = 1;

        if (time.getDatas().isEmpty()) {
            time.addData(0d); // C'est le point de depart du cycle
        } else {
            time.addData(time.getDatas().get(time.getDatas().size() - 1) + offsetTime);
        }

        dataset.addData(value);

        this.firstIndex = dataset.getDatas().size() - 1;
        this.lastIndex = this.firstIndex;
        
        this.t1 = time.getDatas().get(firstIndex);
        this.t2 = time.getDatas().get(lastIndex);

    }

}

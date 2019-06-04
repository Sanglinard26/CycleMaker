/*
 * Creation : 17 août 2018
 */
package form;

import java.io.Serializable;

public abstract class Element implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String BASE = "Base";
    public static final String POINT = "Point";
    public static final String CRENEAU = "Jump";
    public static final String STATIONNAIRE = "Steady";
    public static final String RAMPE = "Ramp";
    public static final String SINUS = "Sinus";
    public static final String TRAPEZE = "Trapeze";

    public static final String ICON_POINT = "/icon_point_200.png";
    public static final String ICON_CRENEAU = "/icon_creneau_200.png";
    public static final String ICON_STATIONNAIRE = "/icon_stationnaire_200.png";
    public static final String ICON_RAMPE = "/icon_rampe_200.png";
    public static final String ICON_SINUS = "/icon_sinus_200.png";
    public static final String ICON_TRAPEZE = "/icon_trapeze_200.png";

    public static final double te = 0.1;

    protected int firstIndex, lastIndex;
    protected double t1, t2;
    protected int nbPoint;
    protected int position;
    protected double duration;
    protected double amplitude;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getDuration() {
        return duration;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public abstract double DiffEndFromBeginValue();

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public int getNbPoint() {
        return nbPoint;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public double getT1() {
        return t1;
    }

    public double getT2() {
        return t2;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public void setT1(double t1) {
        this.t1 = t1;
    }

    public void setT2(double t2) {
        this.t2 = t2;
    }

    public final String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object element) {

        double thisElement = this.toString().hashCode() + this.t1 + this.t2;
        double otherElement = element.toString().hashCode() + ((Element) element).getT1() + ((Element) element).getT2();

        return Double.compare(thisElement, otherElement) == 0;
    }

}

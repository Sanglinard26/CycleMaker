/*
 * Creation : 17 août 2018
 */
package form;

public abstract class Element {

    public static final String BASE = "Base";
    public static final String POINT = "Point";
    public static final String CRENEAU = "Creneau";
    public static final String STATIONNAIRE = "Stationnaire";
    public static final String DOUBLE_RAMPE = "Double Rampe";
    public static final String RAMPE = "Rampe";
    public static final String SINUS = "Sinus";
    public static final String TRAPEZE = "Trapeze";

    public static final double moveTime = 0.01;

    protected int firstIndex, lastIndex;
    protected int nbPoint;
    protected double duration;
    protected double amplitude;

    public double getDuration() {
        return duration;
    }

    public double getAmplitude() {
        return amplitude;
    }

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

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type : " + this.toString() + "\n");
        sb.append("Duration : " + this.duration + "\n");
        sb.append("Nb point : " + this.nbPoint + "\n");
        sb.append("First index : " + this.firstIndex + "\n");
        sb.append("Last index : " + this.lastIndex + "\n");
        return sb.toString();
    }

}

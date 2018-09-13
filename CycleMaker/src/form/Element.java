/*
 * Creation : 17 août 2018
 */
package form;

import java.text.NumberFormat;

public abstract class Element {

    public static final String BASE = "Base";
    public static final String POINT = "Point";
    public static final String CRENEAU = "Creneau";
    public static final String STATIONNAIRE = "Stationnaire";
    public static final String RAMPE = "Rampe";
    public static final String SINUS = "Sinus";
    public static final String TRAPEZE = "Trapeze";
    
    public static final String ICON_POINT =  "/icon_point_200.png";
    public static final String ICON_CRENEAU =  "/icon_creneau_200.png";
    public static final String ICON_STATIONNAIRE =  "/icon_stationnaire_200.png";
    public static final String ICON_RAMPE =  "/icon_rampe_200.png";
    public static final String ICON_SINUS =  "/icon_sinus_200.png";
    public static final String ICON_TRAPEZE =  "/icon_trapeze_200.png";

    public static final double moveTime = 0.01;

    protected int firstIndex, lastIndex;
    protected double t1, t2;
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
    
    public final String getName()
    {
    	return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
    	final NumberFormat nf = NumberFormat.getInstance();
    	
        return this.getClass().getSimpleName() + " (" + nf.format(t1) + "s" + "-" + nf.format(t2) + "s" + ")";
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

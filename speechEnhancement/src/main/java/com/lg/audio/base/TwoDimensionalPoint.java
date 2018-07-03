package com.lg.audio.base;

public class TwoDimensionalPoint {
    /**
     * @uml.property  name="x"
     */
    double x = 0;
    /**
     * @uml.property  name="y"
     */
    double y = 0;
    /**
     * @return
     * @uml.property  name="x"
     */
    public double getX() {
        return x;
    }
    /**
     * @return
     * @uml.property  name="y"
     */
    public double getY() {
        return y;
    }
    public TwoDimensionalPoint(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }
}

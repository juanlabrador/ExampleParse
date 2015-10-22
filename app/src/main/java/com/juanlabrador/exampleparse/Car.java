package com.juanlabrador.exampleparse;

/**
 * Created by juanlabrador on 19/10/15.
 */
public class Car {

    String model;
    boolean haveNitro;

    public Car(String model, boolean nitro) {
        this.model = model;
        this.haveNitro = nitro;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isHaveNitro() {
        return haveNitro;
    }

    public void setHaveNitro(boolean haveNitro) {
        this.haveNitro = haveNitro;
    }
}

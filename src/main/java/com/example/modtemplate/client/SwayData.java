package com.example.modtemplate.client;

/**
 * Mutable data container for block deformation.
 */
public class SwayData {
    public float nx;
    public float nz;
    public float intensity;

    public SwayData(float nx, float nz, float intensity) {
        this.nx = nx;
        this.nz = nz;
        this.intensity = intensity;
    }

    public void update(float nx, float nz, float intensity) {
        this.nx = nx;
        this.nz = nz;
        this.intensity = intensity;
    }
}

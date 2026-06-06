package com.github.razorplay01.sway.client;

public class SwayData {
	public float nx;
	public float nz;
	public float intensity;

	private float prevNx;
	private float prevNz;
	private float prevIntensity;
	private long lastUpdateTime;

	public SwayData(float nx, float nz, float intensity) {
		this.nx = nx;
		this.nz = nz;
		this.intensity = intensity;
		this.prevNx = nx;
		this.prevNz = nz;
		this.prevIntensity = intensity;
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public void update(float newNx, float newNz, float newIntensity) {
		this.prevNx = this.nx;
		this.prevNz = this.nz;
		this.prevIntensity = this.intensity;
		this.nx = newNx;
		this.nz = newNz;
		this.intensity = newIntensity;
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public SwayData getInterpolated(float smoothness) {
		long currentTime = System.currentTimeMillis();
		float timeDelta = Math.min((currentTime - lastUpdateTime) / 1000.0f, 0.1f);
		float alpha = Math.min(timeDelta * smoothness, 1.0f);

		float interpNx = lerp(prevNx, nx, alpha);
		float interpNz = lerp(prevNz, nz, alpha);
		float interpIntensity = lerp(prevIntensity, intensity, alpha);

		return new SwayData(interpNx, interpNz, interpIntensity);
	}

	private float lerp(float start, float end, float alpha) {
		return start + (end - start) * alpha;
	}
}

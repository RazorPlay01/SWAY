package com.github.razorplay01.sway.platform.forge.render;
//? forge {

/*import com.github.razorplay01.sway.client.render.VertexMutator;

public final class ForgePartVertexMutator implements VertexMutator {
	private final int[] vertexData;
	private final int stride;
	private boolean modified;

	public ForgePartVertexMutator(int[] vertexData, int stride) {
		this.vertexData = vertexData.clone();
		this.stride = stride;
		this.modified = false;
	}

	public int[] getTransformedVertexData() {
		return vertexData;
	}

	public boolean isModified() {
		return modified;
	}

	@Override
	public int vertexCount() {
		return 4;
	}

	@Override
	public float x(int idx) {
		return Float.intBitsToFloat(vertexData[idx * stride]);
	}

	@Override
	public float y(int idx) {
		return Float.intBitsToFloat(vertexData[idx * stride + 1]);
	}

	@Override
	public float z(int idx) {
		return Float.intBitsToFloat(vertexData[idx * stride + 2]);
	}

	private void markModified() { this.modified = true; }

	@Override
	public void setX(int idx, float value) {
		markModified();
		vertexData[idx * stride] = Float.floatToRawIntBits(value);
	}

	@Override
	public void setY(int idx, float value) {
		markModified();
		vertexData[idx * stride + 1] = Float.floatToRawIntBits(value);
	}

	@Override
	public void setZ(int idx, float value) {
		markModified();
		vertexData[idx * stride + 2] = Float.floatToRawIntBits(value);
	}
}
*///?}

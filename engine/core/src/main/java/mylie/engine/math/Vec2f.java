package mylie.engine.math;

public record Vec2f(float x, float y) {

	public static Vec2f of(float v, float v1) {
		return new Vec2f(v, v1);
	}
}

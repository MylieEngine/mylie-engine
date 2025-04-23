package mylie.engine.input.processors;

import mylie.engine.input.InputEvent;
import mylie.engine.input.devices.Gamepad;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AxisDeathZoneTest {
	AxisDeathZone axisDeathZone;
	Gamepad gamepad;
	Gamepad virtualGamepad;

	@BeforeEach
	void setup() {
		axisDeathZone = new AxisDeathZone(0.5f);
		gamepad = new Gamepad(null, true);
		virtualGamepad = new Gamepad(null, false);
	}

	@Test
	void testDeadZone() {
		InputEvent<Gamepad, Gamepad.Axis, Float> processedEvent = axisDeathZone
				.process(new InputEvent<>(gamepad, Gamepad.Axis.LEFT_X, 1f), null);
		Assertions.assertEquals(1f, processedEvent.value());
		processedEvent = axisDeathZone.process(new InputEvent<>(gamepad, Gamepad.Axis.LEFT_X, 0.4f), null);
		Assertions.assertEquals(0f, processedEvent.value());
	}
}

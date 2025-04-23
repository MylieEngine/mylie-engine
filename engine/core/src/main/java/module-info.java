module mylie.engine.core {
	requires static lombok;
	requires org.slf4j;
	exports mylie.engine.util;
	exports mylie.engine.core;
	exports mylie.engine.input;
	exports mylie.engine.input.devices;
	exports mylie.engine.event;
	exports mylie.engine.math;
	exports mylie.engine.core.async;
	exports mylie.engine.util.exceptions;
}

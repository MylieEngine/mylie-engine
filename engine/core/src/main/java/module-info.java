module mylie.engine.core {
	requires static lombok;
	requires org.slf4j;
	exports mylie.engine.util;
	exports mylie.engine.core;
	exports mylie.engine.core.async;
	exports mylie.engine.util.exceptions;
}

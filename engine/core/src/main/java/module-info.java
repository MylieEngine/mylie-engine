module mylie.engine.core {
	requires static lombok;
	requires org.slf4j;
	exports mylie.engine.core;
	exports mylie.engine.util;
	exports mylie.engine.util.exceptions;
}

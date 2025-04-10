package mylie.engine.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import mylie.engine.core.ComponentManager;
import mylie.engine.util.exceptions.ConstructorNotFoundException;
import mylie.engine.util.exceptions.IllegalInstantiationException;

public class ClassUtils {
	private ClassUtils() {
		throw new IllegalInstantiationException(ClassUtils.class);
	}

	@SuppressWarnings("rawtypes")
	public static <T> T newInstance(Class<T> clazz, Object... args) {
		Class[] array = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
		try {
			Constructor<T> constructor = clazz.getConstructor(array);
			return constructor.newInstance(args);
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException
				| IllegalAccessException e) {
			throw new ConstructorNotFoundException(e, clazz, ComponentManager.class);
		}
	}
}

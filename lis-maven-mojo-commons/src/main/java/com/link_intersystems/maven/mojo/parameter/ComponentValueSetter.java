package com.link_intersystems.maven.mojo.parameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.ParameterizedConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.ReflectionUtils;
import org.codehaus.plexus.util.StringUtils;

@SuppressWarnings("rawtypes")
public class ComponentValueSetter {
	private final Object object;

	private final String fieldName;

	private final ConverterLookup lookup;

	private Method setter;

	private Class setterParamType;

	private ConfigurationConverter setterTypeConverter;

	private Type[] setterTypeArguments;

	private Field field;

	private Class fieldType;

	private ConfigurationConverter fieldTypeConverter;

	private Type[] fieldTypeArguments;

	private final ConfigurationListener listener;

	public ComponentValueSetter(final String fieldName, final Object object, final ConverterLookup lookup)
			throws ComponentConfigurationException {
		this(fieldName, object, lookup, null);
	}

	public ComponentValueSetter(final String fieldName, final Object object, final ConverterLookup lookup,
			final ConfigurationListener listener) throws ComponentConfigurationException {
		this(fieldName, null, object, lookup, listener);
	}

	public ComponentValueSetter(final String fieldName, final Class implementation, final Object object,
			final ConverterLookup lookup, final ConfigurationListener listener) throws ComponentConfigurationException {
		this.fieldName = fieldName;
		this.object = object;
		this.lookup = lookup;
		this.listener = listener;

		if (object == null) {
			throw new ComponentConfigurationException("Component is null");
		}

		initSetter(implementation);

		initField(implementation);

		if (setter == null && field == null) {
			throw new ComponentConfigurationException("Cannot find setter, adder nor field in "
					+ object.getClass().getName() + " for '" + fieldName + "'");
		}

		if (setterTypeConverter == null && fieldTypeConverter == null) {
			throw new ComponentConfigurationException("Cannot find converter for " + setterParamType.getName()
					+ (fieldType != null && !fieldType.equals(setterParamType) ? " or " + fieldType.getName() : ""));
		}
	}

	@SuppressWarnings("unchecked")
	private void initSetter(final Class implementation) {
		setter = ReflectionUtils.getSetter(fieldName, object.getClass());

		if (setter == null) {
			setter = getAdder(fieldName, object.getClass());

			if (setter == null) {
				return;
			}
		}

		setterParamType = setter.getParameterTypes()[0];
		if (implementation != null && setterParamType.isAssignableFrom(implementation)) {
			setterParamType = implementation; // more specific, compatible type
		}

		try {
			setterTypeConverter = lookup.lookupConverterForType(setterParamType);

			if (setterTypeConverter instanceof ParameterizedConfigurationConverter) {
				setterTypeArguments = getTypeArguments(setter.getGenericParameterTypes()[0]);
			}
		} catch (final ComponentConfigurationException e) {
			// ignore, handle later
		}
	}

	private static Method getAdder(final String fieldName, final Class clazz) {
		final Method[] methods = clazz.getMethods();

		final String adderName = "add" + StringUtils.capitalizeFirstLetter(fieldName);

		for (final Method method : methods) {
			if (adderName.equals(method.getName()) && !Modifier.isStatic(method.getModifiers())
					&& method.getParameterTypes().length == 1) {
				return method;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void initField(final Class implementation) {
		field = ReflectionUtils.getFieldByNameIncludingSuperclasses(fieldName, object.getClass());

		if (field == null) {
			return;
		}

		fieldType = field.getType();
		if (implementation != null && fieldType.isAssignableFrom(implementation)) {
			fieldType = implementation; // more specific, compatible type
		}

		try {
			fieldTypeConverter = lookup.lookupConverterForType(fieldType);

			if (fieldTypeConverter instanceof ParameterizedConfigurationConverter) {
				fieldTypeArguments = getTypeArguments(field.getGenericType());
			}
		} catch (final ComponentConfigurationException e) {
			// ignore, handle later
		}
	}

	private Type[] getTypeArguments(final Type type) {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments();
		}
		return null;
	}

	private void setValueUsingField(final Object value) throws ComponentConfigurationException {
		try {
			final boolean wasAccessible = field.isAccessible();

			if (!wasAccessible) {
				field.setAccessible(true);
			}

			if (listener != null) {
				listener.notifyFieldChangeUsingReflection(fieldName, value, object);
			}

			field.set(object, value);

			if (!wasAccessible) {
				field.setAccessible(false);
			}
		} catch (final IllegalAccessException e) {
			throw new ComponentConfigurationException("Cannot access field: " + field, e);
		} catch (final IllegalArgumentException e) {
			throw new ComponentConfigurationException(
					"Cannot assign value '" + value + "' (type: " + value.getClass() + ") to " + field, e);
		}
	}

	private void setValueUsingSetter(final Object value) throws ComponentConfigurationException {
		if (setterParamType == null || setter == null) {
			throw new ComponentConfigurationException("No setter found");
		}

		final String exceptionInfo = object.getClass().getName() + "." + setter.getName() + "( "
				+ setterParamType.getName() + " )";

		if (listener != null) {
			listener.notifyFieldChangeUsingSetter(fieldName, value, object);
		}

		try {
			setter.invoke(object, new Object[] { value });
		} catch (final IllegalAccessException e) {
			throw new ComponentConfigurationException("Cannot access method: " + exceptionInfo, e);
		} catch (final IllegalArgumentException e) {
			throw new ComponentConfigurationException(
					"Invalid parameter supplied while setting '" + value + "' to " + exceptionInfo, e);
		} catch (final InvocationTargetException e) {
			throw new ComponentConfigurationException(
					"Setter " + exceptionInfo + " threw exception when called with parameter '" + value + "': "
							+ e.getTargetException().getMessage(),
					e);
		}
	}

	public void configure(final PlexusConfiguration config, final ClassLoader classLoader,
			final ExpressionEvaluator evaluator) throws ComponentConfigurationException {
		Object value = null;

		// try setter converter + method first

		if (setterTypeConverter != null) {
			try {
				if (setterTypeArguments != null) {
					final ParameterizedConfigurationConverter converter = (ParameterizedConfigurationConverter) setterTypeConverter;
					value = converter.fromConfiguration(lookup, config, setterParamType, setterTypeArguments,
							object.getClass(), classLoader, evaluator, listener);
				} else {
					value = setterTypeConverter.fromConfiguration(lookup, config, setterParamType, object.getClass(),
							classLoader, evaluator, listener);
				}

				if (value != null) {
					setValueUsingSetter(value);

					return;
				}
			} catch (final ComponentConfigurationException e) {
				if (fieldTypeConverter == null
						|| fieldTypeConverter.getClass().equals(setterTypeConverter.getClass())) {
					throw e;
				}
			}
		}

		// try setting field using value found with method
		// converter, if present.

		ComponentConfigurationException savedEx = null;

		if (value != null) {
			try {
				setValueUsingField(value);
				return;
			} catch (final ComponentConfigurationException e) {
				savedEx = e;
			}
		}

		// either no value or setting went wrong. Try
		// new converter.

		if (fieldTypeArguments != null) {
			final ParameterizedConfigurationConverter converter = (ParameterizedConfigurationConverter) fieldTypeConverter;
			value = converter.fromConfiguration(lookup, config, fieldType, fieldTypeArguments, object.getClass(),
					classLoader, evaluator, listener);
		} else {
			value = fieldTypeConverter.fromConfiguration(lookup, config, fieldType, object.getClass(), classLoader,
					evaluator, listener);
		}

		if (value != null) {
			setValueUsingField(value);
		}
		// FIXME: need this?
		else if (savedEx != null) {
			throw savedEx;
		}
	}

}

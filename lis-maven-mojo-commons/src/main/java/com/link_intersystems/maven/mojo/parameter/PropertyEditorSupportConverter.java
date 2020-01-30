package com.link_intersystems.maven.mojo.parameter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class PropertyEditorSupportConverter extends AbstractConfigurationConverter {

	private PropertyEditorLookup propertyEditorLookup;

	public PropertyEditorSupportConverter(PropertyEditorLookup propertyEditorLookup) {
		this.propertyEditorLookup = propertyEditorLookup;
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return true;
	}

	@SuppressWarnings("rawtypes")
	public Object fromConfiguration(ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
			Class baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator,
			ConfigurationListener listener) throws ComponentConfigurationException {
		if (isPropertyEditorSupportConversion(configuration, type, baseType, classLoader, expressionEvaluator,
				listener)) {

			return null;
		} else {
			ConfigurationConverter lookupConverterForType = converterLookup.lookupConverterForType(type);
			return lookupConverterForType.fromConfiguration(converterLookup, configuration, type, baseType, classLoader,
					expressionEvaluator);
		}
	}

	private boolean isPropertyEditorSupportConversion(PlexusConfiguration configuration, Class<?> type,
			Class<?> baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator,
			ConfigurationListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	public void processConfiguration(ConverterLookup converterLookup, Object object, ClassLoader classLoader,
			PlexusConfiguration configuration) throws ComponentConfigurationException {
		processConfiguration(converterLookup, object, classLoader, configuration, null);
	}

	public void processConfiguration(ConverterLookup converterLookup, Object object, ClassLoader classLoader,
			PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator)
			throws ComponentConfigurationException {
		processConfiguration(converterLookup, object, classLoader, configuration, expressionEvaluator, null);
	}

	public void processConfiguration(ConverterLookup converterLookup, Object object, ClassLoader classLoader,
			PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator, ConfigurationListener listener)
			throws ComponentConfigurationException {
		int items = configuration.getChildCount();

		for (int i = 0; i < items; i++) {
			PlexusConfiguration childConfiguration = configuration.getChild(i);

			String elementName = childConfiguration.getName();

			Class<?> implementation;
			try {
				implementation = getClassForImplementationHint(null, childConfiguration, classLoader);
			} catch (Exception e) {
				implementation = null; // fall back to original behavior
			}

			ComponentValueSetter valueSetter = new ComponentValueSetter(fromXML(elementName), implementation, object,
					converterLookup, listener);

			valueSetter.configure(childConfiguration, classLoader, expressionEvaluator);
		}

		Class<?> mojoClass = object.getClass();

		while (mojoClass != Object.class) {
			Field[] declaredFields = mojoClass.getDeclaredFields();
			List<PropertyEditorField> editorSupportFields = new ArrayList<PropertyEditorField>();
			for (Field declaredField : declaredFields) {
				if (declaredField.isAnnotationPresent(PropertyEditor.class)) {
					PropertyEditorField propertyEditorField = new PropertyEditorField(object, declaredField);
					editorSupportFields.add(propertyEditorField);
				}
			}

			handlePropertyEditorFields(object, editorSupportFields, expressionEvaluator);
			mojoClass = mojoClass.getSuperclass();
		}
	}

	private void handlePropertyEditorFields(Object mojo, List<PropertyEditorField> editorSupportFields,
			ExpressionEvaluator expressionEvaluator) {
		for (PropertyEditorField propertyEditorField : editorSupportFields) {
			PropertyEditorComponent<?> propertyEditorComponent = propertyEditorLookup
					.getPropertyEditorComponent(propertyEditorField);
			String propertyName = propertyEditorField.getProperty();
			Object propValue;
			try {
				propValue = expressionEvaluator.evaluate("${" + propertyName + "}");
				if (propValue == null) {
					continue;
				}
				String propertyValueString = null;
				propertyValueString = propValue.toString();
				propertyEditorField.setValue(propertyEditorComponent, propertyValueString);
			} catch (ExpressionEvaluationException e) {
				throw new IllegalStateException(
						"Unable to inject " + propertyEditorField + " with property named " + propertyName, e);
			}
		}
	}

}

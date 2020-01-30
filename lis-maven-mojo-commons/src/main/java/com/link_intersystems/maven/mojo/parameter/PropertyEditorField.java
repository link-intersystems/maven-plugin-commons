package com.link_intersystems.maven.mojo.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class PropertyEditorField {

	private Field propertyEditorField;
	private PropertyEditor propertyEditor;
	private Object mojo;

	public PropertyEditorField(Object mojo, Field propertyEditorField) {
		this.mojo = mojo;
		this.propertyEditorField = propertyEditorField;
		propertyEditor = propertyEditorField.getAnnotation(PropertyEditor.class);
	}

	public Class<?> getType() {
		return propertyEditorField.getType();
	}

	private PropertyEditor getPropertyEditor() {
		return propertyEditor;
	}

	public List<String> getPropertyEditorHints() {
		PropertyEditor propertyEditor = getPropertyEditor();
		List<String> propertyEditorHints = new ArrayList<String>();

		if (StringUtils.isNotBlank(propertyEditor.propertyEditorHint())) {
			propertyEditorHints.add(propertyEditor.propertyEditorHint());
		}
		Class<?> type = getType();
		propertyEditorHints.add(type.getCanonicalName());

		if (type.isArray()) {
			Class<?> componentType = type.getComponentType();
			propertyEditorHints.add(componentType.getCanonicalName());
		}
		return propertyEditorHints;
	}

	public Class<? extends PropertyEditorComponent<?>> getPropertyEditorClass() {
		PropertyEditor propertyEditor = getPropertyEditor();
		return propertyEditor.propertyEditorClass();
	}

	public String getProperty() {
		String property = getPropertyEditor().property();
		if (StringUtils.isBlank(property)) {
			property = propertyEditorField.getName();
		}
		return property;
	}

	public void setValue(PropertyEditorComponent<?> propertyEditorComponent, String propValue) {
		Object parsedProperty = null;
		if (propValue != null) {
			parsedProperty = propertyEditorComponent.parseProperty(propValue);
		}
		propertyEditorField.setAccessible(true);
		Object fieldValue = parsedProperty;
		Class<?> type = getType();
		if (type.isArray()) {
			if (parsedProperty != null && !parsedProperty.getClass().isArray()) {
				fieldValue = Array.newInstance(type.getComponentType(), 1);
				Array.set(fieldValue, 0, parsedProperty);
			}
		}
		try {
			propertyEditorField.set(mojo, fieldValue);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to set field " + getType() + "." + propertyEditorField.getName(),
					e);
		}

	}

	@Override
	public String toString() {
		StringBuilder toStringBuilder = new StringBuilder();
		Annotation[] annotations = propertyEditorField.getAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			Annotation annotation = annotations[i];
			toStringBuilder.append(annotation.toString());
			toStringBuilder.append("\n");
		}

		toStringBuilder.append(Modifier.toString(propertyEditorField.getModifiers()));
		toStringBuilder.append(" ");
		toStringBuilder.append(getType().getName());
		toStringBuilder.append(".");
		toStringBuilder.append(propertyEditorField.getName());

		return toStringBuilder.toString();
	}

}

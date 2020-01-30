package com.link_intersystems.maven.mojo.parameter;

import java.util.List;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class PropertyEditorLookup {

	private PlexusContainer container;

	public PropertyEditorLookup(PlexusContainer container) {
		this.container = container;
	}

	public PropertyEditorComponent<?> getPropertyEditorComponent(PropertyEditorField propertyEditorField) {
		PropertyEditorComponent<?> propertyEditorComponent = null;

		Class<? extends PropertyEditorComponent<?>> propertyEditorClass = propertyEditorField.getPropertyEditorClass();
		if (propertyEditorClass != PropertyEditor.DEFAULT.class) {
			try {
				propertyEditorComponent = (PropertyEditorComponent<?>) propertyEditorClass.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(
						"Unable to instantiate a " + PropertyEditorComponent.class + " using " + propertyEditorClass,
						e);
			}
		} else {
			List<String> propertyEditorHints = propertyEditorField.getPropertyEditorHints();
			for (String hint : propertyEditorHints) {
				try {
					propertyEditorComponent = container.lookup(PropertyEditorComponent.class, hint);
					break;
				} catch (ComponentLookupException e) {
				}
			}
		}

		if (propertyEditorComponent == null) {
			List<String> propertyEditorHints = propertyEditorField.getPropertyEditorHints();
			throw new IllegalStateException(
					"Unable to lookup a " + PropertyEditorComponent.class + " using hints: " + propertyEditorHints);
		}

		return propertyEditorComponent;
	}
}

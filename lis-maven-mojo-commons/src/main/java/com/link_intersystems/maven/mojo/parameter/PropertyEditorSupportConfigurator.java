package com.link_intersystems.maven.mojo.parameter;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

@Component(role = ComponentConfigurator.class, hint = "editorSupport")
public class PropertyEditorSupportConfigurator extends AbstractComponentConfigurator {

	@Requirement
	private PlexusContainer container;

	@Override
	public void configureComponent(Object component, PlexusConfiguration configuration,
			ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm, ConfigurationListener listener)
			throws ComponentConfigurationException {
		converterLookup.registerConverter(new ClassRealmConverter(containerRealm));
		PropertyEditorLookup propertyEditorLookup = new PropertyEditorLookup(container);
		PropertyEditorSupportConverter converter = new PropertyEditorSupportConverter(propertyEditorLookup);

		converter.processConfiguration(converterLookup, component, containerRealm, configuration, expressionEvaluator,
				listener);
	}

}

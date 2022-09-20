package com.link_intersystems.maven.plugin;

import org.apache.commons.io.input.XmlStreamReader;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptorBuilder;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@Component(role = PluginMetadata.class)
public class PluginMetadata {

    @Requirement
    private PlexusContainer container;

    @Requirement
    private ComponentConfigurator configurator;

    @Requirement
    private RepositorySystem repositorySystem;
    private HashMap<String, MojoDescriptor> mojoDescriptors;


    public Map<String, MojoDescriptor> getMojoDescriptors() {
        if (mojoDescriptors == null) {
            try {
                mojoDescriptors = loadMojoDescriptors();
            } catch (IOException | PlexusConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        return mojoDescriptors;
    }

    private HashMap<String, MojoDescriptor> loadMojoDescriptors() throws IOException, PlexusConfigurationException {
        InputStream is = getClass().getResourceAsStream("/" + getPluginDescriptorLocation());

        XmlStreamReader reader = new XmlStreamReader(is);
        Map contextData = container.getContext().getContextData();
        InterpolationFilterReader interpolationFilterReader = new InterpolationFilterReader(new BufferedReader(reader), contextData);

        PluginDescriptor pluginDescriptor = new PluginDescriptorBuilder().build(interpolationFilterReader);

        Artifact artifact = repositorySystem.createArtifact(pluginDescriptor.getGroupId(),
                pluginDescriptor.getArtifactId(),
                pluginDescriptor.getVersion(), ".jar");

        getPluginArtifactFile().ifPresent(artifact::setFile);
        pluginDescriptor.setPluginArtifact(artifact);
        pluginDescriptor.setArtifacts(Arrays.asList(artifact));

        HashMap<String, MojoDescriptor> mojoDescriptors = new HashMap<>();
        for (MojoDescriptor mojoDescriptor : pluginDescriptor.getMojos()) {
            mojoDescriptors.put(mojoDescriptor.getGoal(), mojoDescriptor);
        }

        return mojoDescriptors;
    }

    protected String getPluginDescriptorLocation() {
        return "META-INF/maven/plugin.xml";
    }

    private Optional<File> getPluginArtifactFile() {
        final String pluginDescriptorLocation = getPluginDescriptorLocation();
        final URL resource = getClass().getResource("/" + pluginDescriptorLocation);

        File file = null;

        // attempt to resolve relative to META-INF/maven/plugin.xml first
        if (resource != null) {
            if ("file".equalsIgnoreCase(resource.getProtocol())) {
                String path = resource.getPath();
                if (path.endsWith(pluginDescriptorLocation)) {
                    file = new File(path.substring(0, path.length() - pluginDescriptorLocation.length()));
                }
            } else if ("jar".equalsIgnoreCase(resource.getProtocol())) {
                // TODO is there a helper for this somewhere?
                try {
                    URL jarfile = new URL(resource.getPath());
                    if ("file".equalsIgnoreCase(jarfile.getProtocol())) {
                        String path = jarfile.getPath();
                        if (path.endsWith(pluginDescriptorLocation)) {
                            file =
                                    new File(path.substring(0, path.length() - pluginDescriptorLocation.length() - 2));
                        }
                    }
                } catch (MalformedURLException e) {
                    // not jar:file:/ URL, too bad
                }
            }
        }

        return Optional.ofNullable(file);
    }

}

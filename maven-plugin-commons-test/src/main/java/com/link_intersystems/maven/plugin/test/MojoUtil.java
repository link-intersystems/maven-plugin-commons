package com.link_intersystems.maven.plugin.test;

import com.link_intersystems.lang.ClassFileCodeSourceLocationResolver;
import com.link_intersystems.lang.CodeSourceLocationResolverChain;
import com.link_intersystems.lang.ProtectionDomainCondeSourceLocationResolver;
import org.apache.commons.io.input.XmlStreamReader;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.*;
import org.apache.maven.lifecycle.internal.MojoDescriptorCreator;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptorBuilder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MojoUtil {

    public static final String MAVEN_PLUGIN_XML = "META-INF/maven/plugin.xml";
    private PlexusContainer container;

    private Map<String, MojoDescriptor> mojoDescriptors;

    public MojoUtil(PlexusContainer container) {
        MavenVersion.INSTANCE.assertAtLeast("3.2.3");
        this.container = container;
    }

    private Map<String, MojoDescriptor> getMojoDescriptors() {
        if (mojoDescriptors == null) {
            try {
                mojoDescriptors = findMojoDescriptors();
            } catch (ComponentLookupException | PlexusConfigurationException | IOException |
                     CycleDetectedInComponentGraphException e) {
                throw new RuntimeException(e);
            }
        }
        return mojoDescriptors;
    }

    protected Map<String, MojoDescriptor> findMojoDescriptors() throws ComponentLookupException, PlexusConfigurationException, IOException, CycleDetectedInComponentGraphException {
        Map<String, MojoDescriptor> mojoDescriptors = new HashMap<>();

        List<PluginDescriptor> pluginDescriptors = getPluginDescriptors();

        for (PluginDescriptor pluginDescriptor : pluginDescriptors) {


            for (MojoDescriptor mojoDescriptor : pluginDescriptor.getMojos()) {
                mojoDescriptors.put(mojoDescriptor.getGoal(), mojoDescriptor);
            }
        }

        return mojoDescriptors;
    }

    public void registerPluginArtifact(Class<? extends Mojo> mojoClass) throws PlexusConfigurationException, IOException, ComponentLookupException, CycleDetectedInComponentGraphException {
        URL pluginArtifactResource = mojoClass.getResource("/" + MAVEN_PLUGIN_XML);
        PluginDescriptor pluginDescriptor = readPluginDescriptor(pluginArtifactResource);


        Artifact artifact =
                container.lookup(RepositorySystem.class).createArtifact(pluginDescriptor.getGroupId(),
                        pluginDescriptor.getArtifactId(),
                        pluginDescriptor.getVersion(), ".jar");

        artifact.setFile(getCodeSourceLocation(mojoClass));
        pluginDescriptor.setPluginArtifact(artifact);
        pluginDescriptor.setArtifacts(Arrays.asList(artifact));

        for (ComponentDescriptor<?> desc : pluginDescriptor.getComponents()) {
            container.addComponentDescriptor(desc);
        }
    }

    protected List<PluginDescriptor> getPluginDescriptors() throws IOException, PlexusConfigurationException {
        List<PluginDescriptor> pluginDescriptors = new ArrayList<>();

        Enumeration<URL> pluginDescriptorResources = iteratePluginDescriptorResources();
        while (pluginDescriptorResources.hasMoreElements()) {
            URL url = pluginDescriptorResources.nextElement();

            PluginDescriptor pluginDescriptor = readPluginDescriptor(url);
            pluginDescriptors.add(pluginDescriptor);

        }

        return pluginDescriptors;
    }

    protected Enumeration<URL> iteratePluginDescriptorResources() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResources(MAVEN_PLUGIN_XML);
    }

    private PluginDescriptor readPluginDescriptor(URL url) throws IOException, PlexusConfigurationException {
        try (InputStream in = url.openStream()) {
            XmlStreamReader reader = new XmlStreamReader(in);

            Map contextData = container.getContext().getContextData();
            InterpolationFilterReader interpolationFilterReader =
                    new InterpolationFilterReader(new BufferedReader(reader), contextData);

            return new PluginDescriptorBuilder().build(interpolationFilterReader);
        }
    }

    /**
     * Returns best-effort plugin artifact file.
     * <p>
     * First, attempts to determine parent directory of META-INF directory holding the plugin descriptor. If META-INF
     * parent directory cannot be determined, falls back to test basedir.
     */
    private File getCodeSourceLocation(Class<? extends Mojo> mojoClass) {

        CodeSourceLocationResolverChain codeSourceLocationResolvers = new CodeSourceLocationResolverChain();
        codeSourceLocationResolvers.add(new ProtectionDomainCondeSourceLocationResolver());
        codeSourceLocationResolvers.add(new ClassFileCodeSourceLocationResolver());

        Optional<URL> codeSourceLocation = codeSourceLocationResolvers.resolve(mojoClass);
        return codeSourceLocation.map(this::toFile).orElse(null);
    }

    private File toFile(URL url) {
        if (url == null) {
            return null;
        }
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected Mojo lookupConfiguredMojo(MavenProject project, String goal)
            throws Exception {
        return lookupConfiguredMojo(newMavenSession(project), newMojoExecution(goal));
    }

    /**
     * @param project
     * @return
     * @since 2.0
     */
    protected MavenSession newMavenSession(MavenProject project) {
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        MavenExecutionResult result = new DefaultMavenExecutionResult();

        MavenSession session = new MavenSession(container, MavenRepositorySystemUtils.newSession(), request, result);
        session.setCurrentProject(project);
        session.setProjects(Arrays.asList(project));
        return session;
    }

    protected MojoExecution newMojoExecution(String goal) {
        MojoDescriptor mojoDescriptor = getMojoDescriptors().get(goal);
        assertNotNull(mojoDescriptor, String.format("The MojoDescriptor for the goal %s cannot be null.", goal));
        MojoExecution execution = new MojoExecution(mojoDescriptor);
        finalizeMojoConfiguration(execution);
        return execution;
    }


    private void finalizeMojoConfiguration(MojoExecution mojoExecution) {
        MojoDescriptor mojoDescriptor = mojoExecution.getMojoDescriptor();

        Xpp3Dom executionConfiguration = mojoExecution.getConfiguration();
        if (executionConfiguration == null) {
            executionConfiguration = new Xpp3Dom("configuration");
        }

        Xpp3Dom defaultConfiguration = MojoDescriptorCreator.convert(mojoDescriptor);
        ;

        Xpp3Dom finalConfiguration = new Xpp3Dom("configuration");

        if (mojoDescriptor.getParameters() != null) {
            for (Parameter parameter : mojoDescriptor.getParameters()) {
                Xpp3Dom parameterConfiguration = executionConfiguration.getChild(parameter.getName());

                if (parameterConfiguration == null) {
                    parameterConfiguration = executionConfiguration.getChild(parameter.getAlias());
                }

                Xpp3Dom parameterDefaults = defaultConfiguration.getChild(parameter.getName());

                parameterConfiguration = Xpp3Dom.mergeXpp3Dom(parameterConfiguration, parameterDefaults, Boolean.TRUE);

                if (parameterConfiguration != null) {
                    parameterConfiguration = new Xpp3Dom(parameterConfiguration, parameter.getName());

                    if (StringUtils.isEmpty(parameterConfiguration.getAttribute("implementation"))
                            && StringUtils.isNotEmpty(parameter.getImplementation())) {
                        parameterConfiguration.setAttribute("implementation", parameter.getImplementation());
                    }

                    finalConfiguration.addChild(parameterConfiguration);
                }
            }
        }

        mojoExecution.setConfiguration(finalConfiguration);
    }


    protected Mojo lookupConfiguredMojo(MavenSession session, MojoExecution execution)
            throws Exception {
        MavenProject project = session.getCurrentProject();
        MojoDescriptor mojoDescriptor = execution.getMojoDescriptor();

        Mojo mojo = (Mojo) container.lookup(mojoDescriptor.getRole(), mojoDescriptor.getRoleHint());

        ExpressionEvaluator evaluator = new PluginParameterExpressionEvaluator(session, execution);

        Xpp3Dom configuration = null;
        Plugin plugin = project.getPlugin(mojoDescriptor.getPluginDescriptor().getPluginLookupKey());
        if (plugin != null) {
            configuration = (Xpp3Dom) plugin.getConfiguration();
        }
        if (configuration == null) {
            configuration = new Xpp3Dom("configuration");
        }
        configuration = Xpp3Dom.mergeXpp3Dom(configuration, execution.getConfiguration());

        PlexusConfiguration pluginConfiguration = new XmlPlexusConfiguration(configuration);

        ComponentConfigurator configurator;

        if (mojoDescriptor.getComponentConfigurator() != null) {
            configurator = container.lookup(ComponentConfigurator.class, mojoDescriptor.getComponentConfigurator());
        } else {
            configurator = container.lookup(ComponentConfigurator.class, "basic");
        }

        configurator.configureComponent(mojo, pluginConfiguration, evaluator, container.getContainerRealm());

        return mojo;
    }

    private static final class Lazy {
        static {
            final String path = System.getProperty("basedir");
            BASEDIR = null != path ? path : new File("").getAbsolutePath();
        }

        static final String BASEDIR;
    }

    public static String getBasedir() {
        return Lazy.BASEDIR;
    }

}

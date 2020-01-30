package com.link_intersystems.maven.jdbc;

import org.apache.maven.plugins.annotations.Parameter;

import com.link_intersystems.maven.mojo.AbstractMavenContextMojo;
import com.link_intersystems.maven.mojo.Goal;
import com.link_intersystems.maven.mojo.MavenContext;

public abstract class AbstractJdbcMojo<T extends Goal<PARAMS>, PARAMS extends JdbcParams>
		extends AbstractMavenContextMojo<T, PARAMS> implements JdbcParams {

	/**
	 * The jdbc driver configuration that should be used to connect to the
	 * database. <br/>
	 * Config example<br/>
	 *
	 * <pre>
	 * &lt;driverConfig&gt;
	 *    &lt;url&gt;jdbc:db2://someserver.net:50000/DATABASE:retrieveMessagesFromServerOnGetMessage=true;&lt;/url&gt;
	 *    &lt;driverClass&gt;com.ibm.db2.jcc.DB2Driver&lt;/driverClass&gt;
	 *    &lt;username&gt;username&lt;/username&gt;
	 *    &lt;password&gt;password&lt;/password&gt;
	 * &lt;/driverConfig&gt;
	 *
	 * or
	 *
	 * &lt;driverConfig&gt;
	 *    &lt;url&gt;jdbc:db2://someserver.net:50000/DATABASE:retrieveMessagesFromServerOnGetMessage=true;&lt;/url&gt;
	 *    &lt;driverClass&gt;com.ibm.db2.jcc.DB2Driver&lt;/driverClass&gt;
	 *    &lt;serverId&gt;settings.xml_serverId&lt;/serverId&gt;
	 * &lt;/driverConfig&gt;
	 *
	 * </pre>
	 *
	 */
	@Parameter
	protected DataSourceConfigParam driverConfig;

	public DataSourceConfig getDataSourceConfig() {
		MavenContext mavenContext = getMavenContext();
		DataSourceConfig richDriverConfig = new DataSourceConfig(mavenContext, driverConfig);
		return richDriverConfig;
	}
}
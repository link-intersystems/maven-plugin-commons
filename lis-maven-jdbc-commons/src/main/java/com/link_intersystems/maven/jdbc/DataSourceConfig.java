package com.link_intersystems.maven.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;

import com.link_intersystems.maven.mojo.GoalParameterException;
import com.link_intersystems.maven.mojo.MavenContext;

public class DataSourceConfig {

	private DataSourceConfigParam dataSourceConfigParam;
	private MavenContext mavenContext;
	private JdbcURL jdbcUrl;

	public DataSourceConfig(MavenContext mavenContext, DataSourceConfigParam dataSourceConfigParam) {
		this.mavenContext = mavenContext;
		this.dataSourceConfigParam = dataSourceConfigParam;
	}

	protected MavenContext getMavenContext() {
		return mavenContext;
	}

	public String getUrl() throws GoalParameterException {
		String url = dataSourceConfigParam.getUrl();
		if (StringUtils.isBlank(url)) {
			throw new GoalParameterException("A driver url must be configured via driverConfig/url");
		}
		return url.trim();
	}

	public JdbcURL getJdbcUrl() {
		if (jdbcUrl == null) {
			jdbcUrl = new JdbcURL(getUrl());
		}
		return jdbcUrl;
	}

	public String getDriverClass() throws MojoExecutionException {
		String driverClass = dataSourceConfigParam.getDriverClass();
		if (StringUtils.isBlank(driverClass)) {
			throw new MojoExecutionException("A driver class must be configured via driverConfig/driverClass");
		}
		return driverClass.trim();
	}

	public String getUsername() throws GoalParameterException {
		String username = dataSourceConfigParam.getUsername();
		if (StringUtils.isBlank(username)) {
			String serverId = dataSourceConfigParam.getServerId();
			if (StringUtils.isBlank(serverId)) {
				throw new GoalParameterException("No username nor a serverId is configured.");
			}
			Server serverSettings = mavenContext.getServerSettings(serverId.trim());
			if (serverSettings == null) {
				throw new GoalParameterException("Maven settings does not contain server configuration " + serverId);
			}
			username = serverSettings.getUsername();
			if (StringUtils.isBlank(username)) {
				throw new GoalParameterException(
						"Maven settings server configuration " + serverId + " does not contain a username.");
			}
		}
		return username.trim();
	}

	public String getPassword() throws GoalParameterException {
		String password = dataSourceConfigParam.getPassword();
		if (password == null) {
			String serverId = dataSourceConfigParam.getServerId();
			if (StringUtils.isBlank(serverId)) {
				throw new GoalParameterException("No passowrd nor a serverId is configured.");
			}
			Server serverSettings = mavenContext.getServerSettings(serverId.trim());
			if (serverSettings == null) {
				throw new GoalParameterException("Maven settings does not contain server configuration " + serverId);
			}
			password = serverSettings.getPassword();
		}
		if (password == null) {
			password = "";
		}
		return password;
	}

	public String getSchema() {
		return dataSourceConfigParam.getSchema();
	}

	public String getHostname() throws MojoExecutionException {
		JdbcURL jdbcUrlAnalyser = new JdbcURL(getUrl());
		return jdbcUrlAnalyser.getHostname();
	}

	public Connection createJDBCConnection() throws MojoExecutionException {
		String driverClass = getDriverClass();
		try {
			Class.forName(driverClass);
			String url = getUrl();
			String user = getUsername();
			String password = getPassword();
			Connection jdbcConnection = DriverManager.getConnection(url, user, password);
			return jdbcConnection;
		} catch (Exception e) {
			throw new MojoExecutionException("Unable to create jdbc database connection", e);
		}
	}
}

package com.link_intersystems.maven.jdbc;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;

public class JdbcURL {

	private URI jdbcURI;

	public JdbcURL(String jdbcUrl) {
		if (StringUtils.isBlank(jdbcUrl)) {
			throw new IllegalArgumentException("jdbcUrl must not be blank");
		}
		jdbcUrl = jdbcUrl.trim();
		if (!jdbcUrl.startsWith("jdbc:")) {
			throw new IllegalArgumentException("Argument jdbcUrl is not a valid jdbc url: " + jdbcUrl);
		}
		try {
			this.jdbcURI = new URI(jdbcUrl);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("jdbcUrl is not a valid URI", e);
		}
	}

	/*
	 * Returns the "jdbc:" protocol of the url parameter. Looks for the prefix
	 * string up to the 3rd ':' or the 1st '@', '/' or '\', whichever comes
	 * first.
	 *
	 * This method is package qualified so that TestDictionaryFactory class can
	 * access and test this method behavior.
	 */
	public String getProtocol() {
		String schemeSpecificPart = jdbcURI.getSchemeSpecificPart();
		String dbProtocol = StringUtils.substringBefore(schemeSpecificPart, ":");
		return dbProtocol;
	}

	public String getHostname() {
		String schemeSpecificPart = jdbcURI.getSchemeSpecificPart();
		String protocol = StringUtils.substringBefore(schemeSpecificPart, ":");
		String schemeSpecificPartWithoutProtocol = StringUtils.removeStart(schemeSpecificPart, protocol + "://");
		String hostname = StringUtils.substringBefore(schemeSpecificPartWithoutProtocol, "/");

		if (hostname.contains("@")) {
			hostname = StringUtils.substringAfter(hostname, "@");
		}
		if (hostname.contains(":")) {
			hostname = StringUtils.substringBefore(hostname, ":");
		}
		return hostname;
	}

	public URI getJDBCUri() {
		return jdbcURI;
	}

	@Override
	public String toString() {
		return jdbcURI.toString();
	}
}

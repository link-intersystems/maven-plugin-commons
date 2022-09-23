package com.link_intersystems.maven.plugin.test;

import org.apache.maven.plugin.Mojo;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class TestMojoConfig {

    private Class<? extends Mojo> mojoType;
    private TestMojo testMojo;

    public TestMojoConfig(Class<? extends Mojo> mojoType, TestMojo testMojo) {
        this.mojoType = mojoType;
        this.testMojo = testMojo;
    }

    public Class<? extends Mojo> getMojoType() {
        return mojoType;
    }

    public TestMojo getTestMojo() {
        return testMojo;
    }
}

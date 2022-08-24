package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class OffLog extends AbstractLog {
    public static final Log INSTANCE = new OffLog();

    @Override
    protected void logLevel(Level level, CharSequence content) {
    }

    @Override
    protected void logLevel(Level level, CharSequence content, Throwable error) {
    }

    @Override
    protected void logLevel(Level level, Throwable error) {
    }

    @Override
    protected boolean isLevelEnabled(Level level) {
        return false;
    }
}

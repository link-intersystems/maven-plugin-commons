package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FilterLog extends DelegatingLog {
    private Level level = Level.info;

    public FilterLog(Log log) {
        super(log);
    }

    public void setLevel(Level level) {
        this.level = requireNonNull(level);
    }

    @Override
    protected boolean isLevelEnabled(Level level) {
        return this.level.contains(level);
    }


}

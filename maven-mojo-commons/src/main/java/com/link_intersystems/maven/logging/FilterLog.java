package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FilterLog extends AbstractLog {

    private Log log;
    private Level level = Level.info;

    public FilterLog(Log log) {
        this.log = requireNonNull(log);
    }

    public void setLevel(Level level) {
        this.level = requireNonNull(level);

        for (Level aLevel : Level.values()) {
            aLevel.setEnabled(this, level.contains(aLevel));
        }
    }

    public Level getLevel() {
        return level;
    }

    @Override
    protected void doLogLevel(Level level, CharSequence content) {
        if (this.level.contains(level)) {
            level.log(log, content);
        }
    }

    @Override
    protected void doLogLevel(Level level, CharSequence content, Throwable error) {
        if (this.level.contains(level)) {
            level.log(log, content, error);
        }
    }

    @Override
    protected void doLogLevel(Level level, Throwable error) {
        if (this.level.contains(level)) {
            level.log(log, error);
        }
    }
}

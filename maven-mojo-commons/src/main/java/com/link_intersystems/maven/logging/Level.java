package com.link_intersystems.maven.logging;

import org.apache.maven.plugin.logging.Log;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public enum Level {
    off {
        @Override
        public boolean isEnabled(Log log) {
            return false;
        }

        @Override
        public void setEnabled(AbstractLog log, boolean enabled) {
        }

        @Override
        public void log(Log log, CharSequence message) {
        }

        @Override
        public void log(Log log, Throwable throwable) {
        }

        @Override
        public void log(Log log, CharSequence message, Throwable throwable) {
        }
    },
    info {
        @Override
        public boolean isEnabled(Log log) {
            return log.isInfoEnabled();
        }

        @Override
        public void setEnabled(AbstractLog log, boolean enabled) {
            log.setInfoEnabled(enabled);
        }

        @Override
        public void log(Log log, CharSequence message) {
            log.info(message);
        }

        @Override
        public void log(Log log, Throwable throwable) {
            log.info(throwable);
        }

        @Override
        public void log(Log log, CharSequence message, Throwable throwable) {
            log.info(message, throwable);
        }
    },
    warn {
        @Override
        public boolean isEnabled(Log log) {
            return log.isWarnEnabled();
        }

        @Override
        public void setEnabled(AbstractLog log, boolean enabled) {
            log.setWarnEnabled(enabled);
        }

        @Override
        public void log(Log log, CharSequence message) {
            log.warn(message);
        }


        @Override
        public void log(Log log, Throwable throwable) {
            log.warn(throwable);
        }

        @Override
        public void log(Log log, CharSequence message, Throwable throwable) {
            log.warn(message, throwable);
        }
    },
    error {
        @Override
        public boolean isEnabled(Log log) {
            return log.isErrorEnabled();
        }

        @Override
        public void setEnabled(AbstractLog log, boolean enabled) {
            log.setErrorEnabled(enabled);
        }

        @Override
        public void log(Log log, CharSequence message) {
            log.error(message);
        }

        @Override
        public void log(Log log, Throwable throwable) {
            log.error(throwable);
        }

        @Override
        public void log(Log log, CharSequence message, Throwable throwable) {
            log.error(message, throwable);
        }
    },
    debug {
        @Override
        public boolean isEnabled(Log log) {
            return log.isDebugEnabled();
        }

        @Override
        public void setEnabled(AbstractLog log, boolean enabled) {
            log.setDebugEnabled(enabled);
        }

        @Override
        public void log(Log log, CharSequence message) {
            log.debug(message);
        }


        @Override
        public void log(Log log, Throwable throwable) {
            log.debug(throwable);
        }

        @Override
        public void log(Log log, CharSequence message, Throwable throwable) {
            log.debug(message, throwable);
        }
    };

    public abstract boolean isEnabled(Log log);

    public abstract void setEnabled(AbstractLog log, boolean enabled);

    public abstract void log(Log log, CharSequence message);

    public abstract void log(Log log, Throwable throwable);

    public abstract void log(Log log, CharSequence message, Throwable throwable);

}

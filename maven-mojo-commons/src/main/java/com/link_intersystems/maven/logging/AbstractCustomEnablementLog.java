package com.link_intersystems.maven.logging;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public abstract class AbstractCustomEnablementLog extends AbstractLog {

    private boolean debugEnabled = false;
    private boolean infoEnabled = true;
    private boolean warnEnabled = true;
    private boolean errorEnabled = true;

    @Override
    protected boolean isLevelEnabled(Level level) {
        switch (level) {
            case debug:
                return debugEnabled;
            case info:
                return infoEnabled;
            case warn:
                return warnEnabled;
            case error:
                return errorEnabled;
            default:
                return false;
        }
    }

    protected void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    protected void setInfoEnabled(boolean infoEnabled) {
        this.infoEnabled = infoEnabled;
    }

    protected void setWarnEnabled(boolean warnEnabled) {
        this.warnEnabled = warnEnabled;
    }

    protected void setErrorEnabled(boolean errorEnabled) {
        this.errorEnabled = errorEnabled;
    }


}

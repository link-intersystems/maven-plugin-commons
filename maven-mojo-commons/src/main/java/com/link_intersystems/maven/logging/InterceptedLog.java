package com.link_intersystems.maven.logging;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class InterceptedLog extends AbstractInterceptedLog {

    @Override
    public void setEnabledInterceptor(EnabledInterceptor enabledInterceptor) {
        super.setEnabledInterceptor(enabledInterceptor);
    }

    @Override
    public void setLogInterceptor(LogInterceptor logInterceptor) {
        super.setLogInterceptor(logInterceptor);
    }
}

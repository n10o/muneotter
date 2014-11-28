/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.bizreach.twitter.projectfw.core.direction;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.dbflute.saflute.core.direction.BootProcessCallback;
import org.dbflute.saflute.core.direction.CachedFwAssistantDirector;
import org.dbflute.saflute.core.direction.FwAssistantDirector;
import org.dbflute.saflute.core.direction.OptionalAssistDirection;
import org.dbflute.saflute.core.direction.OptionalCoreDirection;
import org.dbflute.saflute.core.security.InvertibleCipher;
import org.dbflute.saflute.core.security.SecurityResourceProvider;
import org.dbflute.saflute.core.time.BusinessTimeHandler;
import org.dbflute.saflute.core.time.TimeManager;
import org.dbflute.saflute.core.time.TimeResourceProvider;
import org.dbflute.saflute.core.time.TypicalBusinessTimeHandler;
import org.dbflute.saflute.db.dbflute.OptionalDBFluteDirection;
import org.dbflute.saflute.web.action.OptionalActionDirection;
import org.dbflute.saflute.web.action.callback.ActionExecuteMeta;
import org.dbflute.saflute.web.action.processor.ActionAdjustmentProvider;
import org.dbflute.saflute.web.action.processor.ActionMappingWrapper;
import org.dbflute.saflute.web.servlet.OptionalServletDirection;
import org.dbflute.saflute.web.servlet.cookie.CookieResourceProvider;
import org.dbflute.saflute.web.servlet.request.RequestManager;
import org.dbflute.saflute.web.servlet.request.UserLocaleProcessProvider;
import org.dbflute.saflute.web.servlet.request.UserTimeZoneProcessProvider;
import org.dbflute.saflute.web.task.OptionalTaskDirection;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.struts.config.S2ExecuteConfig;

/**
 * @author jflute
 */
public abstract class TwitterFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String TWITTER_CONFIG_FILE = "twitter_config.properties";
    public static final String TWITTER_ENV_FILE = "twitter_env.properties";
    public static final String TWITTER_MESSAGE_NAME = "twitter_message";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected TwitterConfig twitterConfig;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected OptionalAssistDirection prepareOptionalAssistDirection() {
        final OptionalAssistDirection direction = new OptionalAssistDirection();
        prepareConfiguration(direction);
        return direction;
    }

    protected void prepareConfiguration(OptionalAssistDirection direction) {
        direction.directConfiguration(getDomainConfigFile(), getExtendsConfigFiles());
    }

    protected abstract String getDomainConfigFile();

    protected String[] getExtendsConfigFiles() {
        return new String[] { TWITTER_CONFIG_FILE, TWITTER_ENV_FILE };
    }

    // ===================================================================================
    //                                                                                Core
    //                                                                                ====
    @Override
    protected OptionalCoreDirection prepareOptionalCoreDirection() {
        final OptionalCoreDirection direction = new OptionalCoreDirection();
        prepareFramework(direction);
        prepareSecurity(direction);
        prepareTime(direction);
        return direction;
    }

    // -----------------------------------------------------
    //                                             Framework
    //                                             ---------
    protected void prepareFramework(OptionalCoreDirection direction) {
        // this configuration is on twitter_env.properties
        // because this is true only when development
        direction.directDevelopmentHere(twitterConfig.isDevelopmentHere());

        // titles are from configurations
        direction.directLoggingTitle(twitterConfig.getDomainTitle(), twitterConfig.getEnvironmentTitle());

        // this configuration is on sea_env.properties
        // because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(twitterConfig.isFrameworkDebug()); // basically false

        // you can add your own process when your application is booting
        direction.directBootProcessCallback(new BootProcessCallback() {
            public void callback(FwAssistantDirector assistantDirector) {
                // nothing for now
            }
        });
    }

    // -----------------------------------------------------
    //                                              Security
    //                                              --------
    protected void prepareSecurity(OptionalCoreDirection direction) {
        final String key = "twitter";
        final InvertibleCipher primaryInvertibleCipher = InvertibleCipher.createBlowfishCipher(key);
        direction.directSecurity(new SecurityResourceProvider() {
            public InvertibleCipher providePrimaryInvertibleCipher() {
                return primaryInvertibleCipher;
            }
        });
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    protected void prepareTime(OptionalCoreDirection direction) {
        direction.directTime(new TimeResourceProvider() {
            public BusinessTimeHandler provideBusinessTimeHandler(TimeManager timeManager) {
                return new TypicalBusinessTimeHandler(timeManager);
            }

            public boolean isCurrentIgnoreTransaction() {
                // this project uses transaction time for current date
                return false; // fixedly
            }

            public long provideAdjustTimeMillis() {
                return twitterConfig.getTimeAdjustTimeMillisAsInteger(); // *1
            }

            public boolean isAdjustAbsoluteMode() {
                return twitterConfig.isTimeAdjustAbsoluteMode(); // *1
            }
            // *1: called per called for dynamic change in development
        });
    }

    // ===================================================================================
    //                                                                                  DB
    //                                                                                  ==
    @Override
    protected OptionalDBFluteDirection prepareOptionalDBFluteDirection() {
        final OptionalDBFluteDirection direction = new OptionalDBFluteDirection();
        return direction;
    }

    // ===================================================================================
    //                                                                                 Web
    //                                                                                 ===
    // -----------------------------------------------------
    //                                                Action
    //                                                ------
    @Override
    protected OptionalActionDirection prepareOptionalActionDirection() {
        final OptionalActionDirection direction = new OptionalActionDirection();
        prepareAdjustment(direction);
        prepareMessage(direction);
        return direction;
    }

    protected void prepareAdjustment(OptionalActionDirection direction) {
        direction.directAdjustment(new ActionAdjustmentProvider() {
            public int provideIndexedPropertySizeLimit() {
                return 200; // hard coding for now
            }

            public String decodeUrlParameterPropertyValue(Object bean, String name, String value) {
                return null;
            }

            public String filterJspPath(String path, ActionMappingWrapper actionMappingWrapper) {
                return null;
            }

            @Override
            public boolean isForcedRoutingTarget(HttpServletRequest request, String requestPath) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isForcedSuppressRedirectWithSlash(HttpServletRequest request, String requestPath,
                    S2ExecuteConfig executeConfig) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    protected void prepareMessage(OptionalActionDirection direction) {
        direction.directMessage(getDomainMessageName(), getExtendsMessageNames());
    }

    protected abstract String getDomainMessageName();

    protected String[] getExtendsMessageNames() {
        return new String[] { TWITTER_MESSAGE_NAME };
    }

    // -----------------------------------------------------
    //                                               Servlet
    //                                               -------
    @Override
    protected OptionalServletDirection prepareOptionalServletDirection() {
        final OptionalServletDirection direction = new OptionalServletDirection();
        prepareRequest(direction);
        prepareCookie(direction);
        return direction;
    }

    protected OptionalServletDirection prepareRequest(OptionalServletDirection direction) {
        direction.directRequest(new UserLocaleProcessProvider() {
            private final boolean acceptCookieLocale = false;

            public boolean isAcceptCookieLocale() {
                return acceptCookieLocale;
            }

            @Override
            public Locale findBusinessLocale(ActionExecuteMeta executeMeta, RequestManager requestManager) {
                return null;
            }

            @Override
            public Locale getRequestedLocale(RequestManager requestManager) {
                return null;
            }

            @Override
            public Locale getFallbackLocale() {
                return null;
            }

            @Override
            public String toString() {
                return "{acceptCookieLocale=" + acceptCookieLocale + "}";
            }
        }, new UserTimeZoneProcessProvider() {
            private final boolean useTimeZoneHandling = false;
            private final boolean acceptCookieTimeZone = false;

            public boolean isUseTimeZoneHandling() {
                return useTimeZoneHandling;
            }

            public boolean isAcceptCookieTimeZone() {
                return acceptCookieTimeZone;
            }

            @Override
            public TimeZone findBusinessTimeZone(ActionExecuteMeta executeMeta, RequestManager requestManager) {
                return null;
            }

            @Override
            public TimeZone getRequestedTimeZone(RequestManager requestManager) {
                return null;
            }

            @Override
            public TimeZone getFallbackTimeZone() {
                return null;
            }

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("{useTimeZoneHandling=").append(useTimeZoneHandling);
                sb.append(", acceptCookieTimeZone=").append(acceptCookieTimeZone).append("}");
                return sb.toString();
            }
        });
        return direction;
    }

    protected void prepareCookie(OptionalServletDirection direction) {
        final String key = "nisetter";
        final String cookieDefaultPath = twitterConfig.getCookieDefaultPath();
        final Integer cookieExpireDefault = twitterConfig.getCookieDefaultExpireAsInteger();
        final InvertibleCipher cookieCipher = InvertibleCipher.createBlowfishCipher(key);
        direction.directCookie(new CookieResourceProvider() {
            public String provideDefaultPath() {
                return cookieDefaultPath;
            }

            public Integer provideDefaultExpire() {
                return cookieExpireDefault;
            }

            public InvertibleCipher provideCipher() {
                return cookieCipher;
            }
        });
    }

    // -----------------------------------------------------
    //                                                  Task
    //                                                  ----
    @Override
    protected OptionalTaskDirection prepareOptionalTaskDirection() {
        return new OptionalTaskDirection();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected <ELEMENT> ArrayList<ELEMENT> newArrayList(ELEMENT... elements) {
        return DfCollectionUtil.newArrayList(elements);
    }
}

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
package jp.bizreach.twitter.projectfw.web.action;

import java.sql.Timestamp;

import javax.annotation.Resource;

import jp.bizreach.twitter.projectfw.core.direction.TwitterConfig;
import jp.bizreach.twitter.projectfw.web.paging.PagingNavi;
import org.dbflute.saflute.db.dbflute.accesscontext.AccessContextArranger;
import org.dbflute.saflute.db.dbflute.accesscontext.AccessContextResource;
import org.dbflute.saflute.web.action.TypicalBaseAction;
import org.dbflute.saflute.web.action.callback.ActionExecuteMeta;
import org.dbflute.saflute.web.action.login.UserBean;
import org.seasar.dbflute.AccessContext;
import org.seasar.dbflute.AccessContext.AccessTimestampProvider;
import org.seasar.dbflute.AccessContext.AccessUserProvider;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.pagenavi.range.PageRangeOption;

/**
 * @author jflute
 */
public abstract class TwitterBaseAction extends TypicalBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected TwitterConfig twitterConfig;

    // ===================================================================================
    //                                                                              Paging
    //                                                                              ======
    /**
     * Create the paging navigation as empty.
     * @return The new-created instance of paging navigation as empty. (NotNull)
     */
    protected PagingNavi createPagingNavi() {
        return new PagingNavi();
    }

    /**
     * Prepare the paging navigation for page-range.
     * @param pagingNavi The paging navigation prepared for the paging data. (NotNull)
     * @param page The selected page as bean of paging result. (NotNull)
     * @param linkPaths The varying array of link paths. (NotNull, EmptyAllowed)
     */
    protected void preparePagingNavi(PagingNavi pagingNavi, PagingResultBean<? extends Entity> page,
            Object... linkPaths) {
        final PageRangeOption option = new PageRangeOption();
        option.setPageRangeSize(twitterConfig.getPagingPageRangeSizeAsInteger());
        option.setFillLimit(twitterConfig.isPagingPageRangeFillLimit());
        pagingNavi.prepare(page, option, linkPaths);
    }

    /**
     * Get page size (record count of one page) for paging.
     * @return The integer as page size. (NotZero, NotMinus)
     */
    protected int getPagingPageSize() {
        return twitterConfig.getPagingPageSizeAsInteger();
    }

    // ===================================================================================
    //                                                                            Callback
    //                                                                            ========
    // to suppress unexpected override by sub-class
    // you should remove the 'final' if you want to override this
    @Override
    public final String godHandActionPrologue(ActionExecuteMeta executeMeta) {
        return super.godHandActionPrologue(executeMeta);
    }

    @Override
    public final String godHandExceptionMonologue(ActionExecuteMeta executeMeta) {
        return super.godHandExceptionMonologue(executeMeta);
    }

    @Override
    public final void godHandActionEpilogue(ActionExecuteMeta executeMeta) {
        if (executeMeta.isForwardToJsp()) {
            noCache();
        }
        super.godHandActionEpilogue(executeMeta);
    }

    // ===================================================================================
    //                                                                      Access Context
    //                                                                      ==============
    @Override
    protected AccessContextArranger createAccessContextArranger() {
        return new AccessContextArranger() {
            private static final int TRACE_COLUMN_SIZE = 200;

            public AccessContext arrangePreparedAccessContext(final AccessContextResource resource) {
                final AccessContext context = new AccessContext();
                // uses provider to synchronize it with transaction time
                context.setAccessTimestampProvider(new AccessTimestampProvider() {
                    public Timestamp getAccessTimestamp() {
                        return timeManager.getCurrentTimestamp();
                    }
                });
                // uses provider to synchronize it with login status in session
                context.setAccessUserProvider(new AccessUserProvider() {
                    public String getAccessUser() {
                        return buildAccessUserTrace(resource);
                    }
                });
                return context;
            }

            private String buildAccessUserTrace(AccessContextResource resource) {
                final UserBean userBean = getUserBean();
                final Long userId = userBean.getUserId();
                final String userType = userBean.getUserType();
                final String domainType = userBean.getDomainType();
                final String moduleName = resource.getModuleName();
                final StringBuilder sb = new StringBuilder();
                sb.append(userType).append(":").append(userId != null ? userId : "-1");
                sb.append(",").append(domainType).append(",").append(moduleName);
                final String trace = sb.toString();
                if (trace.length() > TRACE_COLUMN_SIZE) {
                    return trace.substring(0, TRACE_COLUMN_SIZE);
                }
                return trace;
            }
        };
    }

    // ===================================================================================
    //                                                               Application Exception
    //                                                               =====================
    @Override
    protected String getErrorsAppAlreadyDeletedKey() {
        return TwitterMessages.ERRORS_APP_ALREADY_DELETED;
    }

    @Override
    protected String getErrorsAppAlreadyUpdatedKey() {
        return TwitterMessages.ERRORS_APP_ALREADY_UPDATED;
    }

    @Override
    protected String getErrorsAppAlreadyExistsKey() {
        return TwitterMessages.ERRORS_APP_ALREADY_EXISTS;
    }

    @Override
    protected String getErrorsNotLoginKey() {
        return TwitterMessages.ERRORS_NOT_LOGIN;
    }

    @Override
    protected String getErrorsAppIllegalTransitionKey() {
        return TwitterMessages.ERRORS_APP_ILLEGAL_TRANSITION;
    }

    // ===================================================================================
    //                                                                    Response Control
    //                                                                    ================
    /**
     * Cache-Control "no-cache, no-store"
     */
    protected void noCache() {
        responseManager.addNoCache();
    }
}

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
package jp.bizreach.twitter.app.web.base;

import javax.annotation.Resource;

import jp.bizreach.twitter.app.logic.MemberLoginLogic;
import jp.bizreach.twitter.domainfw.action.NisetterJspPath;
import jp.bizreach.twitter.domainfw.action.NisetterUserBean;
import jp.bizreach.twitter.projectfw.web.action.TwitterBaseAction;
import org.dbflute.saflute.web.action.callback.ActionExecuteMeta;

/**
 * @author jflute
 */
public abstract class NisetterBaseAction extends TwitterBaseAction implements NisetterJspPath {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @Resource
    protected MemberLoginLogic memberLoginLogic;

    // -----------------------------------------------------
    //                                          Display Data
    //                                          ------------
    public NisetterUserWebBean userWebBean;

    // ===================================================================================
    //                                                                               Login
    //                                                                               =====
    /**
     * {@inheritDoc}
     */
    @Override
    protected NisetterUserBean getUserBean() {
        final NisetterUserBean userBean = sessionManager.getAttribute(NisetterUserBean.class);
        return userBean != null ? userBean : new NisetterUserBean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NisetterUserBean getUserBeanChecked() {
        return (NisetterUserBean) super.getUserBeanChecked();
    }

    /**
     * Logout the current session.
     */
    protected void logoutProc() {
        memberLoginLogic.logoutProc();
    }

    // ===================================================================================
    //                                                                            Callback
    //                                                                            ========
    // -----------------------------------------------------
    //                                      God Hand Finally
    //                                      ----------------
    // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = [TIPS by jflute]
    // どのリクエストが来ても、バリデーションエラーになっても例外発生しても最後に必ず実行される。
    // 例えば、画面の描画処理などで利用する。(中でJSPへのフォワードであることを判定できる)
    // godHandなんとか() は、Actionのスーパークラス用メソッド。(superは必ず呼ぶこと)
    // = = = = = = = = = =/
    @Override
    public void godHandFinally(ActionExecuteMeta executeMeta) {
        if (executeMeta.isForwardToJsp()) {
            if (userWebBean == null) { // basically true, however just in case
                final NisetterUserBean userBean = getUserBean();
                userWebBean = new NisetterUserWebBean();
                if (userBean.isLogin()) {
                    userWebBean.memberId = userBean.getMemberId();
                    userWebBean.memberName = userBean.getMemberName();
                }
                userWebBean.isLogin = userBean.isLogin();
            }
        }
        super.godHandFinally(executeMeta);
    }

    // ===================================================================================
    //                                                               Application Exception
    //                                                               =====================
    @Override
    protected String getErrorMessageJsp() {
        return path_Error_ErrorMessageJsp;
    }
}

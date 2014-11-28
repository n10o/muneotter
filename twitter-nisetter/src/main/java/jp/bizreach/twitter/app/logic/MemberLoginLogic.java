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
package jp.bizreach.twitter.app.logic;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;

import jp.bizreach.twitter.app.web.login.LoginIndexAction;
import jp.bizreach.twitter.dbflute.cbean.MemberCB;
import jp.bizreach.twitter.dbflute.exbhv.MemberBhv;
import jp.bizreach.twitter.dbflute.exbhv.MemberLoginBhv;
import jp.bizreach.twitter.dbflute.exentity.Member;
import jp.bizreach.twitter.dbflute.exentity.MemberLogin;
import jp.bizreach.twitter.domainfw.action.NisetterLoginRequired;
import jp.bizreach.twitter.domainfw.action.NisetterPerformLogin;
import jp.bizreach.twitter.domainfw.action.NisetterUserBean;
import jp.bizreach.twitter.domainfw.direction.NisetterConfig;
import jp.bizreach.twitter.projectfw.web.login.TwitterLoginBaseLogic;

/**
 * @author jflute
 */
public class MemberLoginLogic extends TwitterLoginBaseLogic<NisetterUserBean, Member> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected MemberBhv memberBhv;

    @Resource
    protected MemberLoginBhv memberLoginBhv;

    @Resource
    protected NisetterConfig nisetterConfig;

    // ===================================================================================
    //                                                                                Find
    //                                                                                ====
    @Override
    protected boolean doCheckUserLoginable(String email, String cipheredPassword) {
        MemberCB cb = new MemberCB();
        cb.query().arrangeLogin(email, cipheredPassword);
        return memberBhv.selectCount(cb) > 0;
    }

    @Override
    protected Member doFindLoginUser(String email, String cipheredPassword) {
        MemberCB cb = new MemberCB();
        cb.query().arrangeLogin(email, cipheredPassword);
        return memberBhv.selectEntity(cb);
    }

    @Override
    protected Member doFindLoginUser(Long userId) {
        MemberCB cb = new MemberCB();
        cb.query().arrangeLoginByIdentity(userId.intValue());
        return memberBhv.selectEntity(cb);
    }

    // ===================================================================================
    //                                                                               Login
    //                                                                               =====
    @Override
    protected Class<? extends Annotation> getLoginRequiredAnnotationType() {
        return NisetterLoginRequired.class;
    }

    @Override
    protected Class<? extends Annotation> getPerformLoginAnnotationType() {
        return NisetterPerformLogin.class;
    }

    @Override
    protected NisetterUserBean createUserBean(Member userEntity) {
        return new NisetterUserBean(userEntity);
    }

    @Override
    protected String getCookieAutoLoginKey() {
        return nisetterConfig.getCookieAutoLoginNisetterKey();
    }

    @Override
    protected void saveLoginHistory(Member member, NisetterUserBean userBean, boolean useAutoLogin) {
        MemberLogin login = new MemberLogin();
        login.setMemberId(member.getMemberId());
        login.setLoginMemberStatusCodeAsMemberStatus(member.getMemberStatusCodeAsMemberStatus());
        login.setLoginDatetime(timeManager.getCurrentTimestamp());
        login.setMobileLoginFlg_False(); // mobile unsupported for now
        memberLoginBhv.insert(login);
    }

    // ===================================================================================
    //                                                              Login Control Resource
    //                                                              ======================
    @Override
    protected Class<NisetterUserBean> getUserBeanType() {
        return NisetterUserBean.class;
    }


    @Override
    protected Class<?> getLoginActionType() {
        return LoginIndexAction.class;
    }

    // TODO nakamura
    public String encrypter(String password) {
        return encryptPassword(password);
    }

}

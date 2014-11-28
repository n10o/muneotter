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
package jp.bizreach.twitter.app.web.login;

import javax.annotation.Resource;

import jp.bizreach.twitter.app.web.IndexAction;
import jp.bizreach.twitter.app.web.base.NisetterBaseAction;
import jp.bizreach.twitter.dbflute.cbean.MemberCB;
import jp.bizreach.twitter.dbflute.exbhv.MemberBhv;
import jp.bizreach.twitter.dbflute.exbhv.MemberFollowingBhv;
import jp.bizreach.twitter.dbflute.exbhv.MemberSecurityBhv;
import jp.bizreach.twitter.dbflute.exentity.Member;
import jp.bizreach.twitter.dbflute.exentity.MemberFollowing;
import jp.bizreach.twitter.dbflute.exentity.MemberSecurity;
import jp.bizreach.twitter.domainfw.action.NisetterPerformLogin;
import jp.bizreach.twitter.domainfw.action.NisetterUserBean;

import org.dbflute.saflute.web.action.login.exception.LoginFailureException;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

/**
 * The action of member's Login.
 * @author jflute (modified by nakamura)
 */
public class LoginIndexAction extends NisetterBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @ActionForm
    @Resource
    public LoginForm loginForm;

    @Resource
    protected MemberBhv memberBhv;

    @Resource
    protected MemberSecurityBhv memberSecurityBhv;

    @Resource
    protected MemberFollowingBhv memberFollowingBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute(validator = false)
    public String index() {
        NisetterUserBean userBean = getUserBean();
        if (userBean.isLogin()) {
            return redirect(IndexAction.class);
        }
        return path_Login_LoginJsp;
    }

    // TODO nakamura nisetterperformloginとは何か
    @NisetterPerformLogin
    @Execute(validator = true, input = path_Login_LoginJsp)
    public String doLogin() {
        try {
            memberLoginLogic.login(loginForm.userName, loginForm.password, loginForm.isAutoLoginTrue());
        } catch (LoginFailureException e) {
            // TODO nakamura safluteの仕組みを理解すれば、try-catch無しでもっと簡単に書けそう
            throw new ActionMessagesException("ユーザ名とパスワードが微妙に異なります", false);
        }

        return redirect(IndexAction.class);
    }

    @Execute(validator = true, input = path_Login_LoginJsp)
    public String doSignup() {
        Member member = new Member();
        member.setMemberName(loginForm.signUpUserName);
        member.setMemberAccount(loginForm.signUpUserName);
        member.setMemberStatusCode_正式会員();
        memberBhv.insert(member);

        MemberSecurity memberSecurity = new MemberSecurity();
        memberSecurity.setMemberId(member.getMemberId());
        memberSecurity.setLoginPassword(memberLoginLogic.encrypter(loginForm.signUpPassword));
        // TODO nakamura DB整理したら消してもよい
        memberSecurity.setReminderAnswer("1");
        memberSecurity.setReminderQuestion("1");
        memberSecurity.setReminderUseCount(1);
        memberSecurityBhv.insert(memberSecurity);

        muneoFollow(member.getMemberId());

        return redirectWith(LoginIndexAction.class, moreUrl("welcome"));
    }

    @Execute(validator = false, urlPattern = "welcome")
    public String signUpComplete() {
        return path_Login_SignupCompleteJsp;
    }

    @Execute(validator = false)
    public String redirectTour() {
        return redirectWith(LoginIndexAction.class, moreUrl("tour"));
    }

    @Execute(validator = false, urlPattern = "tour")
    public String tour() {
        return path_Login_TourJsp;
    }

    protected void muneoFollow(Integer myid){
        MemberCB cb = new MemberCB();
        cb.query().setMemberName_Equal("Muneo");
        Member member = memberBhv.selectEntity(cb);

        MemberFollowing memnberFollowing = new MemberFollowing();
        memnberFollowing.setMyMemberId(myid);
        memnberFollowing.setYourMemberId(member.getMemberId());
        memnberFollowing.setFollowDatetime(timeManager.getCurrentTimestamp());
        memberFollowingBhv.insert(memnberFollowing);
    }

}
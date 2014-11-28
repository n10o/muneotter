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
package jp.bizreach.twitter.unit;

import jp.bizreach.twitter.dbflute.exbhv.MemberBhv;
import jp.bizreach.twitter.dbflute.exentity.Member;
import jp.bizreach.twitter.domainfw.action.NisetterUserBean;
import org.dbflute.saflute.web.servlet.session.SessionManager;
import org.seasar.dbflute.unit.seasar.ContainerTestCase;

/**
 * Use like this:
 * <pre>
 * YourTest extends {@link UnitNisetterContainerTestCase} {
 * 
 *     public void test_yourMethod() {
 *         <span style="color: #3F7E5E">// ## Arrange ##</span>
 *         YourAction action = new YourAction();
 *         <span style="color: #FD4747">inject</span>(action);
 * 
 *         <span style="color: #3F7E5E">// ## Act ##</span>
 *         action.submit();
 * 
 *         <span style="color: #3F7E5E">// ## Assert ##</span>
 *         assertTrue(action...);
 *     }
 * }
 * </pre>
 * @author jflute
 */
public abstract class UnitNisetterContainerTestCase extends ContainerTestCase {

    private MemberBhv memberBhv;
    private SessionManager sessionManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockLogin();
    }

    protected void mockLogin() {
        if (isSuppressMockLogin()) {
            return;
        }
        final Member member = memberBhv.selectByPKValue(1); // mock member
        final NisetterUserBean userBean = new NisetterUserBean(member);
        sessionManager.setAttribute(userBean);
    }

    protected boolean isSuppressMockLogin() { // e.g. LoginAction should override this
        return false; // login as default
    }
}

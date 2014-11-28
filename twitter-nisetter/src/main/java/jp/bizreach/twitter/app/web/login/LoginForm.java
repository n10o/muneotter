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

import org.seasar.struts.annotation.Mask;
import org.seasar.struts.annotation.Maxlength;
import org.seasar.struts.annotation.Minlength;
import org.seasar.struts.annotation.Msg;
import org.seasar.struts.annotation.Required;

import jp.bizreach.twitter.dbflute.allcommon.CDef;

/**
 * The form of member's Login.
 * @author jflute (modified by nakamura)
 */
public class LoginForm {
    @Required(target="doLogin")
    @Minlength(minlength = 4)
    @Maxlength(maxlength = 16)
    public String userName;
    @Required(target="doLogin")
    @Minlength(minlength = 4)
    @Maxlength(maxlength = 32)
    public String password;
    public String autoLogin;

    @Required(target="doSignup")
    @Minlength(minlength = 4)
    @Maxlength(maxlength = 16)
    @Mask(mask = "^[a-zA-Z0-9]*$", msg = @Msg(key ="errors.signup.illegalchar"))
    public String signUpUserName;
    @Required(target="doSignup")
    @Minlength(minlength = 4)
    @Maxlength(maxlength = 32)
    @Mask(mask = "^[a-zA-Z0-9]*$", msg = @Msg(key ="errors.signup.illegalchar"))
    public String signUpPassword;


    public boolean isAutoLoginTrue() {
        return CDef.Flg.True.code().equals(autoLogin);
    }

    public void clearSecurityInfo() {
        password = null;
    }
}

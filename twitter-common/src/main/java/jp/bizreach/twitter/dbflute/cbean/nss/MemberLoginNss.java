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
package jp.bizreach.twitter.dbflute.cbean.nss;

import org.seasar.dbflute.cbean.ConditionQuery;
import jp.bizreach.twitter.dbflute.cbean.cq.MemberLoginCQ;

/**
 * The nest select set-upper of member_login.
 * @author DBFlute(AutoGenerator)
 */
public class MemberLoginNss {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected MemberLoginCQ _query;
    public MemberLoginNss(MemberLoginCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                                     Nested Relation
    //                                                                     ===============
    /**
     * With nested relation columns to select clause. <br />
     * (会員)member by my MEMBER_ID, named 'member'.
     * @return The set-upper of more nested relation. {...with[nested-relation].with[more-nested-relation]} (NotNull)
     */
    public MemberNss withMember() {
        _query.doNss(new MemberLoginCQ.NssCall() { public ConditionQuery qf() { return _query.queryMember(); }});
        return new MemberNss(_query.queryMember());
    }
    /**
     * With nested relation columns to select clause. <br />
     * (会員ステータス)member_status by my LOGIN_MEMBER_STATUS_CODE, named 'memberStatus'.
     * @return The set-upper of more nested relation. {...with[nested-relation].with[more-nested-relation]} (NotNull)
     */
    public MemberStatusNss withMemberStatus() {
        _query.doNss(new MemberLoginCQ.NssCall() { public ConditionQuery qf() { return _query.queryMemberStatus(); }});
        return new MemberStatusNss(_query.queryMemberStatus());
    }

}
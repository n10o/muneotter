package jp.bizreach.twitter.app.web.member;

import org.seasar.struts.annotation.Maxlength;
import org.seasar.struts.annotation.Minlength;
import org.seasar.struts.annotation.Required;

/**
 * @author nakamura
 */
public class MemberIndexForm {
    public Integer pageNumber = 0;
    public String userName;

    @Required(target="addTweet")
    @Maxlength(maxlength = 140)
    public String tweetForm;

    @Required(target="addFollower, indexIncludePagenumber")
    @Maxlength(maxlength = 16)
    @Minlength(minlength = 4)
    public String followerName;

    public Boolean matchUserName;
    public Boolean exceedMaxTweetLength = false;

    public String[] stopFollowerIdList;
    @Required(target="addRecommend")
    public String[] recommendFollowerIdList;
}

package jp.bizreach.twitter.app.web.member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import jp.bizreach.twitter.app.web.base.NisetterBaseAction;
import jp.bizreach.twitter.app.web.login.LoginIndexAction;
import jp.bizreach.twitter.dbflute.cbean.MemberCB;
import jp.bizreach.twitter.dbflute.cbean.MemberFollowingCB;
import jp.bizreach.twitter.dbflute.cbean.TweetCB;
import jp.bizreach.twitter.dbflute.exbhv.MemberBhv;
import jp.bizreach.twitter.dbflute.exbhv.MemberFollowingBhv;
import jp.bizreach.twitter.dbflute.exbhv.TweetBhv;
import jp.bizreach.twitter.dbflute.exentity.Member;
import jp.bizreach.twitter.dbflute.exentity.MemberFollowing;
import jp.bizreach.twitter.dbflute.exentity.Tweet;
import jp.bizreach.twitter.domainfw.action.NisetterUserBean;
import jp.bizreach.twitter.projectfw.web.paging.PagingNavi;

import org.dbflute.saflute.web.action.callback.ActionExecuteMeta;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.OrQuery;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

/**
 * @author nakamura
 */
public class MemberIndexAction extends NisetterBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @ActionForm
    @Resource
    protected MemberIndexForm memberIndexForm;
    @Resource
    protected MemberBhv memberBhv;
    @Resource
    protected TweetBhv tweetBhv;
    @Resource
    protected MemberFollowingBhv memberFollowingBhv;

    // -----------------------------------------------------
    //                                          Display Data
    //                                          ------------
    public NisetterUserBean userBean;
    public List<TweetBean> tweetBeanList;
    public List<MemberFollowingBean> memberFollowingBeanList;
    public List<MemberFollowingRecommendBean> memberFollowingRecommendBeanList;
    public PagingNavi pagingNavi = createPagingNavi();

    final public String restrictWord = "<script>";
    final public int maxFollowerNum = 10;
    final public int recommendMemberNum = 5;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute(validator = false, urlPattern = "{userName}")
    public String index() {
        return pageSelector();
    }

    @Execute(validator = false, urlPattern = "{userName}/{pageNumber}")
    public String indexIncludePagenumber() {
        return pageSelector();
    }

    // TODO nakamura validatorに引っかかった場合に、ユーザページに飛ばず、/member/に飛ばされてしまう
    @Execute(validator = true, input = path_Member_MemberJsp)
    public String addTweet() {
        userBean = getUserBean();
        if (!userBean.isLogin()) {
            return redirect(LoginIndexAction.class);
        }

        if (memberIndexForm.tweetForm != null) {
            Tweet tweet = new Tweet();

            // TODO nakamura 手抜き実装
            if (memberIndexForm.tweetForm.matches(".*" + restrictWord + ".*")) {
                throw new ActionMessagesException("scriptは勘弁して頂けないでしょうか", false);
            }

            tweet.setTweetContents(memberIndexForm.tweetForm);
            tweet.setMemberId(userBean.getMemberId().intValue());
            tweet.setDatetime(timeManager.getCurrentTimestamp());
            tweetBhv.insert(tweet);
        }

        return redirectWith(MemberIndexAction.class, moreUrl(userBean.getMemberName()));
    }

    @Execute(validator = true, input = path_Member_MemberJsp)
    public String addFollower() {
        validateMaxFollower();
        userBean = getUserBean();
        if (!userBean.isLogin()) {
            return redirect(LoginIndexAction.class);
        }

        if (memberIndexForm.followerName.equals(userBean.getMemberName())) {
            // TODO nakamura　パラメタ外出しにしたい
            throw new ActionMessagesException("自分で自分をフォローすることは論理に反します", false);
        }

        MemberCB cb = new MemberCB();
        cb.query().setMemberName_Equal(memberIndexForm.followerName);
        Member member = memberBhv.selectEntity(cb);
        if (member == null) {
            // TODO nakamura　パラメタ外出し
            throw new ActionMessagesException("指定されたユーザ名は存在しません", false);
        }

        if (isDuplicateFollower(member)) {
            // TODO nakamura パラメタ外出し
            throw new ActionMessagesException("既にそのユーザはフォロー済みです", false);
        }
        MemberFollowing memberFollowing = new MemberFollowing();
        memberFollowing.setMyMemberId(userBean.getMemberId().intValue());
        memberFollowing.setYourMemberId(member.getMemberId());
        memberFollowing.setFollowDatetime(timeManager.getCurrentTimestamp());

        memberFollowingBhv.insert(memberFollowing);

        return redirectWith(MemberIndexAction.class, moreUrl(userBean.getMemberName()));
    }

    @Execute(validator = true, input = path_Member_MemberJsp)
    public String stopFollow() {
        userBean = getUserBean();
        if (!userBean.isLogin()) {
            return redirect(LoginIndexAction.class);
        }
        if (memberIndexForm.stopFollowerIdList == null) {
            // TODO nakamura パラメタ外出し
            throw new ActionMessagesException("フォローを解除するためには、ユーザを選択してください", false);
        }

        for (String id : memberIndexForm.stopFollowerIdList) {
            MemberFollowingCB cb = new MemberFollowingCB();
            cb.query().setMyMemberId_Equal(userBean.getUserId().intValue());

            cb.query().setYourMemberId_Equal(Integer.parseInt(id));


            //一回しかまわらない
            List<MemberFollowing> deleteList = memberFollowingBhv.selectList(cb);
            MemberFollowing memberFollowing = new MemberFollowing();
            for (MemberFollowing member : deleteList) {
                memberFollowing.setMemberFollowingId(member.getMemberFollowingId());
                memberFollowingBhv.delete(memberFollowing);
            }
        }

        return redirectWith(MemberIndexAction.class, moreUrl(userBean.getMemberName()));
    }

    @Execute(validator = false)
    public String autoTweet() {
        userBean = getUserBean();
        if (!userBean.isLogin()) {
            return redirect(LoginIndexAction.class);
        }
        String[] muneoWord = { "北海道に骨を埋めるこの" + userBean.getMemberName() + "が、北海道に利益を誘導して何が悪いんですか", "権力の横暴と言わざるをえない",
                "地方への利益誘導ではない、“公正配分”である！",
                "応援団長の松山千春さんは今日はこの" + userBean.getMemberName() + "のために愛媛、香川に飛んでくれてます。 ありがたいです。",
                "皆さん、選挙に行きましょう！政治に関心を持ってください！" + userBean.getMemberName() + "からのお願いです！" };

        List<String> shuffledWord = Arrays.asList(muneoWord);
        Collections.shuffle(shuffledWord);

        Tweet tweet = new Tweet();
        tweet.setTweetContents(shuffledWord.get(0));
        tweet.setMemberId(userBean.getUserId().intValue());
        tweet.setDatetime(timeManager.getCurrentTimestamp());
        tweetBhv.insert(tweet);

        return redirectWith(MemberIndexAction.class, moreUrl(userBean.getMemberName()));
    }

    @Execute(validator = true, input = path_Member_MemberJsp)
    public String autoFollow() {
        validateMaxFollower(); // Follower数が上限値になっていないかチェック

        userBean = getUserBean();
        if (!userBean.isLogin()) {
            return redirect(LoginIndexAction.class);
        }

        MemberFollowing memberFollowing = new MemberFollowing();
        memberFollowing.setMyMemberId(userBean.getMemberId().intValue());
        memberFollowing.setYourMemberId(getUnknownFollower());
        memberFollowing.setFollowDatetime(timeManager.getCurrentTimestamp());
        memberFollowingBhv.insert(memberFollowing);

        // TODO nakamura フォロワーが見つからなかった場合のエラー処理
        return redirectWith(MemberIndexAction.class, moreUrl(userBean.getMemberName()));
    }

    @Execute(validator = true, input = path_Member_MemberJsp)
    public String addRecommend() {
        userBean = getUserBean();
        if (!userBean.isLogin()) {
            return redirect(LoginIndexAction.class);
        }
        for (String followerId : memberIndexForm.recommendFollowerIdList) {
            //途中でExceptionがthrowされた場合、insertは最初からなかったことになる模様
            validateMaxFollower();
            MemberFollowing memberFollowing = new MemberFollowing();
            memberFollowing.setMyMemberId(userBean.getMemberId().intValue());
            memberFollowing.setYourMemberId(Integer.parseInt(followerId));
            memberFollowing.setFollowDatetime(timeManager.getCurrentTimestamp());
            memberFollowingBhv.insert(memberFollowing);
        }

        return redirectWith(MemberIndexAction.class, moreUrl(userBean.getMemberName()));
    }

    @Execute(validator = false)
    public String logout() {
        logoutProc();

        return redirect(LoginIndexAction.class);
    }

    @Execute(validator = false)
    public String goToppage() {

        return redirect(LoginIndexAction.class);
    }

    // ===================================================================================
    //                                                                            Callback
    //                                                                            ========
    @Override
    public void callbackFinally(ActionExecuteMeta executeMeta) {
        if (executeMeta.isForwardToJsp()) {
            prepareMemberPage(); // メンバーページの構築（ツイート表示とフォロワー表示）
        }
    }

    // ===================================================================================
    //                                                                           Logic
    //                                                                           =========
    protected String pageSelector() {
        userBean = getUserBean();
        String userName = memberIndexForm.userName;
        if (getMemberID(userName) == null) {
            if (userName == null) {
                if (userBean.isLogin()) {
                    return redirectWith(MemberIndexAction.class, moreUrl(userBean.getMemberName()));
                } else {
                    return redirect(LoginIndexAction.class);
                }
            }
            return path_Member_MemberNotFoundJsp;
        }
        return path_Member_MemberJsp;
    }

    protected Boolean isDuplicateFollower(Member member) {
        MemberFollowingCB cb = new MemberFollowingCB();
        cb.query().setMyMemberId_Equal(getMemberID(userBean.getMemberName()));
        cb.query().setYourMemberId_Equal(member.getMemberId());
        ListResultBean<MemberFollowing> followringList = memberFollowingBhv.selectList(cb);

        if (followringList.isEmpty()) {
            return false;
        }
        return true;
    }

    protected PagingResultBean<Tweet> selectTweetPage() {
        userBean = getUserBean();
        TweetCB cb = new TweetCB();
        cb.setupSelect_Member();
        cb.query().addOrderBy_Datetime_Desc();

        cb.orScopeQuery(new OrQuery<TweetCB>() {
            @Override
            public void query(TweetCB orCB) {
                // ログインしている状況かつ自分のツイートページを表示している場合には、フォロアーのツイートを追加で表示
                if ((userBean.getMemberName() != null) && (userBean.getMemberName().equals(memberIndexForm.userName))) {
                    orCB.query().queryMember()
                            .existsMemberFollowingByYourMemberIdList(new SubQuery<MemberFollowingCB>() {
                                @Override
                                public void query(MemberFollowingCB subCB) {
                                    subCB.query().setMyMemberId_Equal(userBean.getUserId().intValue());
                                }
                            });
                }
                orCB.query().setMemberId_Equal(getMemberID(memberIndexForm.userName));
            }
        });

        int pageSize = getPagingPageSize();
        cb.paging(pageSize, memberIndexForm.pageNumber);

        return tweetBhv.selectPage(cb);
    }

    protected List<MemberFollowing> getFollowerId() {
        userBean = getUserBean();
        MemberFollowingCB cb = new MemberFollowingCB();
        cb.query().setMyMemberId_Equal(userBean.getMemberId().intValue());
        cb.query().addOrderBy_FollowDatetime_Desc();

        return memberFollowingBhv.selectList(cb);
    }

    protected List<MemberFollowing> getFollowingList(String userName) {
        MemberFollowingCB cb = new MemberFollowingCB();
        cb.setupSelect_MemberByYourMemberId();
        cb.query().addOrderBy_YourMemberId_Asc();
        cb.query().setMyMemberId_Equal(getMemberID(userName));

        return memberFollowingBhv.selectList(cb);
    }

    protected List<Member> getRecommendFollower() {
        int i;
        ArrayList<Integer> followerId = new ArrayList<Integer>();
        if (userBean.getMemberId() != null) {
            for (i = 0; i < recommendMemberNum; i++) {
                // TODO nakamura バグ：同じ番号が帰ってくる場合がある->放置
                followerId.add(getUnknownFollower());
            }
        }

        MemberCB cb = new MemberCB();
        cb.query().setMemberId_InScope(followerId);

        return memberBhv.selectList(cb);
    }

    protected Integer getMemberID(String userName) {
        // TODO nakamura 将来的にユーザ名とメンバーどちらがnullか判別するために、戻り値を変更したい
        if (userName == null) {
            return null;
        }

        MemberCB cb = new MemberCB();
        cb.query().setMemberName_Equal(userName);
        Member member = memberBhv.selectEntity(cb);

        if (member == null) {
            return null;
        }

        return member.getMemberId();
    }

    // TODO nakamura 実装がひどいので直したい
    protected Integer getUnknownFollower() {
        userBean = getUserBean();
        MemberCB cb = new MemberCB();
        int myMemberId = userBean.getMemberId().intValue();

        cb.query().addOrderBy_MemberId_Desc();
        List<Member> memberList = memberBhv.selectList(cb);

        Random random = new Random();
        Integer yourMemberId;


        while (true) {
            // メンバーIDの一番古い番号を取得
            yourMemberId = random.nextInt(memberList.get(0).getMemberId()) + 1;

            // TODO nakamura 簡略化可能＋ループ内でcb回すのは避ける
            // TODO nakamura 登録できるユーザがいない場合は考慮していない（フォロー１０人制限で救っている）
            // 取得したIDが自分のもの、既にフォロー済みのもの、存在しないIDだった場合は除外
            if (yourMemberId != myMemberId) {
                cb.query().setMemberId_Equal(yourMemberId);
                if (!memberBhv.selectList(cb).isEmpty()) {
                    MemberFollowingCB fcb = new MemberFollowingCB();
                    fcb.query().setMyMemberId_Equal(myMemberId);
                    fcb.query().setYourMemberId_Equal(yourMemberId);
                    if (memberFollowingBhv.selectList(fcb).isEmpty()) {
                        break;
                    }
                }
            }
        }

        return yourMemberId;
    }

    protected void validateMaxFollower() {
        userBean = getUserBean();

        MemberFollowingCB cb = new MemberFollowingCB();
        cb.query().setMyMemberId_Equal(userBean.getUserId().intValue());
        if (maxFollowerNum <= memberFollowingBhv.selectCount(cb)) {
            throw new ActionMessagesException("フォロワー数が最大数（10）を超えています。これ以上伸ばすとデザインが崩れますのでご容赦いただけないでしょうか", false);
        }
    }

    protected void prepareMemberPage() {
        userBean = getUserBean();
        if (memberIndexForm.userName == null) {
            if (userBean.getMemberName() == null) {
                //TODO nakamura ログインページにリダイレクトしたい
                return;
            }
            memberIndexForm.userName = userBean.getMemberName();
        }

        String userName = memberIndexForm.userName;

        PagingResultBean<Tweet> tweetPage = selectTweetPage();
        tweetBeanList = new ArrayList<TweetBean>();
        for (Tweet tweet : tweetPage) {
            TweetBean tweetBean = new TweetBean();
            tweetBean.tweetId = tweet.getTweetId();
            tweetBean.memberName = tweet.getMember().getMemberName();
            tweetBean.tweetContents = tweet.getTweetContents();
            System.out.println(tweetBean.tweetContents);
            tweetBean.tweetShortDate = tweetBean.getShortDate(tweet.getDatetime());
            tweetBeanList.add(tweetBean);
        }

        preparePagingNavi(pagingNavi, tweetPage);

        memberFollowingBeanList = new ArrayList<MemberFollowingBean>();
        for (MemberFollowing memberFollowing : getFollowingList(userName)) {
            MemberFollowingBean memberFollowingBean = new MemberFollowingBean();
            memberFollowingBean.followerId = memberFollowing.getYourMemberId();
            memberFollowingBean.followerName = memberFollowing.getMemberByYourMemberId().getMemberName();
            memberFollowingBeanList.add(memberFollowingBean);
        }

        memberFollowingRecommendBeanList = new ArrayList<MemberFollowingRecommendBean>();
        for (Member member : getRecommendFollower()) {
            MemberFollowingRecommendBean memberFollowingRecommendBean = new MemberFollowingRecommendBean();
            memberFollowingRecommendBean.followerId = member.getMemberId();
            memberFollowingRecommendBean.followerName = member.getMemberName();
            memberFollowingRecommendBeanList.add(memberFollowingRecommendBean);
        }
    }
}

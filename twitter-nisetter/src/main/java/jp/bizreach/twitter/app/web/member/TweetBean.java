package jp.bizreach.twitter.app.web.member;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author nakamura
 */
public class TweetBean {
    public Integer tweetId;
    public String tweetContents;

    public String memberName;
    public String tweetShortDate;

    public String getShortDate(Timestamp datetime){
        return new SimpleDateFormat("MM/dd HH:mm").format(datetime);
    }
}

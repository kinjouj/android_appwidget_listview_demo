package net.kinjouj.test.android_appwidget_listview_demo;

import java.io.Serializable;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.res.StringRes;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@EBean
public class TwitterApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @StringRes
    protected String consumerKey;

    @StringRes
    protected String consumerSecret;

    @StringRes
    protected String accessToken;

    @StringRes
    protected String accessTokenSecret;

    private Twitter twitter;
  
    public Twitter getTwitter() {
        if (twitter == null) {
            twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(consumerKey, consumerSecret);
            twitter.setOAuthAccessToken(getAccessToken());
        }

        return twitter;
    }

    private AccessToken getAccessToken() {
        return new AccessToken(accessToken, accessTokenSecret);
    }
}
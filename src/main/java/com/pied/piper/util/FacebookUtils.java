package com.pied.piper.util;

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by akshay.kesarwan on 28/07/16.
 */
@Slf4j
public class FacebookUtils {

    private static final String FB_FRIENDS_URL = "me/friends";

    public static List<User> findFriends(FacebookClient facebookClient, String accountId) {

        Connection<User> myFriends = facebookClient.fetchConnection(FB_FRIENDS_URL, User.class);
        log.info("Found " + myFriends.getData().size() + " facebook friends of id " + accountId);
        return myFriends.getData();

    }

    public static User getUserDetails(FacebookClient facebookClient) {
        User user = facebookClient.fetchObject("me", User.class);
        return user;
    }

    public static String getFullProfileImageUrl(FacebookClient facebookClient) {
        JsonObject jsonObject = facebookClient.fetchObject("me/picture", JsonObject.class, Parameter.with("type", "large"),Parameter.with("redirect","false"));
        log.info("Output " + jsonObject);
        JsonObject imageMap = (JsonObject) jsonObject.get("data");
        return imageMap.getString("url");
    }
}

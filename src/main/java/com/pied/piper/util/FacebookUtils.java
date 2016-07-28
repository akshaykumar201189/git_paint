package com.pied.piper.util;

import com.restfb.Connection;
import com.restfb.FacebookClient;
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
        log.info("Found " + myFriends.getData().size() + " facebook friends of accountId " + accountId);
        return myFriends.getData();

    }
}

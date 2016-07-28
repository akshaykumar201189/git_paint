package com.pied.piper;

import com.restfb.*;
import com.restfb.types.FacebookType;
import com.restfb.types.User;
import org.junit.Test;

import java.util.List;

/**
 * Created by akshay.kesarwan on 27/07/16.
 */
public class MyTest {
    @Test
    public void test() {
        String accessToken = "EAACEdEose0cBAA2zfkcES6Wf2knsOtgDzRM3A7IGU5ZBAwGurtBuY4KAZB1lbd0ZCHzm7qEebhqQPUHd99OTThGhEjv26Tg8oRZAcadbn61UZBmZCx0IBuui7t930ow3xQxZAVNYnuirenVPiC4C6WZAfAlOjA5ALohmobfTjKMF1AZDZD";
        FacebookClient client = new DefaultFacebookClient(accessToken, Version.VERSION_2_0);
        //String messageId = publishMessage(client);
        //System.out.println("Hello " + messageId);
        fetchFriends(client);
    }
    String publishMessage(FacebookClient facebookClient) {
        System.out.println("* Feed publishing *");

        FacebookType publishMessageResponse =
                facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", "Testing FB Comments Via API"));

        System.out.println("Published message ID: " + publishMessageResponse.getId());
        return publishMessageResponse.getId();
    }

    void fetchFriends(FacebookClient facebookClient) {
        Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class, Parameter.with("limit",2));
        System.out.println(myFriends.getTotalCount());
        System.out.println(myFriends.getData().size() +  String .valueOf(myFriends.hasNext()));

        for(List<User> users : myFriends) {
            for(User user : users) {
                System.out.println(user.getId());
            }
        }

        myFriends.getAfterCursor();
/*
        Iterator<List<User>> iterator = myFriends.iterator();
        while (iterator.hasNext()) {
            List<User> users = iterator.next();
            for(User user : users) {
                System.out.println("User: " + user.getName() + " " + user.getId() + " " + user.getPicture() + " " + user.getEmail());
            }
        }
        */
    }
}

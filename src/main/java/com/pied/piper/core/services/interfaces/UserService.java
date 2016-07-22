package com.pied.piper.core.services.interfaces;

import com.google.inject.ImplementedBy;

import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.db.model.User;
import com.pied.piper.core.dto.ImageMetaData;
import com.pied.piper.core.dto.SearchUserRequestDto;
import com.pied.piper.core.dto.UserDetails;
import com.pied.piper.core.services.impl.UserServiceImpl;

import java.util.List;

/**
 * Created by palash.v on 21/07/16.
 */
@ImplementedBy(UserServiceImpl.class)
public interface UserService {

    List<User> searchUser(SearchUserRequestDto searchUserRequestDto);

    User findByAccountId(String accountId);

    UserDetails getUserDetailsByUserId(Long userId);

    User findById(Long userId);

    List<User> getFollowers(String accountId);

    List<User> createFollower(String user, String follower);

    List<List<ImageMetaData>> getFollowerImages(String userId);

}

package com.pied.piper.core.services.interfaces;

import com.pied.piper.core.db.model.Image;
import com.pied.piper.core.dto.*;

import java.util.List;

/**
 * Created by akshay.kesarwan on 21/05/16.
 */
public interface GalleriaService {
    Long saveImage(SaveImageRequestDto saveImageRequestDto) throws Exception;
    String getImageData(Long imageId);
    ImageMetaData getImageMetaData(Long imageId);
    List<ImageMetaData> getImageMetaData(String accountId);
    List<Image> findByAccountId(String accountId);
    List<Image> getImagesForAccountId(String accountId);
    Image getImage(Long imageId);
    SearchResponseDto search(String searchText);
    ProfileDetails getProfileDetails(String accountId, String ownerAccountId) throws Exception;
    Long cloneImage(Long imageId, String accountId) throws Exception;
    void sendPullRequest(Long imageId,String accountId) throws Exception;
    void approvePullRequest(Long prId, SaveImageRequestDto saveImageRequestDto) throws Exception;
    void rejectPullRequest(Long imageId,String accountId) throws Exception;
    PullRequest getPullRequest(Long prId);
}

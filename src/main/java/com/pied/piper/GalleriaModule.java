package com.pied.piper;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.pied.piper.core.config.AwsCredentials;
import com.pied.piper.core.services.impl.*;
import com.pied.piper.core.services.interfaces.*;
import com.pied.piper.util.AWSUtils;


/**
 * Created by akshay.kesarwan on 21/05/16.
 */
public class GalleriaModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GalleriaService.class).to(GalleriaServiceImpl.class).in(Singleton.class);
        bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
        bind(ImageLikesService.class).to(ImageLikesServiceImpl.class).in(Singleton.class);
        bind(SessionService.class).to(SessionServiceImpl.class).in(Singleton.class);
        bind(CommentService.class).to(CommentServiceImpl.class).in(Singleton.class);
        bind(AWSUtils.class);
    }

    @Provides
    @Singleton
    private AwsCredentials providesAwsConfig(Provider<GalleriaConfiguration> galleriaConfigurationProvider) {
        return galleriaConfigurationProvider.get().getAwsCredentials();
    }
}

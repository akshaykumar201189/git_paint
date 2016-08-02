package com.pied.piper.core.services.interfaces;

import com.google.inject.ImplementedBy;
import com.pied.piper.core.services.impl.SessionServiceImpl;
import com.pied.piper.exception.ResponseException;

/**
 * Created by akshay.kesarwan on 01/08/16.
 */
@ImplementedBy(SessionServiceImpl.class)
public interface SessionService {
    String createSessionForAccount(String accountId) throws ResponseException;
    String validateAndGetAccountForSession(String sessionId) throws ResponseException;
}

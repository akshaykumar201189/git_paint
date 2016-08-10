package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.pied.piper.core.services.interfaces.SessionService;
import com.pied.piper.exception.ResponseException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by akshay.kesarwan on 01/08/16.
 */
// TODO : Add Redis/DB based session implementation here rather than local hashmap
public class SessionServiceImpl implements SessionService {

    private static Map<String, String > accountToSessionMap = new HashMap<>();
    private static Map<String, String > sessionToAccountMap = new HashMap<>();

    @Inject
    public SessionServiceImpl() {

    }

    @Override
    public String createSessionForAccount(String accountId) throws ResponseException {
        String sessionId = UUID.randomUUID().toString();
        accountToSessionMap.put(accountId, sessionId);
        sessionToAccountMap.put(sessionId, accountId);
        return sessionId;
    }

    @Override
    public String validateAndGetAccountForSession(String sessionId) throws ResponseException {
        if(!sessionToAccountMap.containsKey(sessionId))
            throw new ResponseException("Session Id not found");
        return sessionToAccountMap.get(sessionId);
    }
}

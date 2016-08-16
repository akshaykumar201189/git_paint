package com.pied.piper.core.services.impl;

import com.google.inject.Inject;
import com.pied.piper.core.db.dao.impl.SessionDaoImpl;
import com.pied.piper.core.db.model.Session;
import com.pied.piper.core.services.interfaces.SessionService;
import com.pied.piper.exception.ResponseException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created by akshay.kesarwan on 01/08/16.
 */
// TODO : Add Redis/DB based session implementation here rather than local hashmap
public class SessionServiceImpl implements SessionService {

    private final SessionDaoImpl sessionDao;

    @Inject
    public SessionServiceImpl(SessionDaoImpl sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public String createSessionForAccount(String accountId) throws ResponseException {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session();
        session.setAccountId(accountId);
        session.setSessionId(sessionId);
        session.setExpirationTime(DateTime.now().plusMonths(6));
        sessionDao.save(session);
        return sessionId;
    }

    @Override
    public String validateAndGetAccountForSession(String sessionId) throws ResponseException {
        Criterion criterion = Restrictions.eq("sessionId", sessionId);
        List<Session> sessions = sessionDao.findByCriteria(criterion);
        if(sessions!=null && sessions.size()>0) {
            for(Session session : sessions) {
                if(session.getExpirationTime().isAfterNow())
                    return session.getAccountId();
            }
        }
        throw new ResponseException("Session Id not found");
    }
}

package lab.idioglossia.row.repository;

import lab.idioglossia.row.domain.RowWebsocketSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface RowSessionRegistry {
    void addSession(RowWebsocketSession rowWebsocketSession);
    RowWebsocketSession getSession(String userId);
    RowWebsocketSession getSession(String userId, String sessionId);
    Collection<RowWebsocketSession> getSessions(String userId);
    void removeSession(String userId, @Nullable String sessionId);
    Collection<RowWebsocketSession> getAll();

    @Log4j2
    class DefaultRowSessionRegistry implements RowSessionRegistry {
        private final Map<String, RowWebsocketSession> sessionMap = new ConcurrentHashMap<>();

        @Override
        public void addSession(RowWebsocketSession rowWebsocketSession) {
            RowWebsocketSession existingWebsocketSession = sessionMap.putIfAbsent(rowWebsocketSession.getUserId(), rowWebsocketSession);
            if(existingWebsocketSession != null){
                try {
                    existingWebsocketSession.getSession().close();
                    sessionMap.replace(rowWebsocketSession.getUserId(), rowWebsocketSession);
                } catch (IOException e) {
                    log.warn("Closing previous session because userId is duplicate", e);
                }
            }
        }

        @Override
        public RowWebsocketSession getSession(String userId) {
            return sessionMap.get(userId);
        }

        @Override
        public RowWebsocketSession getSession(String userId, String sessionId) {
            for (RowWebsocketSession session : getSessions(userId)) {
                if(session.getSession().getId().equals(sessionId))
                    return session;
            }
            return null;
        }

        @Override
        public Collection<RowWebsocketSession> getSessions(String userId) {
            return Collections.singleton(getSession(userId));
        }

        @Override
        public void removeSession(String userId, @Nullable String sessionId) {
            sessionMap.remove(userId);
        }

        @Override
        public Collection<RowWebsocketSession> getAll() {
            return sessionMap.values();
        }

    }

    @Log4j2
    class MultiUserSessionRegistry implements RowSessionRegistry {
        private final Map<String, List<RowWebsocketSession>> sessionMap = new ConcurrentHashMap<>();

        @Override
        public void addSession(RowWebsocketSession rowWebsocketSession) {
            sessionMap.computeIfAbsent(rowWebsocketSession.getUserId(), s -> {
                return new CopyOnWriteArrayList<>();
            }).add(rowWebsocketSession);
        }

        @Override
        public RowWebsocketSession getSession(String userId) {
            List<RowWebsocketSession> rowWebsocketSessions = sessionMap.get(userId);
            if(rowWebsocketSessions != null && rowWebsocketSessions.size() > 0)
                return rowWebsocketSessions.get(0);
            return null;
        }

        @Override
        public RowWebsocketSession getSession(String userId, String sessionId) {
            for (RowWebsocketSession session : getSessions(userId)) {
                if(session.getSession().getId().equals(sessionId))
                    return session;
            }

            return null;
        }

        @Override
        public Collection<RowWebsocketSession> getSessions(String userId) {
            List<RowWebsocketSession> rowWebsocketSessions = sessionMap.get(userId);
            if(rowWebsocketSessions != null)
                return rowWebsocketSessions;
            return new ArrayList<>();
        }

        @Override
        public void removeSession(String userId, @Nullable String sessionId) {
            List<RowWebsocketSession> rowWebsocketSessions = sessionMap.get(userId);
            if(rowWebsocketSessions != null){
                for (RowWebsocketSession rowWebsocketSession : rowWebsocketSessions) {
                    if(rowWebsocketSession.getSession().getId().equals(sessionId)){
                        rowWebsocketSessions.remove(rowWebsocketSession);
                        return;
                    }
                }
            }
        }

        @Override
        public Collection<RowWebsocketSession> getAll() {
            List<RowWebsocketSession> rowWebsocketSessions = new ArrayList<>();
            sessionMap.values().forEach(rowWebsocketSessions::addAll);
            return rowWebsocketSessions;
        }
    }
}

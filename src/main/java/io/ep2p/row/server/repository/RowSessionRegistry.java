package io.ep2p.row.server.repository;

import io.ep2p.row.server.ws.RowServerWebsocket;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface RowSessionRegistry {
    void addSession(RowServerWebsocket<?> rowWebsocketSession);
    RowServerWebsocket<?> getSession(String userId);
    RowServerWebsocket<?> getSession(String userId, String sessionId);
    Collection<RowServerWebsocket<?>> getSessions(String userId);
    void removeSession(String userId, @Nullable String sessionId);
    Collection<RowServerWebsocket<?>> getAll();

    @Log4j2
    class DefaultRowSessionRegistry implements RowSessionRegistry {
        private final Map<String, RowServerWebsocket<?>> sessionMap = new ConcurrentHashMap<>();

        @Override
        public void addSession(RowServerWebsocket<?> rowWebsocketSession) {
            RowServerWebsocket<?> existingWebsocketSession = sessionMap.putIfAbsent(rowWebsocketSession.getUserId(), rowWebsocketSession);
            if(existingWebsocketSession != null){
                try {
                    existingWebsocketSession.close(CloseStatus.NORMAL);
                    sessionMap.replace(rowWebsocketSession.getUserId(), rowWebsocketSession);
                } catch (IOException e) {
                    log.warn("Closing previous session because userId is duplicate", e);
                }
            }
        }

        @Override
        public RowServerWebsocket<?> getSession(String userId) {
            return sessionMap.get(userId);
        }

        @Override
        public RowServerWebsocket<?> getSession(String userId, String sessionId) {
            for (RowServerWebsocket<?> session : getSessions(userId)) {
                if(session.getId().equals(sessionId))
                    return session;
            }
            return null;
        }

        @Override
        public Collection<RowServerWebsocket<?>> getSessions(String userId) {
            return Collections.singleton(getSession(userId));
        }

        @Override
        public void removeSession(String userId, @Nullable String sessionId) {
            sessionMap.remove(userId);
        }

        @Override
        public Collection<RowServerWebsocket<?>> getAll() {
            return sessionMap.values();
        }

    }

    @Log4j2
    class MultiUserSessionRegistry implements RowSessionRegistry {
        private final Map<String, List<RowServerWebsocket<?>>> sessionMap = new ConcurrentHashMap<>();

        @Override
        public void addSession(RowServerWebsocket<?> rowWebsocketSession) {
            sessionMap.computeIfAbsent(rowWebsocketSession.getUserId(), s -> {
                return new CopyOnWriteArrayList<>();
            }).add(rowWebsocketSession);
        }

        @Override
        public RowServerWebsocket<?> getSession(String userId) {
            List<RowServerWebsocket<?>> rowWebsocketSessions = sessionMap.get(userId);
            if(rowWebsocketSessions != null && rowWebsocketSessions.size() > 0)
                return rowWebsocketSessions.get(0);
            return null;
        }

        @Override
        public RowServerWebsocket<?> getSession(String userId, String sessionId) {
            for (RowServerWebsocket<?> session : getSessions(userId)) {
                if(session.getId().equals(sessionId))
                    return session;
            }

            return null;
        }

        @Override
        public Collection<RowServerWebsocket<?>> getSessions(String userId) {
            List<RowServerWebsocket<?>> rowWebsocketSessions = sessionMap.get(userId);
            if(rowWebsocketSessions != null)
                return rowWebsocketSessions;
            return new ArrayList<>();
        }

        @Override
        public void removeSession(String userId, @Nullable String sessionId) {
            List<RowServerWebsocket<?>> rowWebsocketSessions = sessionMap.get(userId);
            if(rowWebsocketSessions != null){
                for (RowServerWebsocket<?> rowWebsocketSession : rowWebsocketSessions) {
                    if(rowWebsocketSession.getId().equals(sessionId)){
                        rowWebsocketSessions.remove(rowWebsocketSession);
                        return;
                    }
                }
            }
        }

        @Override
        public Collection<RowServerWebsocket<?>> getAll() {
            List<RowServerWebsocket<?>> rowWebsocketSessions = new ArrayList<>();
            sessionMap.values().forEach(rowWebsocketSessions::addAll);
            return rowWebsocketSessions;
        }
    }
}

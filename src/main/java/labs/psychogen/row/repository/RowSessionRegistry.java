package labs.psychogen.row.repository;

import labs.psychogen.row.domain.RowWebsocketSession;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface RowSessionRegistry {
    void addSession(RowWebsocketSession rowWebsocketSession);
    RowWebsocketSession getSession(String userId);
    void removeSession(String userId);
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
        public void removeSession(String userId) {
            sessionMap.remove(userId);
        }

        @Override
        public Collection<RowWebsocketSession> getAll() {
            return sessionMap.values();
        }

    }
}

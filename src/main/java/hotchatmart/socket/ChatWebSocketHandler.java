package hotchatmart.socket;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(final Session user) throws Exception {
        final String username = "User" + Chat.nextUserNumber++;
        Chat.userUsernameMap.put(user, username);
        Chat.broadcastMessage(sender = "Server", msg = (username + " Conectado"));
    }

    @OnWebSocketClose
    public void onClose(final Session user, final int statusCode, final String reason) {
        final String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " Saiu"));
    }

    @OnWebSocketMessage
    public void onMessage(final Session user, final String message) {
        Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message);
    }

}

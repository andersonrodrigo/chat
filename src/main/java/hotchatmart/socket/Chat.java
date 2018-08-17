package hotchatmart.socket;
import static j2html.TagCreator.article;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static spark.Spark.after;
import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import hotchatmart.controller.UsuarioController;
import hotchatmart.service.UsuarioService;
import spark.Filter;
public class Chat {

    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user

    public static void main(final String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        port(8093);
        staticFiles.expireTime(60000);
        webSocket("/api/chat", ChatWebSocketHandler.class);
        cors();
        new UsuarioController(new UsuarioService());
        init();
    }

    /**
     * Libera o acesso para clientes idependente de sua origem
     */
    private static void cors() {
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            response.header("Access-Control-Max-Age", "3600");
            response.header("Access-Control-Allow-Headers",
                "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With, Accept");
            response.header("Access-Control-Expose-Headers", "Authorization");
        });

    }

    /**
     * 
     * @param sender
     * @param message
     */
    public static void broadcastMessage(final String sender, final String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("userlist", userUsernameMap.values())
                ));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(final String sender, final String message) {
        return article(
            b(sender + " diz:"),
            span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
            p(message)
        ).render();
    }

}

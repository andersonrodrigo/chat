package hotchatmart.socket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import hotchatmart.entity.MensagemEntity;
import hotchatmart.entity.UsuarioEntity;
import hotchatmart.service.UsuarioService;

@WebSocket
public class ChatWebSocketHandler {


    private static List<MensagemEntity> listaMensagens = new ArrayList<MensagemEntity>();
    private static Map<String, List<MensagemEntity>> listaMensagemUsuario =
        new HashMap<String, List<MensagemEntity>>();

    @OnWebSocketConnect
    public void onConnect(final Session sess) throws Exception {
        final String token =
            sess.getUpgradeRequest().getRequestURI().toString().substring(
                sess.getUpgradeRequest().getRequestURI().toString().indexOf("?token=") + 7,
                sess.getUpgradeRequest().getRequestURI().toString().length());
        final UsuarioEntity usuarioEntity = UsuarioService.recuperaUsuarioByToken(token);
        Chat.nextUserNumber++;
        Chat.userUsernameMap.put(sess, usuarioEntity.getNome());
        if (Chat.nextUserNumber < 100) {
            enviaMensagem(sess, usuarioEntity, "Servidor diz: ", (usuarioEntity.getNome() + " Conectado"),
                true);
            verificaMensagensOffLine(sess, usuarioEntity);
        } else {

        }

    }


    /**
     * 
     * @param usuarioEntity
     */
    private void verificaMensagensOffLine(final Session user, final UsuarioEntity usuarioEntity) {
       
        final List<MensagemEntity> listaMensagensRecebidasUsuario =
            listaMensagemUsuario.get(usuarioEntity.getLogin());
        final List<MensagemEntity> listaClonada1 =
            new ArrayList<MensagemEntity>(listaMensagensRecebidasUsuario);
        final List<MensagemEntity> listaClonada2 = new ArrayList<MensagemEntity>(listaMensagens);
        for (final MensagemEntity mensagemUsuario : listaClonada2) {
            boolean achouMensagem = false;
            for (final MensagemEntity mensagemEntity : listaClonada1) {
                if (mensagemEntity.getId().intValue() == mensagemUsuario.getId().intValue()) {
                    achouMensagem = true;
                }
            }
            if (!achouMensagem) {
                Chat.enviaHistorico(mensagemUsuario.getUsuarioEnvio().getNome(),
                    mensagemUsuario.getTexto() + " (Enviado as " +
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                            .format(mensagemUsuario.getData()) +
                        "')",
                    usuarioEntity.getNome());

            }

        }

    }

    @OnWebSocketClose
    public void onClose(final Session sess, final int statusCode, final String reason) {
        final String username = Chat.userUsernameMap.get(sess);
        Chat.userUsernameMap.remove(sess);
        final String token =
            sess.getUpgradeRequest().getRequestURI().toString().substring(
                sess.getUpgradeRequest().getRequestURI().toString().indexOf("?token=") + 7,
                sess.getUpgradeRequest().getRequestURI().toString().length());
        final UsuarioEntity usuarioEntity = UsuarioService.recuperaUsuarioByToken(token);
        enviaMensagem(sess, usuarioEntity, "Servidor diz: ", (username + " Saiu"), true);
        // Chat.broadcastMessage(sender = "Servidor", msg = (username + " Saiu"));
    }

    @OnWebSocketMessage
    public void onMessage(final Session sess, final String message) {
        // Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message);
        final String username = Chat.userUsernameMap.get(sess);

        final String token =
            sess.getUpgradeRequest().getRequestURI().toString().substring(
                sess.getUpgradeRequest().getRequestURI().toString().indexOf("?token=") + 7,
                sess.getUpgradeRequest().getRequestURI().toString().length());
        final UsuarioEntity usuarioEntity = UsuarioService.recuperaUsuarioByToken(token);
        enviaMensagem(sess, usuarioEntity, username + " diz: ", message, true);
    }

    /**
     * Metodo para arnazenar a mensagem enviada
     * 
     * @param usuarioEntity
     * @param mensagem
     */
    private void enviaMensagem(final Session sess,
                               final UsuarioEntity usuarioEntity,
                               final String enviadoPor,
                               final String textoMensagem,
                               final boolean armazena) {
        final MensagemEntity mensagem = new MensagemEntity();
        Chat.broadcastMessage(enviadoPor, textoMensagem);
        if (armazena) {
            mensagem.setData(new Date());
            mensagem.setTexto(textoMensagem);
            mensagem.setUsuarioEnvio(usuarioEntity);
            mensagem.setId(System.currentTimeMillis());
            listaMensagens.add(mensagem);

            List<MensagemEntity> listaMensagens = listaMensagemUsuario.get(usuarioEntity.getLogin());
            if (listaMensagens == null) {
                listaMensagens = new ArrayList<MensagemEntity>();
            }
            listaMensagens.add(mensagem);
            listaMensagemUsuario.put(usuarioEntity.getLogin(), listaMensagens);
        }

    }

}

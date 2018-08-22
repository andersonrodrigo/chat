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

/**
 * Classe para configurar o Socket
 * 
 * @author andersonaugustorodrigosilva
 *
 */
@WebSocket
public class ChatWebSocketHandler {


    private static Map<String, List<MensagemEntity>> listaMensagemUsuario =
			new HashMap<String, List<MensagemEntity>>();// Map com as mensagens do usuario

	/**
	 * Metodo chamado ao conectar no websocket, nesse momento vejo se temos 100
	 * usurios simultaneos e verifico se existem mensagens off line para enviar para
	 * a tela
	 * 
	 * @param sess
	 * @throws Exception
	 */
    @OnWebSocketConnect
    public void onConnect( final Session sess) throws Exception {
		final String token = sess.getUpgradeRequest().getRequestURI().toString().substring(
				sess.getUpgradeRequest().getRequestURI().toString().indexOf("?token=") + 7,
				sess.getUpgradeRequest().getRequestURI().toString().length());
		final UsuarioEntity usuarioEntity = UsuarioService.recuperaUsuarioByToken(token);
		if (Chat.userUsernameMap.size() >= 100) {
			throw new Exception("Número de Usuário simultaneos ultrapassado!");
		} else {
			Chat.userUsernameMap.put(sess, usuarioEntity.getId() + "|" + usuarioEntity.getNome());
			enviaMensagem(sess, usuarioEntity, "Servidor diz: ", (usuarioEntity.getNome() + " Conectado"), false, "1");
			verificaMensagensOffLine(sess, usuarioEntity);
		}
    }


	/**
	 * Metodo para enviar as mensagens off Line
	 * 
	 * @param user
	 * @param usuarioEntity
	 */
    private void verificaMensagensOffLine( final Session user,  final UsuarioEntity usuarioEntity) {
		final List<MensagemEntity> listaMensagensRecebidasUsuario = listaMensagemUsuario.get(usuarioEntity.getLogin());
		if (listaMensagensRecebidasUsuario != null) {
			for (final MensagemEntity mensagemUsuario : listaMensagensRecebidasUsuario) {
				if (!mensagemUsuario.isLida()) {
					enviaMensageDireta(
							mensagemUsuario.getUsuarioEnvio().getId() + "|"
									+ mensagemUsuario.getUsuarioEnvio().getNome(),
							mensagemUsuario.getTexto() + " (Enviado as "
									+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(mensagemUsuario.getData())
									+ "')",
							mensagemUsuario.getUsuarioDestino().getId() + "|"
									+ mensagemUsuario.getUsuarioDestino().getNome(),
							false, null, null);
					mensagemUsuario.setLida(true);

				}
			}
		}
    }

	/**
	 * Metodo chamado quando o websocket é desconetado
	 * 
	 * @param sess
	 * @param statusCode
	 * @param reason
	 */
    @OnWebSocketClose
    public void onClose( final Session sess,  final int statusCode,  final String reason) {
         final String username = Chat.userUsernameMap.get(sess);
        Chat.userUsernameMap.remove(sess);
         final String token =
            sess.getUpgradeRequest().getRequestURI().toString().substring(
                sess.getUpgradeRequest().getRequestURI().toString().indexOf("?token=") + 7,
                sess.getUpgradeRequest().getRequestURI().toString().length());
         final UsuarioEntity usuarioEntity = UsuarioService.recuperaUsuarioByToken(token);
		enviaMensagem(sess, usuarioEntity, "Servidor diz: ", (username + " Saiu"), false, "2");

    }

	/**
	 * Metodo chamado para enviar mensagem no socket
	 * 
	 * @param sess:
	 *            Sessao do websocket
	 * @param message:
	 *            Mensagem Enviada
	 */
    @OnWebSocketMessage
    public void onMessage( final Session sess,  final String message) {
		final String userEnvio = Chat.userUsernameMap.get(sess);
		final String userDestino = message.split("\\|")[0];
		final String token = sess.getUpgradeRequest().getRequestURI().toString().substring(
				sess.getUpgradeRequest().getRequestURI().toString().indexOf("?token=") + 7,
				sess.getUpgradeRequest().getRequestURI().toString().length());
		final UsuarioEntity usuarioEntityEnvio = UsuarioService.recuperaUsuarioByToken(token);
		final UsuarioEntity usuarioEntityDestino = UsuarioService.recuperaUsuarioById(userDestino);
		enviaMensageDireta(userEnvio, message.split("\\|")[1],
				usuarioEntityDestino.getId() + "|" + usuarioEntityDestino.getNome(), true, usuarioEntityEnvio,
				usuarioEntityDestino);
    }

	/**
	 * MEtodo chamado para enviar a mensagem para algum usuario
	 * 
	 * @param userEnvio:
	 *            Identificador do usuario na sessao do socket
	 * @param textoMensagem:
	 *            Texto que será enviado
	 * @param userDestino:
	 *            Usuário de destino da mensagem
	 * @param armazena:
	 *            Se a mensagem será armazenada ou nao.
	 * @param usuarioEnvio:
	 *            Usuario que envia a mensagem
	 * @param usuarioDestino:
	 *            Usuario que sera enviada a mensagem
	 */
    private void enviaMensageDireta( final String userEnvio,
			final String textoMensagem, final String userDestino, final boolean armazena,
			final UsuarioEntity usuarioEnvio, final UsuarioEntity usuarioDestino) {
		final MensagemEntity mensagem = new MensagemEntity();
		Chat.enviaMensageDireta(userEnvio, textoMensagem, userDestino, "3");
		if (armazena) {
			mensagem.setData(new Date());
			mensagem.setTexto(textoMensagem);
			mensagem.setUsuarioEnvio(usuarioEnvio);
			mensagem.setUsuarioDestino(usuarioDestino);
			mensagem.setId(System.currentTimeMillis());
			mensagem.setLida(false);

			List<MensagemEntity> listaMensagens = listaMensagemUsuario.get(usuarioDestino.getLogin());
			if (listaMensagens == null) {
				listaMensagens = new ArrayList<MensagemEntity>();
			}
			listaMensagens.add(mensagem);
			listaMensagemUsuario.put(usuarioDestino.getLogin(), listaMensagens);
		}

    }


	/**
	 * Metodo chamado para enviar a mensagem
	 * 
	 * @param sess:
	 *            Sessao do websocket
	 * @param usuarioEntity:
	 *            USuario que envia a mensagem
	 * @param enviadoPor:
	 *            Identificador da sessao do usuario de envio
	 * @param textoMensagem:
	 *            Texto enviado
	 * @param armazena:
	 *            Se armazena ou nao.
	 * @param tipoMensagem:
	 *            Tipo da Mensagem: 1:Conectou no servidor, 2: Desconectou 3:
	 *            Mensagem Normal
	 */
    private void enviaMensagem( final Session sess,
                                final UsuarioEntity usuarioEntity,
                                final String enviadoPor,
                                final String textoMensagem,
                                final boolean armazena,
                                final String tipoMensagem) {
         final MensagemEntity mensagem = new MensagemEntity();
        Chat.broadcastMessage(usuarioEntity, textoMensagem, tipoMensagem);
        if (armazena) {
            mensagem.setData(new Date());
            mensagem.setTexto(textoMensagem);
            mensagem.setUsuarioEnvio(usuarioEntity);
            mensagem.setId(System.currentTimeMillis());

            List<MensagemEntity> listaMensagens = listaMensagemUsuario.get(usuarioEntity.getLogin());
            if (listaMensagens == null) {
                listaMensagens = new ArrayList<MensagemEntity>();
            }
            listaMensagens.add(mensagem);
            listaMensagemUsuario.put(usuarioEntity.getLogin(), listaMensagens);
        }

    }

}

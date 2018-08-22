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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import hotchatmart.controller.UsuarioController;
import hotchatmart.entity.UsuarioEntity;
import hotchatmart.service.UsuarioService;
import spark.Filter;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

/**
 * Classe principal, esta configurada no Manifest para iniciar o JAR. Ela vai
 * abrir um Servidor JERSEY na porta 8081 para receber as requisicoes
 * 
 * @author andersonaugustorodrigosilva
 *
 */
@SwaggerDefinition(host = "localhost:8081", 
info = @Info(description = "Exemplo de Chat", 
version = "V1.0", 
title = "Sala de Bate Papo", 
contact = @Contact(name = "Anderson", url = "https://github.com/andersonrodrigo") ) , 
schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS }, 
consumes = { "application/json" }, 
produces = { "application/json" }, 
tags = { @Tag(name = "swagger") })
public class Chat {

	
	
	static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();// Mapa com todas as sessoes ativas do
																			// websocket

	/**
	 * Metodo principal, ele sobe o servidor, abre o soket e configura os endpoints
	 * 
	 * @param args
	 */
    public static void main( final String[] args) {
		staticFiles.location("/public"); // informo o caminho de onde o servidor buscara as paginas html
		port(8081);// Porta que será aberta o jersey
		staticFiles.expireTime(-1);// Tempo de cache dos arquivos HTML, passei -1 para nao ter cache
		webSocket("/api/chat", ChatWebSocketHandler.class);// Configuro o endPoint do usuario
		cors();// Habilito as Requisicoes de dominio para o servidor porder ser acessado fora
				// do dominio padrao.
		new UsuarioController(new UsuarioService());// Inicio o controller
		init();// Inicio o jersey
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
	 * Metodo para enviar mensagem para todos os usuarios
	 * 
	 * @param usuarioEntity:
	 *            Usuario que esta enviando a mensagem
	 * @param message:
	 *            Mensagem que esta sendo enviada
	 * @param tipoMensagem:
	 *            Tipo da Mensagem: 1:Conectou no servidor, 2: Desconectou 3:
	 *            Mensagem Normal
	 */
    public static void broadcastMessage( final UsuarioEntity usuarioEntity,
                                         final String message,
                                         final String tipoMensagem) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(usuarioEntity.getLogin(), message))
                    .put("userlist", carregaListaDeContatos(usuarioEntity.getId()))
                    .put("tipoMensagem", tipoMensagem)

                ));
            } catch ( final Exception e) {
                e.printStackTrace();
            }
        });
    }

	/**
	 * Metodo para enviar uma mensagem direta de um usuario para outro
	 * 
	 * @param sender:
	 *            Usuario que esta enviando a mensagem
	 * @param message:
	 *            Mensagem enviada
	 * @param destinatario:
	 *            Usuario para que será enviada a mensagem
	 * @param tipoMensagem:
	 *            Tipo da Mensagem: 1:Conectou no servidor, 2: Desconectou 3:
	 *            Mensagem Normal
	 */
    public static void enviaMensageDireta( final String sender,
                                       final String message,
                                           final String destinatario,
                                           final String tipoMensagem) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                if (userUsernameMap.get(session).equals(destinatario)) {
                    session.getRemote()
                        .sendString(String.valueOf(
									new JSONObject()
											.put("userMessage",
													createHtmlMessageFromSender(sender.split("\\|")[1], message))
                                .put("userlist", carregaListaDeContatos(Long.valueOf(sender.split("\\|")[0])))
                                .put("tipoMensagem", tipoMensagem)
                                .put("destinatario", destinatario.split("\\|")[0])
                                .put("sender", sender.split("\\|")[0])));
                }

            } catch ( final Exception e) {
                e.printStackTrace();
			}
        });
    }

    /**
	 * Metodo para montar a lista de usuarios que será enviada na tela
	 * 
	 * @return Lista de usuarios
	 */
    private static List<UsuarioEntity> carregaListaDeContatos( final Long id) {
		final List<UsuarioEntity> listaRetorno = new ArrayList<UsuarioEntity>();
		final List<UsuarioEntity> listaTodosUsuarios = UsuarioService.getAllUsuarios();
		for (final Iterator<UsuarioEntity> iterator = listaTodosUsuarios.iterator(); iterator.hasNext();) {
			final UsuarioEntity usuarioEntity = iterator.next();
			boolean online = false;
			for (final Map.Entry<Session, String> pair : userUsernameMap.entrySet()) {
				if (usuarioEntity.getNome().equals(pair.getValue().split("\\|")[1])) {
					online = true;
					break;
				}
			}
			usuarioEntity.setOnline(online);
			listaRetorno.add(usuarioEntity);
		}
		return listaRetorno;
    }

	/**
	 * Metodo para montar o HTML da Mensagem que será exibida.
	 * 
	 * @param sender
	 * @param message
	 * @return
	 */
    private static String createHtmlMessageFromSender( final String sender,  final String message) {
        return article(
            b(sender),
            span(attrs(".timestamp"), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())),
            p(message)
        ).render();
    }


}

//Establish the WebSocket connection and set up event handlers
var host = 'localhost'; //Caminho do Servidor
var port = '8081'; //Porta do Servidor
var urlBase = 'http://'+host+':'+port+'/';//Url base para as consultas
var usuarioLogado = window.localStorage.getItem('usuarioNome'); //Pego o nome do usuario do storage
var idUsuarioLogado = window.localStorage.getItem('usuarioId');//Pego o Id do usuario do storage
var contatoSelecionado = null;//Representa o usuario que esta com a conversa aberta no momento
var listaConversas = new Map();//Mapa de conversas dos usuarios, teremos o usuario e a lista de mensagens que que ele manda
$("#chatControls").hide();//Caixa de entrada das mensagens. Começo ela escondida e quando um contato é selecionado eu abro
$("#instrucoes").show();//Mensagem de instuçoes que aparece quando a caixa de controle esta fechada
$("#usuarioDestino").text('');//Usuario que sera enviada a mensgem
$('#usuario').html(usuarioLogado);//PReencho o nome do usuario na tela
var webSocket = new WebSocket("ws://" + host + ":" + port + "/api/chat?token=" + window.localStorage.getItem('token')	);//Inicio o websocket, eu mando um token jwt para a identificacao do usuario no socket 
/**
 * Configuro o socket para quando receber uma mensagem chamar o metodo update chat
 */
webSocket.onmessage = function (msg) { 
	updateChat(msg); 
};
/**
 * Metodo chamado quando o websocket é fechado
 */
webSocket.onclose = function () { 
	document.location = '/index.html'
};

// Configuro o botao de enviar mensagem para quando clicar a mensagem ser enviada
id("send").addEventListener("click", function () {
	sendMessage(id("message").value);
});


//Configuro o botao de enviar mensagem para quando apertar ENTER a mensagem ser enviada
id("message").addEventListener("keypress", function (e) {
	if (e.keyCode === 13) { sendMessage(e.target.value); }
});

/**
 * MEtodo para enviar a mensagem
 * @param message Mensagem que sera enviada
 * @returns
 */
function sendMessage(message) {
	if (message !== "") {
		var data = new Date();//Pego a data atual
		$('#chat').append("<article><b>"+usuarioLogado+"</b><span class='timestamp'>"+ data.toLocaleDateString() + " "+ data.toLocaleTimeString()+"</span><p>"+message+"</p></article>")
		listaConversas.get(contatoSelecionado).push("<article><b>"+usuarioLogado+"</b><span class='timestamp'>"+ data.toLocaleDateString() + " "+ data.toLocaleTimeString()+"</span><p>"+message+"</p></article>");
		message = contatoSelecionado + "|" + message;//Adiciono o contato na mensagem
		webSocket.send(message);//Envio para o socket a mensagem
		id("message").value = "";//Limpo a caixa de entrada da mensagem
	}
}

/**
 * Metodo chamado para Receber a mensagem 
 * @param msg Mensagem que veio do servidor
 * @returns
 */
function updateChat(msg) {
	let data = JSON.parse(msg.data);
	id("userlist").innerHTML = "";
	data.userlist.forEach(function (user) {// Pego a lista de usuarios
		if (user.id != idUsuarioLogado){ //Retiro o proprio usuario logado da lista
			let iconeOnline = '<button type="button" class="btn btn-primary" style="background-color:green"/>'
				if (!user.online){//Vejo se esta online ou off lina para desenhar na tela
					iconeOnline = iconeOnline.replace('green','red')
				}
			insert("userlist", "<li onclick='selecionaContato(\""+user.id+"\",\""+user.nome+"\")'><span id='"+user.id+"'/>" + user.nome + " " + iconeOnline +" </li>");
		}
	});
	if (data.tipoMensagem == '3'){//Tipo 3 é tipo msg de envio, 1 é conectou e 2 é saiu
		if (data.sender == contatoSelecionado){//Vejo se a conversa esta aberta para receber a mensagem
			$('#chat').append(data.userMessage);//Estando aberta apresento a menssagem
			listaConversas.get(contatoSelecionado).push(data.userMessage);//Coloco a mensagem em um Map de conversas que tem como key o usuario
		}else{
			$('#'+ data.sender).append('<small id="Notificacao'+data.sender+'">Nova Mensagem</small>');//Notificacao de uma nova mensagem
			if ( listaConversas.get(data.sender)){
				listaConversas.get(data.sender).push(data.userMessage);
			}else{
				listaConversas.set(data.sender,[data.userMessage]);//Atualizo o Map de mensagen do usuario
			}
		}
	}

}

/**
 * Metodo para adiconar elementos html em sequencia
 * @param targetId
 * @param message
 * @returns
 */
function insert(targetId, message) {
	id(targetId).insertAdjacentHTML("afterbegin", message);
}

/**
 * Metodo para pegar um elemento por id
 * @param id
 * @returns
 */
function id(id) {
	return document.getElementById(id);
}


/**
 * Metodo chamado para abrir a conversa, é chamado quando clica em algum usuario na lista
 * @param user:Usuario seleciondo
 * @param nome:Nome do usuario selecionado
 * @returns
 */
function selecionaContato(user, nome){
	 $('#Notificacao'+user).html('');//Limpo a notificação de nova conversa
	if (contatoSelecionado != user){//verifico se a conversa já esta aberta
		contatoSelecionado = user;//Seleciono o usuario
		id("chat").innerHTML = "";//Limpo as conversas
		if (listaConversas.get(contatoSelecionado)){//Recupero as cvs do mapa
			id("chat").innerHTML = "";
			listaConversas.get(contatoSelecionado).forEach(function (msg) {//Preencho as conversas do mapa na tela
				$('#chat').append(msg)
			});
		}else{
			id("chat").innerHTML = "";//Limpo as conversas na tela
			listaConversas.set(contatoSelecionado, []);//Inicio o mapa de mensagem
		}
	}
	$("#chatControls").show();//Habilito a entrada da cv
	$("#instrucoes").hide();//Escondo as intrucoes
	$("#usuarioDestino").text(nome);//Preencho o nome do usuario de detino
}



$('.alert').hide();
 


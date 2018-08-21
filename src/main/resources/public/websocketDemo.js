//Establish the WebSocket connection and set up event handlers
var host = 'localhost'
var port = '8081'
var urlBase = 'http://'+host+':'+port+'/'
var usuarioLogado = window.localStorage.getItem('usuarioNome')
var idUsuarioLogado = window.localStorage.getItem('usuarioId')
  $('#usuario').html(usuarioLogado)
var webSocket = new WebSocket("ws://" + host + ":" + port + "/api/chat?token=" + window.localStorage.getItem('token')	);
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose = function () { 
	//alert("WebSocket connection closed") 
	document.location = '/index.html'
	};

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
    	  //	$('#chat').append(message)
    	var data = new Date();
    	
    	  	//insert("chat", "<article><b>"+usuarioLogado+"</b><span class='timestamp'>"+ data.toLocaleDateString() + " "+ data.toLocaleTimeString()+"</span><p>"+message+"</p></article>");
    	  	$('#chat').append("<article><b>"+usuarioLogado+"</b><span class='timestamp'>"+ data.toLocaleDateString() + " "+ data.toLocaleTimeString()+"</span><p>"+message+"</p></article>")
    		listaConversas.get(contatoSelecionado).push("<article><b>"+usuarioLogado+"</b><span class='timestamp'>"+ data.toLocaleDateString() + " "+ data.toLocaleTimeString()+"</span><p>"+message+"</p></article>");
    	  	message = contatoSelecionado + "|" + message
        webSocket.send(message);
        id("message").value = "";
      
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
   
    id("userlist").innerHTML = "";
    if ($( "li" ).length != data.userlist.length){
    		carregaListaUsuarios()
    }
    data.userlist.forEach(function (user) {
    
    	if (idUsuarioLogado == user.split("|")[0]){
    		insert("userlist", "<li onclick='selecionaContato(\""+user.split("|")[0]+"\",\""+user.split("|")[1]+"\")'><span id='"+user.split("|")[0]+"'/><font color='red'><B>" + user.split("|")[1] + "</B></font></li>");
    	}else{
    		insert("userlist", "<li onclick='selecionaContato(\""+user.split("|")[0]+"\",\""+user.split("|")[1]+"\")'><span id='"+user.split("|")[0]+"'/>" + user.split("|")[1] + "</li>");
    	}
    });
    if (data.tipoMensagem == '3'){
	    if (data.sender == contatoSelecionado){
	    		//insert("chat", data.userMessage);
	    		$('#chat').append(data.userMessage)
	    		listaConversas.get(contatoSelecionado).push(data.userMessage);
	    }else{
	    	   //$("#" + data.sender).text('<small>Nova Mensagem</small>')
	    	    $('#'+ data.sender).append('<small>Nova Mensagem</small>')
	    	    if ( listaConversas.get(data.sender)){
	    	    	   listaConversas.get(data.sender).push(data.userMessage);
	    	    }else{
	    	    	 listaConversas.set(data.sender,[data.userMessage])
	    	    }
	    	   
	    	  // insert("chat", data.userMessage);
	    }
}
   
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}



function carregaListaUsuarios(){
	  $.get(urlBase + 'api/user/listAll', function(data, status) {
	         id("userlistAll").innerHTML = "";
	         if (data) {
	            data.forEach(function (user) {
	              insert("userlistAll", "<li onclick='selecionaContato(\""+user.id+"\",\""+user.nome+"\")'>" + user.nome + "</li>");
	            });
	         } 
	       });
}
    
var contatoSelecionado = null;
var listaConversas = new Map();
$("#chatControls").hide()
$("#instrucoes").show()
$("#usuarioDestino").text('')

function selecionaContato(user, nome){
	 debugger
	 if (contatoSelecionado != user){
		 contatoSelecionado = user;
		 if (listaConversas.get(contatoSelecionado)){
			 id("chat").innerHTML = "";
			 listaConversas.get(contatoSelecionado).forEach(function (msg) {
				 $('#chat').append(msg)
			 });
		 }else{
			 id("chat").innerHTML = "";
			 listaConversas.set(contatoSelecionado, []);
		 }
	 }
	
	$("#chatControls").show()
	$("#instrucoes").hide()
	$("#usuarioDestino").text(nome)
}
   
    carregaListaUsuarios();
 

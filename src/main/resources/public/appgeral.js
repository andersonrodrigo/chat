$('.alert').hide();//Escondo a popup
var urlBase = 'http://localhost:8081/';//Url base das requisicoes ajax

/**
 * Metodo chamado para cadastrar o usuario
 * @returns
 */
function cadastrarUsuario() {
	if ($('#senhaCadastro').val() != $('#senhaCadastro2').val()) {
		alert('Senhas não conferem.')
	} else {
		$.post(urlBase + 'api/user', {
			nome : $('#nomeCadastro').val(),
			login : $('#loginCadastro').val(),
			senha : $('#senhaCadastro').val()
		}, function(data, status) {
			if (data){
				if (data == 'sucesso'){
					$('#btnFechaPopup').click()
					alert('Cadastrado com sucesso!')
					limpaTela()
				}else{
					alert(data)
				}
			}else{
				alert('Erro ao Cadastrar!')
			}
		});
	}
}

/**
 * MEtodo para fazer o login
 * @returns
 */
function fazLogin() {
	$.post(urlBase + 'api/user/login', {
		login : $('#login').val(),
		senha : $('#senha').val()
	}, function(data, status) {
		if (data) {
			window.localStorage.setItem('usuarioId', data.id);
			window.localStorage.setItem('usuarioNome', data.nome);
			window.localStorage.setItem('token', data.token);
			document.location = 'chat.html' 
		} else {
			alert('Senhas não conferem...')
		}
	});
}


/**
 * Metodo para limpar a tela
 * @returns
 */
function limpaTela(){ 
	$('#nomeCadastro').val(''),
	$('#loginCadastro').val(''),
	$('#senhaCadastro').val('')
	$('#senhaCadastro2').val('')
}


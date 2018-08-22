# Chat com websocket
##Aplicação que possui um chat com websocket

A Aplicação foi montada utilizando o frame work spark.
Esta rodando em memoria. (Sem Banco de Dados)
###Para rodar execute:


*mvn clean package


Ache o JAR gerado e rode 


*java -jar hotchatmart-1.0-SNAPSHOT.jar


Assim será aberto um servidor jetty na porta 8081.
Acesse de um navegador:

http://localhost:8081

Será aberta uma tela para login, onde existirá a opção Novo Cadastro.
Ao fazer um novo cadastro este usuário ficará em memoria.
Ao logar com o usuário será gerado um token JWT que será utilizado para validar a sessao do usuário no chat.

Esta aplicação está implementada utilizando novos conceitos do JAVA 8 e um pensamento um pouco diferente do normal das aplicações java.

Qualquer duvida ou sugestão: anderson.rodrigo@gmail.com
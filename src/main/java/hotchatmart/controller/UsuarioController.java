package hotchatmart.controller;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.Gson;

import hotchatmart.entity.UsuarioEntity;
import hotchatmart.exception.ResponseError;
import hotchatmart.service.UsuarioService;
import spark.ResponseTransformer;

/**
 * Classe criada para ser o controller do usuário.
 * 
 * @author andersonaugustorodrigosilva
 *
 */
public class UsuarioController {

	/**
	 * Contrutor do controller. Aqui sao configurados os caminhos dos enpoitns
	 * 
	 * @param usuarioService
	 */
    public UsuarioController( final UsuarioService usuarioService) {

    	
		get("/api/user/listAll", (req, res) -> UsuarioService.getAllUsuarios(), json());

        get("/api/user/:id", (req, res) -> {
             final String id = req.params(":id");
             final UsuarioEntity user = usuarioService.getUser(id);
            if (user != null) {
                return user;
            }
            res.status(400);
            return new ResponseError("No user with id '%s' found", id);
        }, json());

        post("/api/user", (req, res) -> {
             final UsuarioEntity usuarioExistente = usuarioService.getUser(req.queryParams("login"));
            if (usuarioExistente != null) {
                return "Já existe um usuário com o Login informado: " + req.queryParams("login");
            }
            usuarioService.criaUsuario(req.queryParams("nome"), req.queryParams("login"),
                req.queryParams("senha")
            );
            return "sucesso";
		}, json());

        post("/api/user/login",
            (req, res) ->  
            usuarioService.login(req.queryParams("login"), req.queryParams("senha"))
             , json());

        put("/api/user/:id", (req, res) -> usuarioService.atualizaUsuario(
                req.params(":id"),
                req.queryParams("name"),
                req.queryParams("email"),
                req.queryParams("password")
        ), json());

        after((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
    }

	/**
	 * Monta o json de resposta.
	 * 
	 * @param object
	 * @return
	 */
    private static String toJson( final Object object) {
        return new Gson().toJson(object);
    }

	/**
	 * Transforma o json para resposta.
	 * 
	 * @return
	 */
    private static ResponseTransformer json() {
        return UsuarioController::toJson;
    }


}

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

public class UsuarioController {

    public UsuarioController(final UsuarioService usuarioService) {

        get("/api/users", (req, res) -> usuarioService.getAllUsuarios(), json());

        get("/api/users/:id", (req, res) -> {
            final String id = req.params(":id");
            final UsuarioEntity user = usuarioService.getUser(id);
            if (user != null) {
                return user;
            }
            res.status(400);
            return new ResponseError("No user with id '%s' found", id);
        }, json());

        post("/api/users", (req, res) -> usuarioService.criaUsuario(
            req.queryParams("nome"), req.queryParams("login"), req.queryParams("senha")
        ), json());

        post("/api/user/login",
            (req, res) ->  
            usuarioService.login(req.queryParams("login"), req.queryParams("senha"))
             , json());

        put("/api/users/:id", (req, res) -> usuarioService.atualizaUsuario(
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

    private static String toJson(final Object object) {
        return new Gson().toJson(object);
    }

    private static ResponseTransformer json() {
        return UsuarioController::toJson;
    }


}

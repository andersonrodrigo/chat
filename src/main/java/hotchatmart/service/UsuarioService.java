package hotchatmart.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hotchatmart.entity.UsuarioEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Classe de serviço do usuario
 * 
 * @author andersonaugustorodrigosilva
 *
 */
public class UsuarioService {

	private static List<UsuarioEntity> usuarios = new ArrayList<UsuarioEntity>();// Lista de usuarios cadastrados
    private static long EXPIRES_IN = 720 * 60; // 15 min expiracao token
    private static String SECRET = "5Uda*=ch=?uNuStAsT75e7?EsTA=?4HE";// chave jwt

    /**
	 * Metodo para criar o usuario, como nao uso banco de dados a lista de usuarios
	 * é o banco
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
    public UsuarioEntity criaUsuario( final String nome,  final String login,  final String password) {
         final UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setNome(nome);
        usuarioEntity.setLogin(login);
        usuarioEntity.setId(System.currentTimeMillis());
        usuarioEntity.setPassword(password);
        usuarios.add(usuarioEntity);
        return usuarioEntity;
    }

    /**
	 * Metodo para fazer o login. Eu recupero o usuario e volto um novo objeto de
	 * usuario sem a senha como resposta para o servidor
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
    public UsuarioEntity login( final String email,  final String password) {
        for ( final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getLogin().equals(email) && usuarioEntity.getPassword().equals(password)) {
                 final String token = geraTokenUsuario(usuarioEntity);
                usuarioEntity.setToken(token);
                 final UsuarioEntity usuarioRetorno = new UsuarioEntity();
                usuarioRetorno.setToken(token);
                usuarioRetorno.setNome(usuarioEntity.getNome());
                usuarioRetorno.setId(usuarioEntity.getId());
                return usuarioRetorno;
            }
        }
        return null;
    }

    /**
	 * Metodo para gerar o token JWT do usuario
	 * 
	 * @param usuarioEntity
	 * @return
	 */
    private String geraTokenUsuario( final UsuarioEntity usuarioEntity) {
        return Jwts.builder()
            .setSubject(usuarioEntity.getLogin())
            .setIssuedAt(Calendar.getInstance().getTime())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRES_IN * 1000))
            .claim("user", usuarioEntity)
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact();
	}

    /**
	 * Metodo para retornar todos os usuarios
	 * 
	 * @return
	 */
    public static List<UsuarioEntity> getAllUsuarios() {
         final List<UsuarioEntity> retorno = new ArrayList<UsuarioEntity>();
        for ( final UsuarioEntity usuarioEntity : usuarios) {
             final UsuarioEntity copiaUsuario = new UsuarioEntity();
            copiaUsuario.setLogin(usuarioEntity.getLogin());
            copiaUsuario.setNome(usuarioEntity.getNome());
            copiaUsuario.setId(usuarioEntity.getId());
            retorno.add(copiaUsuario);
        }
        return retorno;
    }

    /**
	 * Metodo para retornar o usuario pelo id
	 * 
	 * @param id
	 * @return
	 */
    public UsuarioEntity getUser( final Long id) {
        for ( final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getId().intValue() == id.intValue()) {
                return usuarioEntity;
            }
        }
        return null;
    }

    /**
	 * Recupera o usuario pelo login
	 * 
	 * @param login
	 * @return
	 */
    public UsuarioEntity getUser( final String login) {
        for ( final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getLogin().equals(login)) {
                return usuarioEntity;
            }
        }
        return null;
    }

    /**
	 * Atualiza o usuario
	 * 
	 * @param id
	 * @param name
	 * @param email
	 * @return
	 */
    public UsuarioEntity atualizaUsuario( final String id,
                                          final String name,
                                          final String email,
                                          final String password) {
         final UsuarioEntity usuarioEntity = getUser(email);
        if (usuarioEntity == null) {
            throw new IllegalArgumentException("Usuario não encontrado:" + email);
        }
        usuarioEntity.setNome(name);
        usuarioEntity.setLogin(email);
        usuarioEntity.setPassword(password);
        return usuarioEntity;
    }

    /**
	 * Recupera o usuario pelo token
	 * 
	 * @param token
	 * @return
	 */
    public static UsuarioEntity recuperaUsuarioByToken( final String token) {
        for ( final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getToken() != null && usuarioEntity.getToken().equals(token)) {
                return usuarioEntity;
            }
        }
        return null;
    }

    /**
	 * Recupera usuario pelo id
	 * 
	 * @param userDestino
	 * @return
	 */
    public static UsuarioEntity recuperaUsuarioById( final String id) {
        for ( final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getId() != null && usuarioEntity.getId().equals(Long.valueOf(id))) {
                return usuarioEntity;
            }
        }
        return null;
    }

}

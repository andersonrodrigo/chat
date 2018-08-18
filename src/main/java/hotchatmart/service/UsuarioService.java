package hotchatmart.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hotchatmart.entity.UsuarioEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class UsuarioService {
    private static List<UsuarioEntity> usuarios = new ArrayList<UsuarioEntity>();
    private static long EXPIRES_IN = 720 * 60; // 15 min expiracao token
    private static String SECRET = "5Uda*=ch=?uNuStAsT75e7?EsTA=?4HE";// chave jwt
    /**
     * @param login
     * @param password
     * @return
     */
    public UsuarioEntity criaUsuario(final String nome, final String login, final String password) {
        final UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setNome(nome);
        usuarioEntity.setLogin(login);
        usuarioEntity.setPassword(password);
        usuarios.add(usuarioEntity);
        return usuarioEntity;
    }

    /**
     * 
     * @param email
     * @param password
     * @return
     */
    public String login(final String email, final String password) {
        for (final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getLogin().equals(email) && usuarioEntity.getPassword().equals(password)) {
                final String token = geraTokenUsuario(usuarioEntity);
                usuarioEntity.setToken(token);
                return token;
            }
        }
        return null;
    }

    /**
     * 
     * @param usuarioEntity
     * @return
     */
    private String geraTokenUsuario(final UsuarioEntity usuarioEntity) {
        return Jwts.builder()
            .setSubject(usuarioEntity.getLogin())
            .setIssuedAt(Calendar.getInstance().getTime())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRES_IN * 1000))
            .claim("user", usuarioEntity)
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact();
    }

    /**
     * 
     * @return
     */
    public List<UsuarioEntity> getAllUsuarios() {
        return usuarios;
    }

    /**
     * 
     * @param id
     * @return
     */
    public UsuarioEntity getUser(final Long id) {
        for (final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getId().intValue() == id.intValue()) {
                return usuarioEntity;
            }
        }
        return null;
    }

    /**
     * 
     * @param login
     * @return
     */
    public UsuarioEntity getUser(final String login) {
        for (final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getLogin().equals(login)) {
                return usuarioEntity;
            }
        }
        return null;
    }

    /**
     * 
     * @param id
     * @param name
     * @param email
     * @return
     */
    public UsuarioEntity atualizaUsuario(final String id,
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
     * 
     * @param token
     * @return
     */
    public static UsuarioEntity recuperaUsuarioByToken(final String token) {
        for (final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getToken() != null && usuarioEntity.getToken().equals(token)) {
                return usuarioEntity;
            }
        }
        return null;
    }

}

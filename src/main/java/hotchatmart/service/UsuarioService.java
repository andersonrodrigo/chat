package hotchatmart.service;

import java.util.ArrayList;
import java.util.List;

import hotchatmart.entity.UsuarioEntity;

public class UsuarioService {
    private List<UsuarioEntity> usuarios = new ArrayList<UsuarioEntity>();

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
    public boolean login(final String email, final String password) {
        for (final UsuarioEntity usuarioEntity : usuarios) {
            if (usuarioEntity.getLogin().equals(email) && usuarioEntity.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
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

}

package hotchatmart.entity;

public class UsuarioEntity {

    private Long id;
    private String nome;
    private String login;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * 
     * @param id
     * @param nome
     * @param login
     * @param password
     */
    public UsuarioEntity(final Long id, final String nome, final String login, final String password) {
        super();
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.password = password;
    }

    public UsuarioEntity() {
    }

}

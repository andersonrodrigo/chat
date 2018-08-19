package hotchatmart.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Entidade para salvar a mensagem enviada.
 * 
 * @author andersonaugustorodrigosilva
 *
 */
public class MensagemEntity implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5401334937402171350L;

    /**
     * Identifiacador da Mensagem
     */
    private Long id;

    /**
     * Texto da mensagem
     */
    private String texto;

    /**
     * Data de envio da mensagem
     */
    private Date data;

    /**
     * Usuario que enviou a mensagem
     */
    private UsuarioEntity usuarioEnvio;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(final String texto) {
        this.texto = texto;
    }

    public Date getData() {
        return data;
    }

    public void setData(final Date data) {
        this.data = data;
    }

    public UsuarioEntity getUsuarioEnvio() {
        return usuarioEnvio;
    }

    public void setUsuarioEnvio(final UsuarioEntity usuarioEnvio) {
        this.usuarioEnvio = usuarioEnvio;
    }

}

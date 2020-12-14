package hyve.petshow.domain.embeddables;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

import static hyve.petshow.util.AuditoriaUtils.ATIVO;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Embeddable
@NoArgsConstructor
public class Auditoria {
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long usuarioCriacao;
    private String flagAtivo;
    
    public Boolean isAtivo() {
    	return ATIVO.equals(getFlagAtivo());
    }
}

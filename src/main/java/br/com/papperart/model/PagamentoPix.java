package br.com.papperart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pagamento por Pix.
 */
public class PagamentoPix extends Pagamento {

    public PagamentoPix(BigDecimal valor) {
        super(valor);
    }

    /** Recria um pagamento salvo no banco (usado pelo DAO). */
    public PagamentoPix(BigDecimal valor, LocalDateTime data) {
        super(valor, data);
    }

    /**
     * O Pix não tem regra extra: o valor maior que zero já é conferido
     * no construtor da superclasse.
     */
    @Override
    public void validar() {
        // Nada a conferir além do que a superclasse já garante
    }

    @Override
    public String getDescricao() {
        return "Pix";
    }
}

package br.com.papperart.model;

import br.com.papperart.exception.PagamentoInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pagamento em dinheiro, com troco.
 */
public class PagamentoDinheiro extends Pagamento {

    private final BigDecimal valorRecebido;

    public PagamentoDinheiro(BigDecimal valor, BigDecimal valorRecebido) {
        this(valor, LocalDateTime.now(), valorRecebido);
    }

    /** Recria um pagamento salvo no banco (usado pelo DAO). */
    public PagamentoDinheiro(BigDecimal valor, LocalDateTime data, BigDecimal valorRecebido) {
        super(valor, data);
        if (valorRecebido == null) {
            throw new IllegalArgumentException("Informe o valor recebido.");
        }
        this.valorRecebido = valorRecebido;
    }

    /** RN09: o dinheiro recebido tem que cobrir o valor do pagamento. */
    @Override
    public void validar() throws PagamentoInvalidoException {
        if (valorRecebido.compareTo(getValor()) < 0) {
            throw new PagamentoInvalidoException("Valor recebido (R$ " + valorRecebido
                    + ") é menor que o valor do pagamento (R$ " + getValor() + ").");
        }
    }

    /** RN09: troco = valor recebido − valor do pagamento (nunca negativo). */
    public BigDecimal calcularTroco() {
        return valorRecebido.subtract(getValor()).max(BigDecimal.ZERO);
    }

    @Override
    public String getDescricao() {
        return "Dinheiro (troco: R$ " + calcularTroco() + ")";
    }

    // ---------- Getters ----------

    public BigDecimal getValorRecebido() { return valorRecebido; }
}

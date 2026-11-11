package br.com.papperart.model;

import br.com.papperart.exception.PagamentoInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pagamento com cartão de crédito ou de débito.
 * <p>
 * Crédito e débito não viraram subclasses: o comportamento é o mesmo,
 * só muda a regra das parcelas. Por isso o tipo é um atributo.
 */
public class PagamentoCartao extends Pagamento {

    /** Máximo de parcelas no crédito. Depende da maquininha da loja. */
    public static final int MAXIMO_PARCELAS = 12;

    private final TipoCartao tipo;
    private final int parcelas;

    public PagamentoCartao(BigDecimal valor, TipoCartao tipo, int parcelas) {
        this(valor, LocalDateTime.now(), tipo, parcelas);
    }

    /** Recria um pagamento salvo no banco (usado pelo DAO). */
    public PagamentoCartao(BigDecimal valor, LocalDateTime data, TipoCartao tipo, int parcelas) {
        super(valor, data);
        if (tipo == null) {
            throw new IllegalArgumentException("Informe se o cartão é de crédito ou de débito.");
        }
        this.tipo = tipo;
        this.parcelas = parcelas;
    }

    /** RN08: débito é sempre 1 parcela; crédito vai de 1 até o máximo da maquininha. */
    @Override
    public void validar() throws PagamentoInvalidoException {
        if (tipo == TipoCartao.DEBITO && parcelas != 1) {
            throw new PagamentoInvalidoException("Cartão de débito não pode ser parcelado.");
        }
        if (parcelas < 1 || parcelas > MAXIMO_PARCELAS) {
            throw new PagamentoInvalidoException(
                    "No crédito, o parcelamento vai de 1 a " + MAXIMO_PARCELAS + " vezes.");
        }
    }

    @Override
    public String getDescricao() {
        if (tipo == TipoCartao.DEBITO) {
            return "Cartão de débito";
        }
        return (parcelas == 1) ? "Cartão de crédito à vista" : "Cartão de crédito em " + parcelas + "x";
    }

    // ---------- Getters ----------

    public TipoCartao getTipo() { return tipo; }

    public int getParcelas() { return parcelas; }
}

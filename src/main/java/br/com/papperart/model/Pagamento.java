package br.com.papperart.model;

import br.com.papperart.exception.PagamentoInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Um pagamento de pedido.
 * <p>
 * É abstrata (A5): não existe "pagamento genérico", todo pagamento é de algum
 * tipo (Pix, cartão ou dinheiro). Cada subclasse tem a sua própria regra (A3).
 */
public abstract class Pagamento {

    private int id; // gerado pelo banco
    private final BigDecimal valor;
    private final LocalDateTime data;

    /** Pagamento novo, feito agora. */
    protected Pagamento(BigDecimal valor) {
        this(valor, LocalDateTime.now());
    }

    /** Pagamento que já está salvo no banco, com a data original (usado pelo DAO). */
    protected Pagamento(BigDecimal valor, LocalDateTime data) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do pagamento tem que ser maior que zero.");
        }
        if (data == null) {
            throw new IllegalArgumentException("A data do pagamento é obrigatória.");
        }
        this.valor = valor;
        this.data = data;
    }

    /**
     * Confere a regra de negócio deste tipo de pagamento.
     * Cada subclasse implementa do seu jeito (polimorfismo, A4).
     */
    public abstract void validar() throws PagamentoInvalidoException;

    /** Texto pra mostrar na tela, ex: "Cartão de crédito em 3x". */
    public abstract String getDescricao();

    /** O id é gerado pelo banco: quem chama é o DAO, depois de salvar. */
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id inválido.");
        }
        this.id = id;
    }

    // ---------- Getters ----------

    public int getId() { return id; }

    public BigDecimal getValor() { return valor; }

    public LocalDateTime getData() { return data; }

    @Override
    public String toString() {
        return getDescricao() + ": R$ " + valor;
    }
}

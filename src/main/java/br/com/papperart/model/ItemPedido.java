package br.com.papperart.model;

import java.math.BigDecimal;

/**
 * Uma linha do pedido: um produto e a quantidade.
 * <p>
 * Só existe dentro de um {@link Pedido} (composição): é o próprio pedido que cria os itens.
 */
public class ItemPedido implements Precificavel {

    private int id; // gerado pelo banco
    private final Produto produto;
    private int quantidade;

    // RN05 e A1: atributo SEM setter. O preço é congelado quando o item entra no pedido:
    // se o produto mudar de preço depois, este pedido continua com o preço antigo.
    private final BigDecimal precoUnitario;

    /**
     * Item novo: congela o preço atual do produto.
     * Não é public porque só o {@link Pedido} cria itens novos.
     */
    ItemPedido(Produto produto, int quantidade) {
        this(produto, quantidade, (produto == null) ? null : produto.getPreco());
    }

    /**
     * Recria um item que já está salvo no banco, com o preço da época da compra.
     * Usado pelo DAO.
     */
    public ItemPedido(Produto produto, int quantidade, BigDecimal precoUnitario) {
        if (produto == null) {
            throw new IllegalArgumentException("O item precisa de um produto.");
        }
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do item tem que ser maior que zero.");
        }
        this.produto = produto;
        this.precoUnitario = precoUnitario;
        setQuantidade(quantidade);
    }

    /** Total da linha: preço congelado × quantidade. */
    @Override
    public BigDecimal calcularTotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    /** Só o Pedido muda a quantidade (ex: o mesmo produto adicionado de novo). */
    void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade tem que ser maior que zero.");
        }
        this.quantidade = quantidade;
    }

    /** O id é gerado pelo banco: quem chama é o DAO, depois de salvar. */
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id inválido.");
        }
        this.id = id;
    }

    // ---------- Getters ----------

    public int getId() { return id; }

    public Produto getProduto() { return produto; }

    public int getQuantidade() { return quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }

    @Override
    public String toString() {
        return quantidade + "x " + produto.getNome() + " (R$ " + precoUnitario + " cada)";
    }
}

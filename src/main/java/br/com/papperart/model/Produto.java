package br.com.papperart.model;

import br.com.papperart.exception.EstoqueInsuficienteException;

import java.math.BigDecimal;

/**
 * Um produto vendido pela PapperArt.
 * <p>
 * Não tem subclasses: caneta, caderno e mochila se comportam igual no sistema
 * (têm preço, estoque e são vendidos). O que muda entre eles é só a categoria.
 */
public class Produto {

    private int id; // gerado pelo banco
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int estoque;
    private Categoria categoria;

    public Produto(String nome, String descricao, BigDecimal preco, int estoque, Categoria categoria) {
        // O construtor passa pelos setters: um produto nunca nasce inválido (A2)
        setNome(nome);
        setDescricao(descricao);
        setPreco(preco);
        setEstoque(estoque);
        setCategoria(categoria);
    }

    /**
     * Tira unidades do estoque. É chamado quando o pedido é pago (RN07).
     */
    public void baixarEstoque(int quantidade) throws EstoqueInsuficienteException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade tem que ser maior que zero.");
        }
        if (quantidade > estoque) {
            throw new EstoqueInsuficienteException("Estoque insuficiente de " + nome
                    + ": tem " + estoque + ", precisa de " + quantidade + ".");
        }
        estoque -= quantidade;
    }

    // ---------- Setters que validam a entrada (A1) ----------

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        this.nome = nome.trim();
    }

    public void setDescricao(String descricao) {
        this.descricao = (descricao == null) ? "" : descricao.trim();
    }

    /** RN01: o preço tem que ser maior que zero. */
    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço tem que ser maior que zero.");
        }
        this.preco = preco;
    }

    /** RN01: o estoque nunca pode ser negativo. */
    public void setEstoque(int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo.");
        }
        this.estoque = estoque;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Escolha uma categoria.");
        }
        this.categoria = categoria;
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

    public String getNome() { return nome; }

    public String getDescricao() { return descricao; }

    public BigDecimal getPreco() { return preco; }

    public int getEstoque() { return estoque; }

    public Categoria getCategoria() { return categoria; }

    @Override
    public String toString() {
        return nome + " (R$ " + preco + ")";
    }
}

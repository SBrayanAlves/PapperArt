package br.com.papperart.model;

import br.com.papperart.exception.EstoqueInsuficienteException;
import br.com.papperart.exception.PagamentoInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Um pedido de um cliente.
 * <p>
 * Enquanto está ABERTO, funciona como o carrinho (RN03: no máximo 1 por cliente,
 * garantido pelo banco). Os itens e os pagamentos só existem dentro do pedido
 * (composição, A7).
 */
public class Pedido implements Precificavel {

    private int id; // gerado pelo banco
    private final Cliente cliente;    // sem setter: o pedido não muda de dono
    private final LocalDateTime data; // sem setter: a data de criação não muda
    private StatusPedido status;
    private final List<ItemPedido> itens = new ArrayList<>();
    private final List<Pagamento> pagamentos = new ArrayList<>();

    /** Abre um pedido novo (o carrinho) pro cliente. */
    public Pedido(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("O pedido precisa de um cliente.");
        }
        this.cliente = cliente;
        this.data = LocalDateTime.now();
        this.status = StatusPedido.ABERTO;
    }

    /** Recria um pedido que já está salvo no banco (usado pelo DAO). */
    public Pedido(int id, Cliente cliente, LocalDateTime data, StatusPedido status,
                  List<ItemPedido> itens, List<Pagamento> pagamentos) {
        if (cliente == null || data == null || status == null) {
            throw new IllegalArgumentException("Dados do pedido incompletos.");
        }
        this.id = id;
        this.cliente = cliente;
        this.data = data;
        this.status = status;
        if (itens != null) {
            this.itens.addAll(itens);
        }
        if (pagamentos != null) {
            this.pagamentos.addAll(pagamentos);
        }
    }

    // ---------- Itens ----------

    /**
     * RN04 e RN06: só pedido ABERTO recebe itens, e nunca mais do que o estoque.
     * Se o produto já estiver no pedido, só aumenta a quantidade.
     */
    public void adicionarItem(Produto produto, int quantidade) throws EstoqueInsuficienteException {
        exigirAberto();
        if (produto == null) {
            throw new IllegalArgumentException("Escolha um produto.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade tem que ser maior que zero.");
        }

        ItemPedido existente = buscarItem(produto);
        int quantidadeTotal = (existente == null) ? quantidade : existente.getQuantidade() + quantidade;
        if (quantidadeTotal > produto.getEstoque()) {
            throw new EstoqueInsuficienteException("Estoque insuficiente de " + produto.getNome()
                    + ": tem " + produto.getEstoque() + ", o pedido ficaria com " + quantidadeTotal + ".");
        }

        if (existente == null) {
            itens.add(new ItemPedido(produto, quantidade)); // o pedido cria o item (composição)
        } else {
            existente.setQuantidade(quantidadeTotal);
        }
    }

    public void removerItem(Produto produto) {
        exigirAberto();
        ItemPedido item = buscarItem(produto);
        if (item != null) {
            itens.remove(item);
        }
    }

    // ---------- Pagamentos ----------

    /** RN06: só pedido ABERTO recebe pagamentos. Cada um já é conferido na hora. */
    public void adicionarPagamento(Pagamento pagamento) throws PagamentoInvalidoException {
        exigirAberto();
        if (pagamento == null) {
            throw new IllegalArgumentException("Informe o pagamento.");
        }
        pagamento.validar();
        pagamentos.add(pagamento);
    }

    // ---------- Mudanças de status ----------

    /**
     * RN07: fecha o pedido. Confere tudo e, só no final, baixa o estoque.
     */
    public void finalizar() throws PagamentoInvalidoException, EstoqueInsuficienteException {
        exigirAberto();
        if (itens.isEmpty()) {
            throw new IllegalStateException("O pedido não tem nenhum item.");
        }

        // Polimorfismo (A4): cada pagamento da lista valida do seu jeito (Pix, cartão ou dinheiro)
        BigDecimal totalPago = BigDecimal.ZERO;
        for (Pagamento pagamento : pagamentos) {
            pagamento.validar();
            totalPago = totalPago.add(pagamento.getValor());
        }
        if (totalPago.compareTo(calcularTotal()) != 0) {
            throw new PagamentoInvalidoException("Os pagamentos somam R$ " + totalPago
                    + ", mas o pedido é de R$ " + calcularTotal() + ".");
        }

        // Confere o estoque de todos os itens antes de baixar qualquer um
        for (ItemPedido item : itens) {
            if (item.getQuantidade() > item.getProduto().getEstoque()) {
                throw new EstoqueInsuficienteException("Estoque insuficiente de "
                        + item.getProduto().getNome() + ".");
            }
        }
        for (ItemPedido item : itens) {
            item.getProduto().baixarEstoque(item.getQuantidade());
        }
        status = StatusPedido.PAGO;
    }

    /** Só pedido PAGO pode ser entregue. */
    public void entregar() {
        if (status != StatusPedido.PAGO) {
            throw new IllegalStateException("Só pedido pago pode ser entregue.");
        }
        status = StatusPedido.ENTREGUE;
    }

    /** RN10: só pedido ABERTO pode ser cancelado. */
    public void cancelar() {
        exigirAberto();
        status = StatusPedido.CANCELADO;
    }

    /** Total do pedido: a soma dos itens. */
    @Override
    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            total = total.add(item.calcularTotal());
        }
        return total;
    }

    // ---------- Auxiliares ----------

    /** RN06: pedido PAGO, ENTREGUE ou CANCELADO não pode mais ser alterado. */
    private void exigirAberto() {
        if (status != StatusPedido.ABERTO) {
            throw new IllegalStateException("O pedido está " + status + " e não pode mais ser alterado.");
        }
    }

    /** Procura o item do produto no pedido (pelo id, ou pelo próprio objeto se ainda não foi salvo). */
    private ItemPedido buscarItem(Produto produto) {
        for (ItemPedido item : itens) {
            Produto p = item.getProduto();
            boolean mesmoProduto = (p == produto) || (p.getId() != 0 && p.getId() == produto.getId());
            if (mesmoProduto) {
                return item;
            }
        }
        return null;
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

    public Cliente getCliente() { return cliente; }

    public LocalDateTime getData() { return data; }

    public StatusPedido getStatus() { return status; }

    /** Lista só de leitura: itens só entram pelo adicionarItem, que confere as regras. */
    public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }

    /** Lista só de leitura: pagamentos só entram pelo adicionarPagamento. */
    public List<Pagamento> getPagamentos() { return Collections.unmodifiableList(pagamentos); }
}

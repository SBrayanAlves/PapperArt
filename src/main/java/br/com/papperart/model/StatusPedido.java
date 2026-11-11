package br.com.papperart.model;

/**
 * Situação de um pedido. O pedido ABERTO funciona como o carrinho do cliente.
 * <p>
 * ABERTO → PAGO → ENTREGUE, ou ABERTO → CANCELADO.
 */
public enum StatusPedido {
    ABERTO,
    PAGO,
    ENTREGUE,
    CANCELADO
}

package br.com.papperart.model;

import java.math.BigDecimal;

/**
 * Contrato de tudo que tem um valor total: um item do pedido e o próprio pedido.
 */
public interface Precificavel {

    BigDecimal calcularTotal();
}

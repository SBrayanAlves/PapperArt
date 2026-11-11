package br.com.papperart.exception;

/**
 * Lançada quando o pedido precisa de mais unidades de um produto do que existem
 * em estoque (RN04).
 * <p>
 * Estende {@code Exception} (e não {@code RuntimeException}) de propósito: assim
 * o Java obriga quem chama, por exemplo a tela, a tratar o erro.
 */
public class EstoqueInsuficienteException extends Exception {

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}

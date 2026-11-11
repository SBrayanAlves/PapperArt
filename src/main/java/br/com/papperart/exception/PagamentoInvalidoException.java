package br.com.papperart.exception;

/**
 * Lançada quando um pagamento quebra uma regra de negócio: débito parcelado,
 * dinheiro que não cobre o valor ou pagamentos que não fecham o total do pedido.
 */
public class PagamentoInvalidoException extends Exception {

    public PagamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}

package br.com.papperart.exception;

/**
 * Lançada quando o login falha: senha errada, usuário que não existe ou
 * funcionário desativado. A mensagem é sempre a mesma de propósito, pra não
 * revelar qual foi o problema (RN15).
 */
public class LoginInvalidoException extends Exception {

    public LoginInvalidoException(String mensagem) {
        super(mensagem);
    }
}

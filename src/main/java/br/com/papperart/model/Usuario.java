package br.com.papperart.model;

import br.com.papperart.exception.LoginInvalidoException;
import br.com.papperart.seguranca.HashSenha;

/**
 * Quem entra no sistema: um cliente (área de compras) ou um funcionário (área de gestão).
 * <p>
 * É abstrata (A5): não existe "usuário genérico". O que cada um pode fazer vem do
 * TIPO da classe, e não de um atributo de permissão que alguém poderia alterar.
 */
public abstract class Usuario {

    private int id; // gerado pelo banco
    private String nome;
    private String email;     // é o login
    private String senhaHash; // A1: a senha em si nunca é guardada, só o hash

    protected Usuario(String nome, String email) {
        setNome(nome);
        setEmail(email);
    }

    // ---------- Senha e login ----------

    /**
     * RN13: senha com pelo menos 8 caracteres, com letras e números.
     * Guarda só o hash (PBKDF2 com salt), nunca a senha.
     */
    public void definirSenha(String senha) {
        if (senha == null || senha.length() < 8
                || !senha.matches(".*[A-Za-z].*") || !senha.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "A senha precisa ter pelo menos 8 caracteres, com letras e números.");
        }
        this.senhaHash = HashSenha.gerar(senha);
    }

    /** Confere a senha digitada com o hash guardado. */
    public boolean conferirSenha(String senha) {
        return senhaHash != null && HashSenha.confere(senha, senhaHash);
    }

    /**
     * RN15: entra no sistema. A mensagem de erro é sempre a mesma, pra não revelar
     * se o problema é a senha ou o usuário.
     */
    public void autenticar(String senha) throws LoginInvalidoException {
        if (!conferirSenha(senha) || !podeEntrar()) {
            throw new LoginInvalidoException("E-mail ou senha inválidos.");
        }
    }

    /** Se este usuário pode entrar no sistema. Por padrão, sim; o Funcionario desativado não (RN14). */
    protected boolean podeEntrar() {
        return true;
    }

    /** Polimorfismo (A4): cada tipo de usuário responde do seu jeito. */
    public abstract boolean podeGerenciarLoja();

    // ---------- Setters que validam a entrada (A1) ----------

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        this.nome = nome.trim();
    }

    /**
     * RN02: e-mail obrigatório e em formato válido (algo@algo.algo, sem espaços).
     * Guardado em minúsculas, pra "Ana@Email.com" e "ana@email.com" contarem como o mesmo.
     * Que ele não se repita é garantido pelo banco ({@code UNIQUE}).
     */
    public void setEmail(String email) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        this.email = email.trim().toLowerCase();
    }

    /** Usado pelo DAO pra carregar o hash que está salvo no banco. */
    public void carregarSenhaHash(String senhaHash) {
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("Hash de senha vazio.");
        }
        this.senhaHash = senhaHash;
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

    public String getEmail() { return email; }

    /** Só o hash, usado pelo DAO pra salvar. A senha não tem getter porque não é guardada. */
    public String getSenhaHash() { return senhaHash; }

    @Override
    public String toString() {
        return nome + " (" + email + ")";
    }
}

package br.com.papperart.model;

/**
 * Funcionário da PapperArt: entra na área de gestão (produtos, clientes e pedidos).
 */
public class Funcionario extends Usuario {

    private String cargo;
    private boolean ativo = true;

    public Funcionario(String nome, String email, String cargo) {
        super(nome, email); // A3: a superclasse cuida do nome e do e-mail
        setCargo(cargo);
    }

    /** Funcionário ativo gerencia a loja. */
    @Override
    public boolean podeGerenciarLoja() {
        return ativo;
    }

    /** RN14: funcionário desativado não entra no sistema. */
    @Override
    protected boolean podeEntrar() {
        return ativo;
    }

    /** RN14: quem sai da loja perde o acesso, sem apagar o histórico. */
    public void desativar() {
        ativo = false;
    }

    public void ativar() {
        ativo = true;
    }

    public void setCargo(String cargo) {
        if (cargo == null || cargo.isBlank()) {
            throw new IllegalArgumentException("O cargo é obrigatório.");
        }
        this.cargo = cargo.trim();
    }

    // ---------- Getters ----------

    public String getCargo() { return cargo; }

    public boolean isAtivo() { return ativo; }
}

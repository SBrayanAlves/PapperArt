package br.com.papperart.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Cliente da PapperArt: entra na área de compras.
 */
public class Cliente extends Usuario {

    private String cpf; // só os números
    private LocalDate dataNascimento;
    private String telefone;

    public Cliente(String nome, String email, String cpf, LocalDate dataNascimento, String telefone) {
        super(nome, email); // A3: a superclasse cuida do nome e do e-mail
        // O construtor passa pelos setters: um cliente nunca nasce inválido (A2)
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setTelefone(telefone);
    }

    /** Cliente usa a área de compras, nunca a de gestão. */
    @Override
    public boolean podeGerenciarLoja() {
        return false;
    }

    // ---------- Setters que validam a entrada (A1) ----------

    /**
     * RN11: CPF válido, conferindo os 2 dígitos verificadores.
     * Que ele não se repita é garantido pelo banco ({@code UNIQUE}).
     */
    public void setCpf(String cpf) {
        String numeros = (cpf == null) ? "" : cpf.replaceAll("\\D", "");
        if (!cpfValido(numeros)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        this.cpf = numeros;
    }

    /** RN12: data de nascimento obrigatória e nunca no futuro. */
    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode ser no futuro.");
        }
        if (dataNascimento.isBefore(LocalDate.now().minusYears(120))) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
        }
        this.dataNascimento = dataNascimento;
    }

    /** RN02: telefone obrigatório, só com números: DDD + número (10 ou 11 dígitos). */
    public void setTelefone(String telefone) {
        String numeros = (telefone == null) ? "" : telefone.replaceAll("\\D", "");
        if (numeros.length() < 10 || numeros.length() > 11) {
            throw new IllegalArgumentException("Telefone inválido: use DDD + número (10 ou 11 dígitos).");
        }
        this.telefone = numeros;
    }

    // ---------- Consultas ----------

    /** LGPD: na tela, o CPF aparece escondido. Ex: ***.982.247-** */
    public String getCpfMascarado() {
        return "***." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-**";
    }

    public int getIdade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    /** Algoritmo oficial dos 2 dígitos verificadores do CPF. */
    private static boolean cpfValido(String cpf) {
        // Precisa ter 11 números e não pode ser tudo igual (111.111.111-11 passa na conta, mas é inválido)
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        // 1º dígito: pesos de 10 a 2 nos 9 primeiros números; 2º dígito: pesos de 11 a 2 nos 10 primeiros
        for (int posicao = 9; posicao <= 10; posicao++) {
            int soma = 0;
            for (int i = 0; i < posicao; i++) {
                soma += (cpf.charAt(i) - '0') * (posicao + 1 - i);
            }
            int resto = (soma * 10) % 11;
            int digitoEsperado = (resto == 10) ? 0 : resto;
            if (cpf.charAt(posicao) - '0' != digitoEsperado) {
                return false;
            }
        }
        return true;
    }

    // ---------- Getters ----------

    /** CPF completo, usado pelo DAO. Na tela, use o getCpfMascarado(). */
    public String getCpf() { return cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }

    public String getTelefone() { return telefone; }
}

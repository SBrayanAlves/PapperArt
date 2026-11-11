package br.com.papperart.seguranca;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Gera e confere hashes de senha com PBKDF2, que já vem no Java (sem biblioteca extra).
 * <p>
 * Hash não é criptografia: criptografia tem chave e pode ser desfeita; hash não volta.
 * Cada senha ganha um "salt" aleatório (duas senhas iguais geram hashes diferentes) e o
 * cálculo é repetido 600 mil vezes de propósito, pra deixar ataques de força bruta lentos.
 * <p>
 * O que vai pro banco tem o formato {@code iteracoes:salt:hash} (salt e hash em Base64).
 */
public final class HashSenha {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 600_000; // recomendação da OWASP pra PBKDF2-SHA256
    private static final int TAMANHO_SALT = 16;   // bytes
    private static final int TAMANHO_HASH = 256;  // bits

    private HashSenha() {
        // Classe utilitária: só tem métodos estáticos, não se cria objeto dela
    }

    /** Gera o hash de uma senha nova, com um salt aleatório. */
    public static String gerar(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = calcular(senha, salt, ITERACOES);
        return ITERACOES + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /** Confere se a senha digitada gera o mesmo hash que está guardado. */
    public static boolean confere(String senha, String guardado) {
        if (senha == null || guardado == null) {
            return false;
        }
        try {
            String[] partes = guardado.split(":");
            if (partes.length != 3) {
                return false;
            }
            int iteracoes = Integer.parseInt(partes[0]);
            byte[] salt = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            byte[] calculado = calcular(senha, salt, iteracoes);
            // Compara em tempo constante: não dá pra descobrir o hash medindo o tempo da resposta
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) { // número ou Base64 inválido no que estava guardado
            return false;
        }
    }

    private static byte[] calcular(String senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, TAMANHO_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível calcular o hash da senha.", e);
        } finally {
            spec.clearPassword(); // apaga a senha da memória do objeto
        }
    }
}

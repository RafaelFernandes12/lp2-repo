package br.com.loja.model;

import br.com.loja.exception.ValidacaoException;

/**
 * Cliente é uma Pessoa que compra na loja. É abstrato porque a política de
 * desconto depende do tipo de cliente (Regular ou VIP) — ponto de polimorfismo.
 */
public abstract class Cliente extends Pessoa {

    private String endereco;

    protected Cliente(int id, String nome, String cpf, String email, String endereco) {
        super(id, nome, cpf, email);
        setEndereco(endereco);
    }

    /**
     * Regra de negócio polimórfica: calcula o desconto (em R$) que este cliente
     * tem direito sobre um determinado valor de compra.
     */
    public abstract double calcularDesconto(double valorCompra);

    @Override
    public String descricaoPapel() {
        return "Cliente";
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        if (endereco == null || endereco.trim().length() < 5) {
            throw new ValidacaoException("Endereço deve ter ao menos 5 caracteres.");
        }
        this.endereco = endereco.trim();
    }
}

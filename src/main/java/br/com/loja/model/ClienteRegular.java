package br.com.loja.model;

/**
 * Cliente comum. Atributo próprio: primeiraCompra.
 * Regra própria: ganha 5% de desconto apenas na primeira compra; depois, 0%.
 */
public class ClienteRegular extends Cliente {

    private boolean primeiraCompra;

    public ClienteRegular(int id, String nome, String cpf, String email, String endereco,
                          boolean primeiraCompra) {
        super(id, nome, cpf, email, endereco);
        this.primeiraCompra = primeiraCompra;
    }

    @Override
    public double calcularDesconto(double valorCompra) {
        return primeiraCompra ? valorCompra * 0.05 : 0.0;
    }

    /** Marca que o cliente já realizou ao menos uma compra (consome o desconto de boas-vindas). */
    public void registrarCompraConcluida() {
        this.primeiraCompra = false;
    }

    @Override
    public String descricaoPapel() {
        return "Cliente Regular" + (primeiraCompra ? " (1ª compra)" : "");
    }

    public boolean isPrimeiraCompra() {
        return primeiraCompra;
    }

    public void setPrimeiraCompra(boolean primeiraCompra) {
        this.primeiraCompra = primeiraCompra;
    }
}

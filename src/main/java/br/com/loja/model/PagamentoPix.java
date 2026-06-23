package br.com.loja.model;

/**
 * Pagamento via Pix. Atributo próprio: chavePix.
 * Regra própria: 5% de desconto à vista por ser liquidação imediata.
 */
public class PagamentoPix extends Pagamento {

    private static final double DESCONTO = 0.05;

    private String chavePix;

    public PagamentoPix(int id, double valorBase, String chavePix) {
        super(id, valorBase);
        this.chavePix = chavePix == null ? "" : chavePix.trim();
    }

    @Override
    public double calcularValorFinal() {
        return getValorBase() * (1 - DESCONTO);
    }

    @Override
    public String forma() {
        return "PIX";
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix == null ? "" : chavePix.trim();
    }
}

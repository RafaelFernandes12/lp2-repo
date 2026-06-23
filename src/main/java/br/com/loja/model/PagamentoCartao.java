package br.com.loja.model;

import br.com.loja.exception.ValidacaoException;

/**
 * Pagamento via cartão de crédito. Atributo próprio: parcelas.
 * Regra própria: à vista (1x) sem juros; parcelado aplica 2% de juros por parcela
 * acima da primeira.
 */
public class PagamentoCartao extends Pagamento {

    private static final double JUROS_POR_PARCELA = 0.02;

    private int parcelas;

    public PagamentoCartao(int id, double valorBase, int parcelas) {
        super(id, valorBase);
        setParcelas(parcelas);
    }

    @Override
    public double calcularValorFinal() {
        int parcelasComJuros = Math.max(0, parcelas - 1);
        return getValorBase() * (1 + JUROS_POR_PARCELA * parcelasComJuros);
    }

    @Override
    public String forma() {
        return "CARTAO";
    }

    /** Valor de cada parcela na fatura do cartão. */
    public double valorParcela() {
        return calcularValorFinal() / parcelas;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        if (parcelas < 1 || parcelas > 12) {
            throw new ValidacaoException("Parcelas deve estar entre 1 e 12.");
        }
        this.parcelas = parcelas;
    }
}

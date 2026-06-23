package br.com.loja.model;

/**
 * Cliente VIP. Atributo próprio: pontosFidelidade.
 * Regra própria: 10% de desconto sempre, e acumula 1 ponto a cada R$ 10 gastos.
 */
public class ClienteVip extends Cliente {

    private int pontosFidelidade;

    public ClienteVip(int id, String nome, String cpf, String email, String endereco,
                      int pontosFidelidade) {
        super(id, nome, cpf, email, endereco);
        this.pontosFidelidade = Math.max(0, pontosFidelidade);
    }

    @Override
    public double calcularDesconto(double valorCompra) {
        return valorCompra * 0.10;
    }

    /** Acumula pontos de fidelidade conforme o valor efetivamente pago. */
    public void acumularPontos(double valorPago) {
        this.pontosFidelidade += (int) (valorPago / 10.0);
    }

    @Override
    public String descricaoPapel() {
        return "Cliente VIP (" + pontosFidelidade + " pts)";
    }

    public int getPontosFidelidade() {
        return pontosFidelidade;
    }

    public void setPontosFidelidade(int pontosFidelidade) {
        this.pontosFidelidade = Math.max(0, pontosFidelidade);
    }
}

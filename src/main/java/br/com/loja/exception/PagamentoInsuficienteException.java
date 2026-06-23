package br.com.loja.exception;

/**
 * Lançada quando o valor pago não cobre o total devido do pedido
 * (itens + frete - desconto).
 */
public class PagamentoInsuficienteException extends RuntimeException {

    public PagamentoInsuficienteException(double pago, double devido) {
        super(String.format("Pagamento insuficiente: pago R$ %.2f, devido R$ %.2f.", pago, devido));
    }
}

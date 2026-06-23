package br.com.loja.exception;

import br.com.loja.model.SituacaoPedido;

/**
 * Lançada quando se tenta mudar o estado de um pedido para uma situação
 * não permitida pela máquina de estados (ex.: ACEITO -> ENTREGUE direto).
 */
public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(SituacaoPedido atual, SituacaoPedido destino) {
        super("Transição inválida de " + atual + " para " + destino + ".");
    }

    public TransicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}

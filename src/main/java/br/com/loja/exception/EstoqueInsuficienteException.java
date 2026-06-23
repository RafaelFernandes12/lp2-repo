package br.com.loja.exception;

/**
 * Lançada quando se tenta movimentar uma quantidade maior do que a disponível
 * em estoque de um produto físico.
 */
public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String produto, int solicitado, int disponivel) {
        super("Estoque insuficiente para '" + produto + "': solicitado "
                + solicitado + ", disponível " + disponivel + ".");
    }
}

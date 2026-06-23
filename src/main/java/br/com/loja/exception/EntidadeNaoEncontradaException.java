package br.com.loja.exception;

/**
 * Lançada quando uma busca por id (cliente, produto, pedido) não retorna resultado.
 */
public class EntidadeNaoEncontradaException extends RuntimeException {

    public EntidadeNaoEncontradaException(String entidade, int id) {
        super(entidade + " com id " + id + " não encontrado(a).");
    }
}

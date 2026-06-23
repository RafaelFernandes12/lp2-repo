package br.com.loja.exception;

/**
 * Lançada quando dados informados pelo usuário falham nas validações de domínio
 * (ex.: CPF inválido, e-mail mal formado, preço/estoque negativos).
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}

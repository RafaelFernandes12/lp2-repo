package br.com.loja.model;

import br.com.loja.exception.ValidacaoException;

/**
 * Classe base abstrata para qualquer pessoa do sistema.
 * Concentra os dados e validações comuns (nome, cpf, email) e
 * obriga as subclasses a se identificarem por um papel (descricaoPapel).
 */
public abstract class Pessoa {

    private int id;
    private String nome;
    private String cpf;
    private String email;

    protected Pessoa(int id, String nome, String cpf, String email) {
        this.id = id;
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
    }

    /** Comportamento abstrato: cada tipo de pessoa descreve seu papel no sistema. */
    public abstract String descricaoPapel();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().length() < 2) {
            throw new ValidacaoException("Nome deve ter ao menos 2 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        String somenteDigitos = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (somenteDigitos.length() != 11) {
            throw new ValidacaoException("CPF deve conter 11 dígitos.");
        }
        this.cpf = somenteDigitos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ValidacaoException("E-mail inválido: " + email);
        }
        this.email = email.trim();
    }

    @Override
    public String toString() {
        return String.format("#%d %s (%s) - %s", id, nome, descricaoPapel(), email);
    }
}

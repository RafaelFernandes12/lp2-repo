package br.com.loja.model;

import br.com.loja.exception.EstoqueInsuficienteException;
import br.com.loja.exception.ValidacaoException;

/**
 * Produto abstrato vendido pela loja. O cálculo de frete varia conforme o tipo
 * concreto (físico x digital) — ponto de polimorfismo. Também controla estoque.
 */
public abstract class Produto {

    private int id;
    private String nome;
    private double precoBase;
    private int estoque;

    protected Produto(int id, String nome, double precoBase, int estoque) {
        this.id = id;
        setNome(nome);
        setPrecoBase(precoBase);
        setEstoque(estoque);
    }

    /** Regra de negócio polimórfica: frete depende do tipo concreto de produto. */
    public abstract double calcularFrete();

    /** Identifica o tipo para persistência e exibição. */
    public abstract String tipo();

    /** Baixa do estoque; lança exceção se não houver quantidade suficiente. */
    public void baixarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new ValidacaoException("Quantidade para baixa deve ser positiva.");
        }
        if (quantidade > estoque) {
            throw new EstoqueInsuficienteException(nome, quantidade, estoque);
        }
        estoque -= quantidade;
    }

    public void reporEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new ValidacaoException("Quantidade para reposição deve ser positiva.");
        }
        estoque += quantidade;
    }

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
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome do produto é obrigatório.");
        }
        this.nome = nome.trim();
    }

    public double getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(double precoBase) {
        if (precoBase < 0) {
            throw new ValidacaoException("Preço não pode ser negativo.");
        }
        this.precoBase = precoBase;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        if (estoque < 0) {
            throw new ValidacaoException("Estoque não pode ser negativo.");
        }
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return String.format("#%d %s [%s] R$ %.2f | estoque: %d | frete: R$ %.2f",
                id, nome, tipo(), precoBase, estoque, calcularFrete());
    }
}

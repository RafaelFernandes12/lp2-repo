package br.com.loja.model;

import br.com.loja.exception.ValidacaoException;

/**
 * Item de um pedido: associa um Produto a uma quantidade.
 * O subtotal combina preço do produto e o frete unitário (interação entre classes).
 */
public class ItemPedido {

    private int id;
    private Produto produto;
    private int quantidade;

    public ItemPedido(int id, Produto produto, int quantidade) {
        this.id = id;
        if (produto == null) {
            throw new ValidacaoException("Item precisa de um produto.");
        }
        this.produto = produto;
        setQuantidade(quantidade);
    }

    /** Subtotal = (preço base + frete) * quantidade. */
    public double subtotal() {
        return (produto.getPrecoBase() + produto.calcularFrete()) * quantidade;
    }

    public double freteTotal() {
        return produto.calcularFrete() * quantidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new ValidacaoException("Quantidade do item deve ser positiva.");
        }
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return String.format("%dx %s = R$ %.2f", quantidade, produto.getNome(), subtotal());
    }
}

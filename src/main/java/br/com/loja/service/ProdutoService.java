package br.com.loja.service;

import br.com.loja.exception.EntidadeNaoEncontradaException;
import br.com.loja.model.Produto;
import br.com.loja.persistence.ProdutoDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de serviço de produtos: cache em memória + sincronização com o banco.
 */
public class ProdutoService {

    private final ProdutoDAO dao = new ProdutoDAO();
    private final List<Produto> produtos = new ArrayList<>();

    public void carregar() {
        produtos.clear();
        produtos.addAll(dao.listarTodos());
    }

    public Produto cadastrar(Produto produto) {
        dao.inserir(produto);
        produtos.add(produto);
        return produto;
    }

    public void alterar(Produto produto) {
        buscarPorId(produto.getId());
        dao.atualizar(produto);
    }

    public void remover(int id) {
        Produto p = buscarPorId(id);
        dao.remover(id);
        produtos.remove(p);
    }

    public Produto buscarPorId(int id) {
        return produtos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto", id));
    }

    public List<Produto> listar() {
        return List.copyOf(produtos);
    }

    /** Persiste o estado atual do produto (ex.: estoque após baixa/reposição). */
    public void persistir(Produto produto) {
        dao.atualizar(produto);
    }
}

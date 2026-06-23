package br.com.loja.service;

import br.com.loja.exception.EntidadeNaoEncontradaException;
import br.com.loja.model.Cliente;
import br.com.loja.persistence.ClienteDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de serviço de clientes: mantém os clientes em memória (carregados na
 * inicialização) e sincroniza cada alteração com o banco via ClienteDAO.
 */
public class ClienteService {

    private final ClienteDAO dao = new ClienteDAO();
    private final List<Cliente> clientes = new ArrayList<>();

    /** Carrega os clientes persistidos para a memória (chamado no startup). */
    public void carregar() {
        clientes.clear();
        clientes.addAll(dao.listarTodos());
    }

    public Cliente cadastrar(Cliente cliente) {
        dao.inserir(cliente); // validações ocorrem no construtor da entidade
        clientes.add(cliente);
        return cliente;
    }

    public void alterar(Cliente cliente) {
        buscarPorId(cliente.getId()); // garante existência
        dao.atualizar(cliente);
    }

    public void remover(int id) {
        Cliente c = buscarPorId(id);
        dao.remover(id);
        clientes.remove(c);
    }

    public Cliente buscarPorId(int id) {
        return clientes.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente", id));
    }

    public List<Cliente> listar() {
        return List.copyOf(clientes);
    }

    /** Persiste alterações feitas no cliente por outras operações (ex.: pontos VIP). */
    public void persistir(Cliente cliente) {
        dao.atualizar(cliente);
    }
}

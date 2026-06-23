package br.com.loja.service;

import br.com.loja.exception.EntidadeNaoEncontradaException;
import br.com.loja.model.Cliente;
import br.com.loja.model.ClienteRegular;
import br.com.loja.model.ClienteVip;
import br.com.loja.model.ItemPedido;
import br.com.loja.model.Pagamento;
import br.com.loja.model.Pedido;
import br.com.loja.model.Produto;
import br.com.loja.persistence.PedidoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço que orquestra o ciclo de vida do pedido. É aqui que ocorre a interação
 * entre múltiplas classes e regras de negócio: ao pagar, o pedido baixa estoque
 * (ProdutoService), atualiza fidelidade do cliente (ClienteService) e persiste a
 * mudança de estado. Depende dos demais serviços para manter tudo sincronizado.
 */
public class PedidoService {

    private final PedidoDAO dao = new PedidoDAO();
    private final List<Pedido> pedidos = new ArrayList<>();
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(ClienteService clienteService, ProdutoService produtoService) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    /** Carrega os pedidos persistidos, religando clientes e produtos já em memória. */
    public void carregar() {
        pedidos.clear();
        var clientes = clienteService.listar().stream()
                .collect(Collectors.toMap(Cliente::getId, c -> c));
        var produtos = produtoService.listar().stream()
                .collect(Collectors.toMap(Produto::getId, p -> p));
        pedidos.addAll(dao.listarTodos(clientes, produtos));
    }

    public Pedido criar(int clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        Pedido pedido = new Pedido(0, cliente);
        dao.inserir(pedido);
        pedidos.add(pedido);
        return pedido;
    }

    public void adicionarItem(int pedidoId, int produtoId, int quantidade) {
        Pedido pedido = buscarPorId(pedidoId);
        Produto produto = produtoService.buscarPorId(produtoId);
        pedido.adicionarItem(new ItemPedido(0, produto, quantidade));
        dao.atualizar(pedido);
    }

    public void removerItem(int pedidoId, int itemId) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.removerItem(itemId);
        dao.atualizar(pedido);
    }

    /**
     * Confirma o pagamento. Esta operação cruza várias classes: Pedido valida a
     * transição e o valor pago, baixa o estoque dos Produtos, acumula pontos do
     * ClienteVip / consome desconto do ClienteRegular, e tudo é persistido.
     */
    public void pagar(int pedidoId, Pagamento pagamento) {
        Pedido pedido = buscarPorId(pedidoId);
        double valorPago = pedido.totalAPagar();

        pedido.pagar(pagamento); // pode lançar PagamentoInsuficiente/Transicao/Estoque

        // estoque dos produtos mudou -> persistir cada um
        pedido.getItens().forEach(i -> produtoService.persistir(i.getProduto()));

        // fidelidade do cliente (comportamento específico por tipo)
        Cliente cliente = pedido.getCliente();
        if (cliente instanceof ClienteVip vip) {
            vip.acumularPontos(valorPago);
        } else if (cliente instanceof ClienteRegular reg) {
            reg.registrarCompraConcluida();
        }
        clienteService.persistir(cliente);

        dao.atualizar(pedido);
    }

    public void enviar(int pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.enviar();
        dao.atualizar(pedido);
    }

    public void entregar(int pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.entregar();
        dao.atualizar(pedido);
    }

    public void cancelar(int pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.cancelar();
        // cancelamento pode ter devolvido estoque -> persistir produtos
        pedido.getItens().forEach(i -> produtoService.persistir(i.getProduto()));
        dao.atualizar(pedido);
    }

    public Pedido buscarPorId(int id) {
        return pedidos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pedido", id));
    }

    public List<Pedido> listar() {
        return List.copyOf(pedidos);
    }
}

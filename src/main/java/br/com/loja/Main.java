package br.com.loja;

import br.com.loja.persistence.ConexaoSQLite;
import br.com.loja.service.ClienteService;
import br.com.loja.service.PedidoService;
import br.com.loja.service.ProdutoService;
import br.com.loja.ui.MenuPrincipal;

/**
 * Ponto de entrada. Inicializa o banco, carrega os dados persistidos para a
 * memória (requisito: ao iniciar, carregar as informações salvas) e abre o menu.
 */
public class Main {

    public static void main(String[] args) {
        ConexaoSQLite.inicializar();

        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();
        PedidoService pedidoService = new PedidoService(clienteService, produtoService);

        // ordem importa: pedidos dependem de clientes e produtos já carregados
        clienteService.carregar();
        produtoService.carregar();
        pedidoService.carregar();

        new MenuPrincipal(clienteService, produtoService, pedidoService).iniciar();
    }
}

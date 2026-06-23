package br.com.loja.ui;

import br.com.loja.model.Cliente;
import br.com.loja.model.ClienteRegular;
import br.com.loja.model.ClienteVip;
import br.com.loja.model.Pagamento;
import br.com.loja.model.PagamentoCartao;
import br.com.loja.model.PagamentoPix;
import br.com.loja.model.Pedido;
import br.com.loja.model.Produto;
import br.com.loja.model.ProdutoDigital;
import br.com.loja.model.ProdutoFisico;
import br.com.loja.service.ClienteService;
import br.com.loja.service.PedidoService;
import br.com.loja.service.ProdutoService;

import java.util.Scanner;

/**
 * Interface de terminal. Apresenta menus e delega às camadas de serviço.
 * Captura exceções de domínio para exibir mensagens amigáveis sem derrubar o app.
 */
public class MenuPrincipal {

    private final Scanner sc = new Scanner(System.in);
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public MenuPrincipal(ClienteService clienteService, ProdutoService produtoService,
                         PedidoService pedidoService) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    public void iniciar() {
        System.out.println("=== Loja - Sistema de Pedidos (LP2) ===");
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1) Clientes");
            System.out.println("2) Produtos");
            System.out.println("3) Pedidos");
            System.out.println("0) Sair");
            switch (lerInt("Opção")) {
                case 1 -> menuClientes();
                case 2 -> menuProdutos();
                case 3 -> menuPedidos();
                case 0 -> rodando = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Até logo!");
    }

    // ===================== CLIENTES =====================

    private void menuClientes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1) Listar  2) Cadastrar  3) Alterar  4) Remover  0) Voltar");
            switch (lerInt("Opção")) {
                case 1 -> mostrarClientes();
                case 2 -> cadastrarCliente();
                case 3 -> alterarCliente();
                case 4 -> executar(() -> {
                    if (!mostrarClientes()) return;
                    clienteService.remover(lerInt("Id do cliente"));
                    System.out.println("Removido.");
                });
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarCliente() {
        executar(() -> {
            System.out.println("Tipo: 1) Regular  2) VIP");
            int tipo = lerInt("Tipo");
            String nome = lerTexto("Nome");
            String cpf = lerTexto("CPF (11 dígitos)");
            String email = lerTexto("E-mail");
            String endereco = lerTexto("Endereço");
            Cliente c;
            if (tipo == 2) {
                c = new ClienteVip(0, nome, cpf, email, endereco, lerInt("Pontos iniciais"));
            } else {
                c = new ClienteRegular(0, nome, cpf, email, endereco, true);
            }
            clienteService.cadastrar(c);
            System.out.println("Cadastrado: " + c);
        });
    }

    private void alterarCliente() {
        executar(() -> {
            if (!mostrarClientes()) return;
            Cliente c = clienteService.buscarPorId(lerInt("Id do cliente"));
            c.setNome(lerTexto("Novo nome (" + c.getNome() + ")"));
            c.setEmail(lerTexto("Novo e-mail (" + c.getEmail() + ")"));
            c.setEndereco(lerTexto("Novo endereço"));
            clienteService.alterar(c);
            System.out.println("Atualizado: " + c);
        });
    }

    // ===================== PRODUTOS =====================

    private void menuProdutos() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- PRODUTOS ---");
            System.out.println("1) Listar  2) Cadastrar  3) Alterar  4) Remover  0) Voltar");
            switch (lerInt("Opção")) {
                case 1 -> mostrarProdutos();
                case 2 -> cadastrarProduto();
                case 3 -> alterarProduto();
                case 4 -> executar(() -> {
                    if (!mostrarProdutos()) return;
                    produtoService.remover(lerInt("Id do produto"));
                    System.out.println("Removido.");
                });
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarProduto() {
        executar(() -> {
            System.out.println("Tipo: 1) Físico  2) Digital");
            int tipo = lerInt("Tipo");
            String nome = lerTexto("Nome");
            double preco = lerDouble("Preço base");
            int estoque = lerInt("Estoque");
            Produto p;
            if (tipo == 2) {
                p = new ProdutoDigital(0, nome, preco, estoque,
                        lerDouble("Tamanho (MB)"), lerTexto("URL de download"));
            } else {
                p = new ProdutoFisico(0, nome, preco, estoque, lerDouble("Peso (kg)"));
            }
            produtoService.cadastrar(p);
            System.out.println("Cadastrado: " + p);
        });
    }

    private void alterarProduto() {
        executar(() -> {
            if (!mostrarProdutos()) return;
            Produto p = produtoService.buscarPorId(lerInt("Id do produto"));
            p.setNome(lerTexto("Novo nome (" + p.getNome() + ")"));
            p.setPrecoBase(lerDouble("Novo preço (" + p.getPrecoBase() + ")"));
            p.setEstoque(lerInt("Novo estoque (" + p.getEstoque() + ")"));
            produtoService.alterar(p);
            System.out.println("Atualizado: " + p);
        });
    }

    // ===================== PEDIDOS =====================

    private void menuPedidos() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- PEDIDOS ---");
            System.out.println("1) Listar  2) Criar  3) Adicionar item  4) Detalhar");
            System.out.println("5) Pagar  6) Enviar  7) Entregar  8) Cancelar  0) Voltar");
            switch (lerInt("Opção")) {
                case 1 -> mostrarPedidos();
                case 2 -> executar(() -> {
                    if (!mostrarClientes()) return;
                    Pedido p = pedidoService.criar(lerInt("Id do cliente"));
                    System.out.println("Criado: " + p);
                });
                case 3 -> executar(() -> {
                    if (!mostrarPedidos()) return;
                    int pid = lerInt("Id do pedido");
                    System.out.println("Produtos disponíveis:");
                    if (!mostrarProdutos()) return;
                    int prod = lerInt("Id do produto");
                    int qtd = lerInt("Quantidade");
                    pedidoService.adicionarItem(pid, prod, qtd);
                    System.out.println("Item adicionado.");
                });
                case 4 -> detalharPedido();
                case 5 -> pagarPedido();
                case 6 -> executar(() -> {
                    if (!mostrarPedidos()) return;
                    pedidoService.enviar(lerInt("Id do pedido"));
                    System.out.println("Pedido enviado.");
                });
                case 7 -> executar(() -> {
                    if (!mostrarPedidos()) return;
                    pedidoService.entregar(lerInt("Id do pedido"));
                    System.out.println("Pedido entregue.");
                });
                case 8 -> executar(() -> {
                    if (!mostrarPedidos()) return;
                    pedidoService.cancelar(lerInt("Id do pedido"));
                    System.out.println("Pedido cancelado.");
                });
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void detalharPedido() {
        executar(() -> {
            if (!mostrarPedidos()) return;
            Pedido p = pedidoService.buscarPorId(lerInt("Id do pedido"));
            System.out.println(p);
            System.out.println("Itens:");
            p.getItens().forEach(i -> System.out.println("  - " + i));
            System.out.printf("Total bruto: R$ %.2f%n", p.totalBruto());
            System.out.printf("Frete total: R$ %.2f%n", p.freteTotal());
            System.out.printf("Desconto cliente: R$ %.2f%n", p.descontoCliente());
            System.out.printf("Total a pagar: R$ %.2f%n", p.totalAPagar());
            if (p.getPagamento() != null) {
                System.out.println("Pagamento: " + p.getPagamento());
            }
        });
    }

    private void pagarPedido() {
        executar(() -> {
            if (!mostrarPedidos()) return;
            int pid = lerInt("Id do pedido");
            Pedido p = pedidoService.buscarPorId(pid);
            double devido = p.totalAPagar();
            System.out.printf("Total a pagar (devido): R$ %.2f%n", devido);
            double base = lerDouble("Valor a pagar (base; deve cobrir o devido)");
            System.out.println("Forma: 1) Pix (5%% desconto)  2) Cartão (juros se parcelado)");
            Pagamento pagamento;
            if (lerInt("Forma") == 1) {
                pagamento = new PagamentoPix(0, base, lerTexto("Chave Pix"));
            } else {
                pagamento = new PagamentoCartao(0, base, lerInt("Parcelas (1-12)"));
            }
            System.out.printf("Valor efetivo na forma escolhida: R$ %.2f%n",
                    pagamento.calcularValorFinal());
            pedidoService.pagar(pid, pagamento);
            System.out.println("Pagamento confirmado. Pedido PAGO.");
        });
    }

    // ===================== LISTAGENS =====================

    /** Lista os clientes existentes. Retorna false (e avisa) se não houver nenhum. */
    private boolean mostrarClientes() {
        var lista = clienteService.listar();
        if (lista.isEmpty()) {
            System.out.println("(nenhum cliente cadastrado)");
            return false;
        }
        System.out.println("Clientes:");
        lista.forEach(c -> System.out.println("  " + c));
        return true;
    }

    /** Lista os produtos existentes. Retorna false (e avisa) se não houver nenhum. */
    private boolean mostrarProdutos() {
        var lista = produtoService.listar();
        if (lista.isEmpty()) {
            System.out.println("(nenhum produto cadastrado)");
            return false;
        }
        System.out.println("Produtos:");
        lista.forEach(p -> System.out.println("  " + p));
        return true;
    }

    /** Lista os pedidos existentes. Retorna false (e avisa) se não houver nenhum. */
    private boolean mostrarPedidos() {
        var lista = pedidoService.listar();
        if (lista.isEmpty()) {
            System.out.println("(nenhum pedido cadastrado)");
            return false;
        }
        System.out.println("Pedidos:");
        lista.forEach(p -> System.out.println("  " + p));
        return true;
    }

    // ===================== HELPERS =====================

    /** Executa uma ação capturando exceções de domínio para não derrubar o menu. */
    private void executar(Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException e) {
            System.out.println(">> Erro: " + e.getMessage());
        }
    }

    private int lerInt(String rotulo) {
        while (true) {
            System.out.print(rotulo + ": ");
            String linha = sc.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private double lerDouble(String rotulo) {
        while (true) {
            System.out.print(rotulo + ": ");
            String linha = sc.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private String lerTexto(String rotulo) {
        System.out.print(rotulo + ": ");
        return sc.nextLine().trim();
    }
}

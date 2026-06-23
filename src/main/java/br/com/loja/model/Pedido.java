package br.com.loja.model;

import br.com.loja.exception.PagamentoInsuficienteException;
import br.com.loja.exception.TransicaoInvalidaException;
import br.com.loja.exception.ValidacaoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pedido é a classe com ESTADO DINÂMICO do sistema. Mantém uma lista de itens,
 * referência ao cliente e ao pagamento, e controla as transições de situação
 * (ACEITO -> PAGO -> ENVIADO -> ENTREGUE, com CANCELADO como ramo).
 *
 * Concentra várias regras de negócio que envolvem múltiplas classes:
 * cálculo de total (itens + frete), desconto do cliente (polimórfico),
 * verificação do valor pago (pagamento polimórfico) e baixa de estoque.
 */
public class Pedido {

    private int id;
    private Cliente cliente;
    private final List<ItemPedido> itens = new ArrayList<>();
    private SituacaoPedido situacao;
    private Pagamento pagamento; // definido ao pagar

    public Pedido(int id, Cliente cliente) {
        this.id = id;
        if (cliente == null) {
            throw new ValidacaoException("Pedido precisa de um cliente.");
        }
        this.cliente = cliente;
        this.situacao = SituacaoPedido.ACEITO;
    }

    // ----- montagem do pedido -----

    public void adicionarItem(ItemPedido item) {
        exigirEstado(SituacaoPedido.ACEITO, "adicionar itens");
        if (item == null) {
            throw new ValidacaoException("Item inválido.");
        }
        itens.add(item);
    }

    public void removerItem(int itemId) {
        exigirEstado(SituacaoPedido.ACEITO, "remover itens");
        itens.removeIf(i -> i.getId() == itemId);
    }

    // ----- cálculos (regras envolvendo múltiplas classes) -----

    /** Soma dos subtotais (preço + frete) de todos os itens. */
    public double totalBruto() {
        return itens.stream().mapToDouble(ItemPedido::subtotal).sum();
    }

    public double freteTotal() {
        return itens.stream().mapToDouble(ItemPedido::freteTotal).sum();
    }

    /** Desconto concedido pelo tipo de cliente (polimorfismo) sobre o total bruto. */
    public double descontoCliente() {
        return cliente.calcularDesconto(totalBruto());
    }

    /** Valor que o cliente precisa pagar: total bruto menos o desconto do cliente. */
    public double totalAPagar() {
        return totalBruto() - descontoCliente();
    }

    // ----- máquina de estados -----

    /**
     * Paga o pedido. Regras: precisa ter itens; o pagamento (após sua própria
     * regra de juros/desconto) deve cobrir o total a pagar; baixa o estoque dos
     * produtos e evolui o estado ACEITO -> PAGO.
     */
    public void pagar(Pagamento pagamento) {
        exigirTransicao(SituacaoPedido.PAGO);
        if (itens.isEmpty()) {
            throw new ValidacaoException("Não é possível pagar um pedido sem itens.");
        }
        if (pagamento == null) {
            throw new ValidacaoException("Forma de pagamento obrigatória.");
        }
        double devido = totalAPagar();
        // O valor base ofertado deve cobrir o total devido. A forma de pagamento
        // ainda aplica seu próprio ajuste (Pix desconta, Cartão acresce juros)
        // sobre essa base para chegar ao valor efetivamente cobrado.
        double base = pagamento.getValorBase();
        // tolerância de centavos para evitar problemas de ponto flutuante
        if (base + 0.001 < devido) {
            throw new PagamentoInsuficienteException(base, devido);
        }
        for (ItemPedido item : itens) {
            item.getProduto().baixarEstoque(item.getQuantidade());
        }
        this.pagamento = pagamento;
        this.situacao = SituacaoPedido.PAGO;
    }

    public void enviar() {
        exigirTransicao(SituacaoPedido.ENVIADO);
        this.situacao = SituacaoPedido.ENVIADO;
    }

    public void entregar() {
        exigirTransicao(SituacaoPedido.ENTREGUE);
        this.situacao = SituacaoPedido.ENTREGUE;
    }

    /** Cancela o pedido; só permitido antes do envio (regra da máquina de estados). */
    public void cancelar() {
        exigirTransicao(SituacaoPedido.CANCELADO);
        // se já estava pago, devolve o estoque reservado
        if (situacao == SituacaoPedido.PAGO) {
            for (ItemPedido item : itens) {
                item.getProduto().reporEstoque(item.getQuantidade());
            }
        }
        this.situacao = SituacaoPedido.CANCELADO;
    }

    private void exigirTransicao(SituacaoPedido destino) {
        if (!situacao.podeIrPara(destino)) {
            throw new TransicaoInvalidaException(situacao, destino);
        }
    }

    private void exigirEstado(SituacaoPedido esperado, String acao) {
        if (situacao != esperado) {
            throw new TransicaoInvalidaException(
                    "Não é possível " + acao + " com o pedido em " + situacao + ".");
        }
    }

    // ----- getters/setters -----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public SituacaoPedido getSituacao() {
        return situacao;
    }

    /** Usado pela camada de persistência para restaurar o estado salvo. */
    public void restaurarSituacao(SituacaoPedido situacao) {
        this.situacao = situacao;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    @Override
    public String toString() {
        return String.format("Pedido #%d | %s | %s | itens: %d | total a pagar: R$ %.2f",
                id, cliente.getNome(), situacao, itens.size(), totalAPagar());
    }
}

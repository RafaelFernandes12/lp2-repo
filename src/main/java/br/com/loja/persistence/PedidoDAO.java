package br.com.loja.persistence;

import br.com.loja.model.Cliente;
import br.com.loja.model.ItemPedido;
import br.com.loja.model.Pagamento;
import br.com.loja.model.PagamentoCartao;
import br.com.loja.model.PagamentoPix;
import br.com.loja.model.Pedido;
import br.com.loja.model.Produto;
import br.com.loja.model.SituacaoPedido;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO de Pedido. Persiste o pedido, seus itens (tabela item_pedido) e os dados
 * do pagamento desnormalizados na própria linha do pedido. Na leitura, reusa os
 * mapas de clientes e produtos já carregados em memória para religar as referências.
 */
public class PedidoDAO {

    public Pedido inserir(Pedido p) {
        String sql = "INSERT INTO pedido(cliente_id,situacao) VALUES (?,?)";
        try (PreparedStatement ps = ConexaoSQLite.get()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getCliente().getId());
            ps.setString(2, p.getSituacao().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
            salvarItens(p);
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir pedido: " + e.getMessage(), e);
        }
    }

    /** Atualiza situação + pagamento e regrava os itens. */
    public void atualizar(Pedido p) {
        String sql = "UPDATE pedido SET situacao=?,pgto_forma=?,pgto_valor_base=?,"
                + "pgto_parcelas=?,pgto_chave=? WHERE id=?";
        try (PreparedStatement ps = ConexaoSQLite.get().prepareStatement(sql)) {
            ps.setString(1, p.getSituacao().name());
            Pagamento pg = p.getPagamento();
            if (pg == null) {
                ps.setObject(2, null);
                ps.setObject(3, null);
                ps.setObject(4, null);
                ps.setObject(5, null);
            } else {
                ps.setString(2, pg.forma());
                ps.setDouble(3, pg.getValorBase());
                if (pg instanceof PagamentoCartao cartao) {
                    ps.setInt(4, cartao.getParcelas());
                    ps.setObject(5, null);
                } else if (pg instanceof PagamentoPix pix) {
                    ps.setObject(4, null);
                    ps.setString(5, pix.getChavePix());
                }
            }
            ps.setInt(6, p.getId());
            ps.executeUpdate();
            salvarItens(p);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido: " + e.getMessage(), e);
        }
    }

    public void remover(int id) {
        try {
            try (PreparedStatement ps = ConexaoSQLite.get()
                    .prepareStatement("DELETE FROM item_pedido WHERE pedido_id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = ConexaoSQLite.get()
                    .prepareStatement("DELETE FROM pedido WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover pedido: " + e.getMessage(), e);
        }
    }

    private void salvarItens(Pedido p) throws SQLException {
        try (PreparedStatement del = ConexaoSQLite.get()
                .prepareStatement("DELETE FROM item_pedido WHERE pedido_id=?")) {
            del.setInt(1, p.getId());
            del.executeUpdate();
        }
        String sql = "INSERT INTO item_pedido(pedido_id,produto_id,quantidade) VALUES (?,?,?)";
        try (PreparedStatement ps = ConexaoSQLite.get().prepareStatement(sql)) {
            for (ItemPedido item : p.getItens()) {
                ps.setInt(1, p.getId());
                ps.setInt(2, item.getProduto().getId());
                ps.setInt(3, item.getQuantidade());
                ps.executeUpdate();
            }
        }
    }

    public List<Pedido> listarTodos(Map<Integer, Cliente> clientes, Map<Integer, Produto> produtos) {
        List<Pedido> pedidos = new ArrayList<>();
        try (Statement st = ConexaoSQLite.get().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM pedido ORDER BY id")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Cliente cliente = clientes.get(rs.getInt("cliente_id"));
                if (cliente == null) {
                    continue; // cliente removido; ignora pedido órfão
                }
                Pedido pedido = new Pedido(id, cliente);
                carregarItens(pedido, produtos);
                restaurarPagamento(pedido, rs);
                pedido.restaurarSituacao(SituacaoPedido.valueOf(rs.getString("situacao")));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return pedidos;
    }

    private void carregarItens(Pedido pedido, Map<Integer, Produto> produtos) throws SQLException {
        String sql = "SELECT * FROM item_pedido WHERE pedido_id=? ORDER BY id";
        try (PreparedStatement ps = ConexaoSQLite.get().prepareStatement(sql)) {
            ps.setInt(1, pedido.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto produto = produtos.get(rs.getInt("produto_id"));
                    if (produto != null) {
                        // adiciona direto enquanto o pedido ainda está em ACEITO (estado inicial)
                        pedido.adicionarItem(new ItemPedido(
                                rs.getInt("id"), produto, rs.getInt("quantidade")));
                    }
                }
            }
        }
    }

    private void restaurarPagamento(Pedido pedido, ResultSet rs) throws SQLException {
        String forma = rs.getString("pgto_forma");
        if (forma == null) {
            return;
        }
        double base = rs.getDouble("pgto_valor_base");
        if ("CARTAO".equals(forma)) {
            pedido.setPagamento(new PagamentoCartao(0, base, rs.getInt("pgto_parcelas")));
        } else if ("PIX".equals(forma)) {
            pedido.setPagamento(new PagamentoPix(0, base, rs.getString("pgto_chave")));
        }
    }
}

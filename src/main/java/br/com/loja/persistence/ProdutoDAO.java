package br.com.loja.persistence;

import br.com.loja.model.Produto;
import br.com.loja.model.ProdutoDigital;
import br.com.loja.model.ProdutoFisico;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Produto. Coluna discriminadora 'tipo' reconstrói ProdutoFisico/ProdutoDigital.
 */
public class ProdutoDAO {

    public Produto inserir(Produto p) {
        String sql = "INSERT INTO produto(tipo,nome,preco_base,estoque,peso_kg,tamanho_mb,url_download) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexaoSQLite.get()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, p);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir produto: " + e.getMessage(), e);
        }
    }

    public void atualizar(Produto p) {
        String sql = "UPDATE produto SET tipo=?,nome=?,preco_base=?,estoque=?,peso_kg=?,"
                + "tamanho_mb=?,url_download=? WHERE id=?";
        try (PreparedStatement ps = ConexaoSQLite.get().prepareStatement(sql)) {
            preencher(ps, p);
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void remover(int id) {
        try (PreparedStatement ps = ConexaoSQLite.get()
                .prepareStatement("DELETE FROM produto WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        try (Statement st = ConexaoSQLite.get().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM produto ORDER BY id")) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    private void preencher(PreparedStatement ps, Produto p) throws SQLException {
        ps.setString(2, p.getNome());
        ps.setDouble(3, p.getPrecoBase());
        ps.setInt(4, p.getEstoque());
        if (p instanceof ProdutoFisico fisico) {
            ps.setString(1, "FISICO");
            ps.setDouble(5, fisico.getPesoKg());
            ps.setObject(6, null);
            ps.setObject(7, null);
        } else {
            ProdutoDigital dig = (ProdutoDigital) p;
            ps.setString(1, "DIGITAL");
            ps.setObject(5, null);
            ps.setDouble(6, dig.getTamanhoMb());
            ps.setString(7, dig.getUrlDownload());
        }
    }

    private Produto montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String nome = rs.getString("nome");
        double preco = rs.getDouble("preco_base");
        int estoque = rs.getInt("estoque");
        if ("FISICO".equals(tipo)) {
            return new ProdutoFisico(id, nome, preco, estoque, rs.getDouble("peso_kg"));
        }
        return new ProdutoDigital(id, nome, preco, estoque,
                rs.getDouble("tamanho_mb"), rs.getString("url_download"));
    }
}

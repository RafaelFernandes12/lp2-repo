package br.com.loja.persistence;

import br.com.loja.model.Cliente;
import br.com.loja.model.ClienteRegular;
import br.com.loja.model.ClienteVip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Cliente. Usa a coluna discriminadora 'tipo' para persistir e
 * reconstruir a subclasse correta (REGULAR ou VIP) — permitindo manter o
 * polimorfismo após carregar do banco.
 */
public class ClienteDAO {

    public Cliente inserir(Cliente c) {
        String sql = "INSERT INTO cliente(tipo,nome,cpf,email,endereco,primeira_compra,pontos) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexaoSQLite.get()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, c);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir cliente: " + e.getMessage(), e);
        }
    }

    public void atualizar(Cliente c) {
        String sql = "UPDATE cliente SET tipo=?,nome=?,cpf=?,email=?,endereco=?,"
                + "primeira_compra=?,pontos=? WHERE id=?";
        try (PreparedStatement ps = ConexaoSQLite.get().prepareStatement(sql)) {
            preencher(ps, c);
            ps.setInt(8, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void remover(int id) {
        try (PreparedStatement ps = ConexaoSQLite.get()
                .prepareStatement("DELETE FROM cliente WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        Connection con = ConexaoSQLite.get();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM cliente ORDER BY id")) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    private void preencher(PreparedStatement ps, Cliente c) throws SQLException {
        if (c instanceof ClienteVip vip) {
            ps.setString(1, "VIP");
            ps.setObject(6, null);
            ps.setInt(7, vip.getPontosFidelidade());
        } else {
            ClienteRegular reg = (ClienteRegular) c;
            ps.setString(1, "REGULAR");
            ps.setInt(6, reg.isPrimeiraCompra() ? 1 : 0);
            ps.setObject(7, null);
        }
        ps.setString(2, c.getNome());
        ps.setString(3, c.getCpf());
        ps.setString(4, c.getEmail());
        ps.setString(5, c.getEndereco());
    }

    private Cliente montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");
        String email = rs.getString("email");
        String endereco = rs.getString("endereco");
        if ("VIP".equals(tipo)) {
            return new ClienteVip(id, nome, cpf, email, endereco, rs.getInt("pontos"));
        }
        return new ClienteRegular(id, nome, cpf, email, endereco,
                rs.getInt("primeira_compra") == 1);
    }
}

package br.com.loja.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerencia a conexão única com o banco SQLite (arquivo loja.db) e cria as
 * tabelas na primeira execução. Como SQLite armazena tudo em um único arquivo,
 * atende ao requisito de persistência em arquivo e ao bônus de banco de dados.
 */
public final class ConexaoSQLite {

    private static final String URL = "jdbc:sqlite:loja.db";
    private static Connection conexao;

    private ConexaoSQLite() {
    }

    public static Connection get() {
        try {
            if (conexao == null || conexao.isClosed()) {
                conexao = DriverManager.getConnection(URL);
                try (Statement st = conexao.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
            }
            return conexao;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar ao banco: " + e.getMessage(), e);
        }
    }

    /** Cria o schema se ainda não existir (idempotente). Chamado na inicialização. */
    public static void inicializar() {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS cliente (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipo TEXT NOT NULL," +            // REGULAR | VIP
                "nome TEXT NOT NULL," +
                "cpf TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "endereco TEXT NOT NULL," +
                "primeira_compra INTEGER," +        // ClienteRegular
                "pontos INTEGER)",                  // ClienteVip

            "CREATE TABLE IF NOT EXISTS produto (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipo TEXT NOT NULL," +             // FISICO | DIGITAL
                "nome TEXT NOT NULL," +
                "preco_base REAL NOT NULL," +
                "estoque INTEGER NOT NULL," +
                "peso_kg REAL," +                   // ProdutoFisico
                "tamanho_mb REAL," +                // ProdutoDigital
                "url_download TEXT)",               // ProdutoDigital

            "CREATE TABLE IF NOT EXISTS pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cliente_id INTEGER NOT NULL," +
                "situacao TEXT NOT NULL," +
                "pgto_forma TEXT," +                // PIX | CARTAO (null se não pago)
                "pgto_valor_base REAL," +
                "pgto_parcelas INTEGER," +
                "pgto_chave TEXT," +
                "FOREIGN KEY (cliente_id) REFERENCES cliente(id))",

            "CREATE TABLE IF NOT EXISTS item_pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pedido_id INTEGER NOT NULL," +
                "produto_id INTEGER NOT NULL," +
                "quantidade INTEGER NOT NULL," +
                "FOREIGN KEY (pedido_id) REFERENCES pedido(id)," +
                "FOREIGN KEY (produto_id) REFERENCES produto(id))"
        };
        try (Statement st = get().createStatement()) {
            for (String sql : ddl) {
                st.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao criar schema: " + e.getMessage(), e);
        }
    }
}

package io.github.wysohn.rapidframework3.utils.sql;

import java.sql.*;
import java.util.function.Consumer;

public class SQLSession {
    private final String url;
    private Connection connection;

    public SQLSession(String driverClass, String url, Consumer<Connection> initFn) throws SQLException {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.url = url;
        reconnect();
        initFn.accept(connection);
    }

    private void reconnect() throws SQLException {
        connection = DriverManager.getConnection(url);
    }

    public void execute(String sql, Consumer<PreparedStatement> fn, Consumer<Long> fnResult) {
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fn.accept(stmt);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next())
                    fnResult.accept(rs.getLong(1));
                else
                    fnResult.accept(-1L);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            try {
                reconnect();
                execute(sql, fn, fnResult);
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public void execute(String sql) throws SQLException {
        execute(sql, pstmt -> {
        }, id -> {
        });
    }

    public void query(String sql, Consumer<PreparedStatement> fn, Consumer<ResultSet> fnResult) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            fn.accept(stmt);

            try (ResultSet rs = stmt.executeQuery()) {
                fnResult.accept(rs);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                reconnect();
                query(sql, fn, fnResult);
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
        }
    }
}

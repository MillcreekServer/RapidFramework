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

    public void execute(String sql, Consumer<Integer> fn) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            fn.accept(stmt.executeUpdate());
        } catch (SQLException ex) {
            try {
                reconnect();
                execute(sql, fn);
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public void execute(String sql) throws SQLException {
        execute(sql, i -> {
        });
    }

    public void query(String sql, Consumer<ResultSet> fn) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            fn.accept(stmt.executeQuery());
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                reconnect();
                query(sql, fn);
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
        }
    }
}

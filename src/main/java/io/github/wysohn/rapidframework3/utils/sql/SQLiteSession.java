package io.github.wysohn.rapidframework3.utils.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SQLiteSession extends SQLSession {
    public SQLiteSession(File dbFile, Consumer<Connection> initFn) throws SQLException {
        super("org.sqlite.JDBC",
                String.format("jdbc:sqlite:%s", dbFile.getAbsolutePath()),
                initFn);
    }

    public static void main(String[] ar) throws SQLException {
        File file = new File("build/tmp/sqltest/somedb.db");
        file.getParentFile().mkdirs();

        SQLSession session = new SQLiteSession(file, conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS abc( value char(256), other integer );";
            try (PreparedStatement newTableStmt = conn.prepareStatement(sql)) {
                newTableStmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        session.execute("INSERT INTO abc VALUES(\"hoho\", 12345);");

        session.query("SELECT * FROM abc", resultSet -> {
            try {
                while (resultSet.next()) {
                    System.out.print(resultSet.getString("value"));
                    System.out.print(",");
                    System.out.print(resultSet.getInt("other"));
                    System.out.println();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}

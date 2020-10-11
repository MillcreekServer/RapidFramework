package io.github.wysohn.rapidframework3.utils.sql;

import org.junit.Test;

import java.io.File;
import java.sql.SQLException;

public class SQLiteSessionTest {
    @Test
    public void test() throws SQLException {
        File file = new File("build/tmp/sqltest/somedb.db");
        file.getParentFile().mkdirs();

        try {
            SQLSession session = SQLSession.Builder.sqlite(file)
                    .createTable("abc", tableInitializer -> tableInitializer.ifNotExist()
                            .field("id", "integer",
                                    SQLSession.Attribute.PRIMARY_KEY, SQLSession.Attribute.AUTO_INCREMENT)
                            .field("value", "char(256)")
                            .field("other", "integer"))
                    .build();

            session.execute("INSERT INTO abc(value, other) VALUES(?, ?);", pstmt -> {
                try {
                    pstmt.setString(1, "hoho");
                    pstmt.setInt(2, 12345);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, System.out::println);

            session.query("SELECT * FROM abc", pstmt -> {
            }, resultSet -> {
                try {
                    while (resultSet.next()) {
                        System.out.print(resultSet.getString("id"));
                        System.out.print(",");
                        System.out.print(resultSet.getString("value"));
                        System.out.print(",");
                        System.out.print(resultSet.getInt("other"));
                        System.out.println();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            session.execute("DELETE FROM abc WHERE id = ?", pstmt -> {
                try {
                    pstmt.setInt(1, 55);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, System.out::println);

            session.execute("DELETE FROM abc WHERE id = ?", pstmt -> {
                try {
                    pstmt.setInt(1, 1);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, System.out::println);

            session.query("SELECT * FROM abc", pstmt -> {
            }, resultSet -> {
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
        } finally {
            file.delete();
        }
    }
}
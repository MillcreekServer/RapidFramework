package io.github.wysohn.rapidframework3.utils.sql;

import org.junit.Test;

import java.io.File;
import java.sql.ResultSet;
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
                            .field("value", "char(256)", SQLSession.Attribute.NOT_NULL)
                            .field("other", "integer", SQLSession.Attribute.UNIQUE))
                    .build();

            session.execute("INSERT INTO abc(value, other) VALUES(?, ?);", pstmt -> {
                try {
                    pstmt.setString(1, "hoho");
                    pstmt.setInt(2, 12345);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, System.out::println);

            // before commit
            session.query("SELECT * FROM abc", pstmt -> {
            }, resultSet -> {
                try {
                    return Data.read(resultSet);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }).forEach(System.out::println);

            session.commit();

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

            // rollback to last commit
            session.rollback();

            session.query("SELECT * FROM abc", pstmt -> {
            }, resultSet -> {
                try {
                    return Data.read(resultSet);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }).forEach(System.out::println);
        } finally {
            file.delete();
        }
    }

    private static class Data {
        private final int id;
        private final String value;
        private final int other;

        private Data(int id, String value, int other) {
            this.id = id;
            this.value = value;
            this.other = other;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "id=" + id +
                    ", value='" + value + '\'' +
                    ", other=" + other +
                    '}';
        }

        public static Data read(ResultSet rs) throws SQLException {
            int id = rs.getInt("id");
            String value = rs.getString("value");
            int other = rs.getInt("other");

            return new Data(id, value, other);
        }


    }
}
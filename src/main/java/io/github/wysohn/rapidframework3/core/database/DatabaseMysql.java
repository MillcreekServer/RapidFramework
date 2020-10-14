/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework3.core.database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import io.github.wysohn.rapidframework3.utils.MiniConnectionPoolManager;
import io.github.wysohn.rapidframework3.utils.sql.SQLSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DatabaseMysql extends Database {
    private final String tablename;

    private final String KEY = "dbkey";
    private final String VALUE = "dbval";

    private final MysqlConnectionPoolDataSource ds;
    private final MiniConnectionPoolManager pool;

    private final String CREATETABLEQUARY = "" + "CREATE TABLE IF NOT EXISTS %s (" + "" + KEY
            + " CHAR(128) PRIMARY KEY," + "" + VALUE + " JSON" + ")";

    private final String CREATEDATABASEQUARY = "" + "CREATE DATABASE IF NOT EXISTS %s";
    private final String UPDATEQUARY = "INSERT INTO %s VALUES (" + "?," + "?" + ") " + "ON DUPLICATE KEY UPDATE " + ""
            + VALUE + " = VALUES(" + VALUE + ")";

    public DatabaseMysql(String address,
                         String dbName,
                         String tablename,
                         String userName,
                         String password)
            throws SQLException {
        super(tablename);
        this.tablename = tablename;

        ds = SQLSession.createDataSource(address, dbName, userName, password);
        pool = new MiniConnectionPoolManager(ds, 2, 0.5);

        Connection conn = pool.getConnection();
        initTable(conn);
        conn.close();
    }

    private final String SELECTKEY = "" + "SELECT " + VALUE + " FROM %s WHERE " + KEY + " = ?";

    private void initTable(Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(String.format(CREATETABLEQUARY, tablename));
        pstmt.executeUpdate();
        pstmt.close();
    }

    private final String DELETEQUARY = "DELETE FROM %s WHERE " + KEY + " = ?";

    @Override
    public String load(String key) throws IOException {
        Connection conn = null;
        String result = null;

        try {
            conn = pool.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(String.format(SELECTKEY, tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getString(VALUE); // https://forums.mysql.com/read.php?39,662014,662042
            }
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private final String SELECTKEYS = "" + "SELECT " + KEY + " FROM %s";

    @Override
    public void save(String key, String serialized) throws IOException {
        Connection conn = null;
        try {
            conn = pool.getConnection();

            if (serialized != null) {
                PreparedStatement pstmt = conn.prepareStatement(String.format(UPDATEQUARY, tablename));
                pstmt.setString(1, key);
                pstmt.setString(2, serialized); // https://forums.mysql.com/read.php?39,662014,662042
                pstmt.executeUpdate();
                pstmt.close();
            } else {
                PreparedStatement pstmt = conn.prepareStatement(String.format(DELETEQUARY, tablename));
                pstmt.setString(1, key);
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private final String SELECTKEYSWHERE = "" + "SELECT " + KEY + " FROM %s WHERE " + KEY + " = ?";

    @Override
    public synchronized Set<String> getKeys() {
        Set<String> keys = new HashSet<String>();

        Connection conn = null;
        try {
            conn = pool.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(String.format(SELECTKEYS, tablename));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                keys.add(rs.getString(KEY));
            }
            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return keys;
    }

    @Override
    public synchronized boolean has(String key) {
        boolean result = false;

        Connection conn = null;
        try {
            conn = pool.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(String.format(SELECTKEYSWHERE, tablename));
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();

            result = rs.next();

            rs.close();
            pstmt.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private final String DELETEALL = "" + "DELETE FROM %s";

    @Override
    protected void finalize() throws Throwable {
        if (pool != null)
            pool.dispose();
        super.finalize();
    }

    @Override
    public void clear() {
        Connection conn = null;
        try {
            conn = pool.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(String.format(DELETEALL, tablename));
            pstmt.executeLargeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

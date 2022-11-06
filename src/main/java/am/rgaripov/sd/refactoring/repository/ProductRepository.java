package am.rgaripov.sd.refactoring.repository;

import am.rgaripov.sd.refactoring.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ProductRepository {

    private final String DBConnectionAddress;

    public ProductRepository(String connectionAddress) {
        this.DBConnectionAddress = connectionAddress;
    }

    public void insert(Product product) throws SQLException {
        String query = "INSERT INTO PRODUCT " +
                "(NAME, PRICE) VALUES (\"" + product.getName() + "\"," + product.getPrice().toString() + ")";
        runUpdate(query);
    }

    public List<Product> selectAll() throws SQLException {
        String query = "SELECT * FROM PRODUCT";
        return runQuery(query, this::processProducts);
    }

    public Product max() throws SQLException {
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
        return runQuery(query, this::firstRow);
    }

    public Product min() throws SQLException {
        String query = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
        return runQuery(query, this::firstRow);
    }

    public Integer count() throws SQLException {
        String query = "SELECT COUNT(*) FROM PRODUCT";
        return runQuery(query, this::firstRowFirstColumn);
    }

    public Integer sum() throws SQLException {
        String query = "SELECT SUM(price) FROM PRODUCT";
        return runQuery(query, this::firstRowFirstColumn);
    }

    private Integer firstRowFirstColumn(ResultSet rs) {
        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Product firstRow(ResultSet rs) {
        List<Product> list = processProducts(rs);
        return list.get(0);
    }

    private List<Product> processProducts(ResultSet rs) {
        List<Product> products = new ArrayList<>();
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                products.add(new Product(name, price));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    private void runUpdate(String query) throws SQLException {
        try (Connection c = DriverManager.getConnection(this.DBConnectionAddress)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }
    }

    private <T> T runQuery(String query, Function<ResultSet, T> processSQLResultSet) throws SQLException {
        T result;
        try (Connection c = DriverManager.getConnection(this.DBConnectionAddress)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            result =  processSQLResultSet.apply(rs);
            rs.close();
            stmt.close();
        }
        return result;
    }

    public void initDB() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)";
        runUpdate(query);
    }

    public void dropDB() throws SQLException {
        String query = "DROP TABLE PRODUCT";
        runUpdate(query);
    }
}

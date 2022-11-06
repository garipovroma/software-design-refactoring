package am.rgaripov.sd.refactoring.servlet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.when;

public class ServletTest {

    AddProductServlet addProductServlet = new AddProductServlet();
    GetProductsServlet getProductsServlet = new GetProductsServlet();
    QueryServlet queryServlet = new QueryServlet();

    @Before
    public void initAndFillDB() {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            Map<String, Integer> map =
                    new TreeMap(Map.of("Fish", 0,
                            "Meat", 10,
                            "Milk", 20,
                            "Cookie", 30,
                            "Doritos", 40,
                            "Coca Cola", 50));
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String name = entry.getKey();
                Integer price = entry.getValue();
                String sql = "INSERT INTO PRODUCT " +
                        "(NAME, PRICE) VALUES (\"" + name + "\"," + price + ")";
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void simpleTest() {
        checkGetProductsServlet("<html><body>\n" +
                "Coca Cola\t50</br>\n" +
                "Cookie\t30</br>\n" +
                "Doritos\t40</br>\n" +
                "Fish\t0</br>\n" +
                "Meat\t10</br>\n" +
                "Milk\t20</br>\n" +
                "</body></html>");
    }

    @Test
    public void addProductsTest() {
        checkAddProductServlet("X", 100);
        checkAddProductServlet("Y", 200);
        checkAddProductServlet("Z", 300);
        checkGetProductsServlet("<html><body>\n" +
                "Coca Cola\t50</br>\n" +
                "Cookie\t30</br>\n" +
                "Doritos\t40</br>\n" +
                "Fish\t0</br>\n" +
                "Meat\t10</br>\n" +
                "Milk\t20</br>\n" +
                "X\t100</br>\n" +
                "Y\t200</br>\n" +
                "Z\t300</br>\n" +
                "</body></html>");
    }

    @Test
    public void maxQuery() {
        checkQuery("max", "<html><body>\n" +
                "<h1>Product with max price: </h1>\n" +
                "Coca Cola\t50</br>\n" +
                "</body></html>");
    }

    @Test
    public void minQuery() {
        checkQuery("min", "<html><body>\n" +
                "<h1>Product with min price: </h1>\n" +
                "Fish\t0</br>\n" +
                "</body></html>");
    }

    @Test
    public void sumQuery() {
        checkQuery("sum", "<html><body>\n" +
                "Summary price: \n" +
                "150\n" +
                "</body></html>");
    }

    @Test
    public void countQuery() {
        checkQuery("count", "<html><body>\n" +
                "Number of products: \n" +
                "6\n" +
                "</body></html>");
    }

    @Test
    public void allServletsTest() {
        checkQuery("count", "<html><body>\n" +
                "Number of products: \n" +
                "6\n" +
                "</body></html>");
        checkAddProductServlet("X", 100);
        checkGetProductsServlet("<html><body>\n" +
                "Coca Cola\t50</br>\n" +
                "Cookie\t30</br>\n" +
                "Doritos\t40</br>\n" +
                "Fish\t0</br>\n" +
                "Meat\t10</br>\n" +
                "Milk\t20</br>\n" +
                "X\t100</br>\n" +
                "</body></html>");
        checkAddProductServlet("Y", 200);
        checkGetProductsServlet("<html><body>\n" +
                "Coca Cola\t50</br>\n" +
                "Cookie\t30</br>\n" +
                "Doritos\t40</br>\n" +
                "Fish\t0</br>\n" +
                "Meat\t10</br>\n" +
                "Milk\t20</br>\n" +
                "X\t100</br>\n" +
                "Y\t200</br>\n" +
                "</body></html>");
        checkQuery("sum", "<html><body>\n" +
                "Summary price: \n" +
                "450\n" +
                "</body></html>");
        checkAddProductServlet("Z", 300);
        checkGetProductsServlet("<html><body>\n" +
                "Coca Cola\t50</br>\n" +
                "Cookie\t30</br>\n" +
                "Doritos\t40</br>\n" +
                "Fish\t0</br>\n" +
                "Meat\t10</br>\n" +
                "Milk\t20</br>\n" +
                "X\t100</br>\n" +
                "Y\t200</br>\n" +
                "Z\t300</br>\n" +
                "</body></html>");
        checkQuery("count", "<html><body>\n" +
                "Number of products: \n" +
                "9\n" +
                "</body></html>");
        checkQuery("max", "<html><body>\n" +
                "<h1>Product with max price: </h1>\n" +
                "Z\t300</br>\n" +
                "</body></html>");
        checkQuery("sum", "<html><body>\n" +
                    "Summary price: \n" +
                    "750\n" +
                    "</body></html>");
    }

    void checkQuery(String query, String responseString) {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();

        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            when(response.getWriter()).thenReturn(printWriter);
            HttpServletRequest request = buildRequest(Collections.emptyMap());
            when(request.getParameter("command")).thenReturn(query);
            queryServlet.doGet(request, response);
            Assert.assertTrue(stringWriter.toString().contains(responseString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    HttpServletRequest buildRequest(Map<String, String> map) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        for (Map.Entry<String, String> entry: map.entrySet()) {
            when(request.getParameter(entry.getKey())).thenReturn(entry.getValue());
        }
       return request;
    }

    void checkGetProductsServlet(String responseString) {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();

        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            when(response.getWriter()).thenReturn(printWriter);
            HttpServletRequest request = buildRequest(Collections.emptyMap());
            getProductsServlet.doGet(request, response);
            Assert.assertTrue(stringWriter.toString().contains(responseString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void checkAddProductServlet(String name, Integer price) {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();

        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            when(response.getWriter()).thenReturn(printWriter);
            HttpServletRequest request = buildRequest(Collections.emptyMap());
            when(request.getParameter("name")).thenReturn(name);
            when(request.getParameter("price")).thenReturn(price.toString());
            addProductServlet.doGet(request, response);
            Assert.assertTrue(stringWriter.toString().contains("OK"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void clearDB() {
        try {
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                String sql = "DROP TABLE PRODUCT";
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

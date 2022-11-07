package am.rgaripov.sd.refactoring.servlet;

import am.rgaripov.sd.refactoring.model.Product;
import am.rgaripov.sd.refactoring.repository.ProductRepository;
import am.rgaripov.sd.refactoring.response.HttpResponseBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author akirakozov
 */
public class AddProductServlet extends HttpServlet {

    private final ProductRepository productRepository;

    public AddProductServlet(ProductRepository repo) {
        this.productRepository = repo;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        Integer price = Integer.parseInt(request.getParameter("price"));
        HttpResponseBuilder builder = new HttpResponseBuilder();
        try {
            productRepository.insert(new Product(name, price));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        builder.str("OK");
        builder.build(response);
    }
}

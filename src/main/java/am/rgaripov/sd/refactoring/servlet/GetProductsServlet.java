package am.rgaripov.sd.refactoring.servlet;

import am.rgaripov.sd.refactoring.model.Product;
import am.rgaripov.sd.refactoring.repository.ProductRepository;
import am.rgaripov.sd.refactoring.response.HttpResponseBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    private final ProductRepository productRepository;

    public GetProductsServlet(ProductRepository repo) {
        this.productRepository = repo;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpResponseBuilder builder = new HttpResponseBuilder();
        try {
            List<Product> result = productRepository.selectAll();
            result.forEach((x -> builder.str(x.getName())
                                        .tab()
                                        .str(x.getPrice())
                                        .br()
                                        .newLine()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        builder.build(response);
    }
}

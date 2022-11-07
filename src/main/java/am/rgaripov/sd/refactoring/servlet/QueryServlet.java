package am.rgaripov.sd.refactoring.servlet;

import am.rgaripov.sd.refactoring.model.Product;
import am.rgaripov.sd.refactoring.repository.ProductRepository;
import am.rgaripov.sd.refactoring.response.HttpResponseBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

    private final ProductRepository productRepository;

    public QueryServlet(ProductRepository repo) {
        this.productRepository = repo;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        HttpResponseBuilder builder = new HttpResponseBuilder();
        try {
            if ("max".equals(command)) {
                Product product = productRepository.max();
                builder.headerStr("Product with max price: ").newLine()
                        .str(product.getName()).tab().str(product.getPrice()).br();
            } else if ("min".equals(command)) {
                Product product = productRepository.min();
                builder.headerStr("Product with min price: ").newLine()
                        .str(product.getName()).tab().str(product.getPrice()).br();
            } else if ("sum".equals(command)) {
                Integer sum = productRepository.sum();
                builder.str("Summary price: ").newLine()
                        .str(sum);
            } else if ("count".equals(command)) {
                Integer count = productRepository.count();
                builder.str("Number of products: ").newLine()
                        .str(count);
            } else {
                builder.str("Unknown command: ").str(command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        builder.build(response);
    }
}

package am.rgaripov.sd.refactoring.servlet;

import am.rgaripov.sd.refactoring.model.Product;
import am.rgaripov.sd.refactoring.repository.ProductRepository;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
        try {
            List<Product> result = productRepository.selectAll();
            response.getWriter().println("<html><body>");
            PrintWriter writer = response.getWriter();
            result.forEach((x -> writer.println(x.getName() + "\t" + x.getPrice() + "</br>")));
            response.getWriter().println("</body></html>");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

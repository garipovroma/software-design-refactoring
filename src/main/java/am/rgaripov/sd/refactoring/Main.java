package am.rgaripov.sd.refactoring;

import am.rgaripov.sd.refactoring.repository.ProductRepository;
import am.rgaripov.sd.refactoring.servlet.AddProductServlet;
import am.rgaripov.sd.refactoring.servlet.GetProductsServlet;
import am.rgaripov.sd.refactoring.servlet.QueryServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ProductRepository repo = new ProductRepository("jdbc:sqlite:test.db");
        repo.initDB();

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(repo)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(repo)),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(repo)),"/query");

        server.start();
        server.join();
    }
}

package am.rgaripov.sd.refactoring.response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpResponseBuilder {
    StringBuilder stringBuilder = new StringBuilder("");

    public HttpResponseBuilder str(String string) {
        stringBuilder.append(string);
        return this;
    }

    public HttpResponseBuilder str(Integer v) {
        stringBuilder.append(v);
        return this;
    }

    public HttpResponseBuilder headerStr(String string) {
        stringBuilder.append(String.format("<h1>%s</h1>", string));
        return this;
    }

    public HttpResponseBuilder tab() {
        stringBuilder.append("\t");
        return this;
    }

    public HttpResponseBuilder br() {
        stringBuilder.append("</br>");
        return this;
    }

    public HttpResponseBuilder newLine() {
        stringBuilder.append("\n");
        return this;
    }

    public HttpServletResponse build(HttpServletResponse response) throws IOException {
        response.getWriter().write("<html><body>\n");
        String str = stringBuilder.toString();
        if (!str.endsWith("\n")) {
            str += "\n";
        }
        response.getWriter().write(str);
        response.getWriter().write("</body></html>");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        return response;
    }

}

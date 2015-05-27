package com.github.terma.logb;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class ListServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ListServlet.class.getName());

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String ERROR_DELIMITER = "/* --- JSON STREAM --- ERROR DELIMITER --- */";

    private final Gson gson = new Gson();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("Start list request...");
        response.setContentType(JSON_CONTENT_TYPE);

        final ListRequest listRequest = gson.fromJson(RequestUtils.getRequestBody(request), ListRequest.class);

        final PrintWriter writer = response.getWriter();
        try {
            writer.append(gson.toJson(new DispatcherService().list(listRequest.app)));
        } catch (final Throwable exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.append(ERROR_DELIMITER).append('\n');
            writer.append(gson.toJson(exception));
        }
        LOGGER.info("Finish request");
    }

}

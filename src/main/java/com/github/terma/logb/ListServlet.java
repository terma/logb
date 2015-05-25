package com.github.terma.logb;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ListServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String ERROR_DELIMITER = "/* --- JSON STREAM --- ERROR DELIMITER --- */";

    private final Gson gson = new Gson();

    private final File directory = new File("/Users/terma/Projects/logb");

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
//        final T executeRequest = gson.fromJson(getRequestBody(request), Object.class);
        response.setContentType(JSON_CONTENT_TYPE);

        final PrintWriter writer = response.getWriter();
        try {
            writer.append(gson.toJson(toList(".", directory.listFiles())));
        } catch (final Throwable exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.append(ERROR_DELIMITER).append('\n');
            writer.append(gson.toJson(exception));
        }
    }

    private static List<ListItem> toList(final String path, final File[] files) {
        List<ListItem> result = new ArrayList<ListItem>();
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(toList(path + "/" + file.getName(), file.listFiles()));
            } else {
                result.add(fileToItem(path, file));
            }
        }
        return result;
    }

    private static ListItem fileToItem(final String path, final File file) {
        ListItem listItem = new ListItem();
        listItem.name = path + "/" + file.getName();
        listItem.lenght = file.length();
        listItem.lastModified = file.lastModified();
        return listItem;
    }

    private static String getRequestBody(final HttpServletRequest request) throws IOException {
        final StringBuilder requestBody = new StringBuilder();

        while (true) {
            String line = request.getReader().readLine();
            if (line == null) break;
            else requestBody.append(line).append('\n');
        }

        return requestBody.toString();
    }

}

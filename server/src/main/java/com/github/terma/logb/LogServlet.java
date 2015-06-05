/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.logb;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Set;

// todo show list of files with pattern in content -> node-1 and content 12

// todo show lines by pattern in single log

// todo show list of files and lines with pattern in content
// todo open log in new tab/window
public class LogServlet extends HttpServlet {

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
        final LogRequest logRequest = gson.fromJson(RequestUtils.getRequestBody(request), LogRequest.class);
        response.setContentType(JSON_CONTENT_TYPE);

        final PrintWriter writer = response.getWriter();
        try {
            writer.append(gson.toJson(DispatcherService.INSTANCE.piece(logRequest, getNodeJar(this))));
        } catch (final Throwable exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.append(ERROR_DELIMITER).append('\n');
            writer.append(gson.toJson(new JsonException(exception)));
        }
    }

    public static InputStream getNodeJar(HttpServlet httpServlet) {
        Set<String> libs = httpServlet.getServletContext().getResourcePaths("/WEB-INF/lib/");
        for (String lib : libs) {
            if (lib.contains("logb-node")) {
                return httpServlet.getServletContext().getResourceAsStream(lib);
            }
        }
        throw new IllegalArgumentException("Can't find logb-node jar in " + libs);
    }

}

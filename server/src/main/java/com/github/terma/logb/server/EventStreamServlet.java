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

package com.github.terma.logb.server;

import com.github.terma.logb.*;
import com.github.terma.logb.node.EventStreamRequest;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class EventStreamServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EventStreamServlet.class.getName());

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
        LOGGER.info("Start stream request...");
        response.setContentType(JSON_CONTENT_TYPE);

        final EventStreamRequest streamRequest = new Gson().fromJson(RequestUtils.getRequestBody(request), EventStreamRequest.class);

        final PrintWriter writer = response.getWriter();
        try {
            final List<Pr> eventStreamRemotes = DispatcherService.INSTANCE
                    .getEventStreamRemotes(streamRequest, LogServlet.getNodeJar(this));

            final List<StreamEvent> streamEvents = new ArrayList<>();
            for (Pr eventStreamRemote : eventStreamRemotes) {
                streamEvents.addAll(eventStreamRemote.eventStreamRemote.get(
                        streamRequest, eventStreamRemote.paths));
            }
            Collections.sort(streamEvents);

            writer.append(new Gson().toJson(streamEvents));
        } catch (final Throwable exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.append(ERROR_DELIMITER).append('\n');
            writer.append(gson.toJson(new JsonException(exception)));
        }
        LOGGER.info("Finish request");
    }

}

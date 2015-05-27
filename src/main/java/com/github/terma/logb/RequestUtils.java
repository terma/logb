package com.github.terma.logb;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by terma on 5/26/15.
 */
public class RequestUtils {
    public static String getRequestBody(final HttpServletRequest request) throws IOException {
        final StringBuilder requestBody = new StringBuilder();

        while (true) {
            String line = request.getReader().readLine();
            if (line == null) break;
            else requestBody.append(line).append('\n');
        }

        return requestBody.toString();
    }
}

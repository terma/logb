package com.github.terma.logb.node;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogMonkey {

    public static void main(String[] args) throws InterruptedException, IOException {
        final Random random = new Random();
        final List<File> files = new ArrayList<>();
        files.add(new File("test-data", "log-host000.log"));
        files.add(new File("test-data", "log-host900.log"));
        files.add(new File("test-data", "log-host2-host3-host000.log"));
        files.add(new File("test-data", "log.log"));

        while (true) {
            final File file = files.get(random.nextInt(files.size()));
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy HH:mm:ss").withZoneUTC();

                writer.print(formatter.print(System.currentTimeMillis()));
                writer.print(" ");
                switch (random.nextInt(2)) {
                    case 0:
                        writer.print("INFO:oejsh.ContextHandler:main: Started o.e.j.m.p.JettyWebAppContext@2873d672{/,file:///Users/terma/Projects/logb/server/src/main/webapp/,AVAILABLE}{file:///Users/terma/Projects/logb/server/src/main/webapp/}");
                        break;
                    default:
                        writer.print("com.github.terma.logb.server.EventStreamServlet doPost");
                }
                writer.println();
            }

            Thread.sleep(1000L);
        }
    }

}

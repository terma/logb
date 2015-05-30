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

import com.github.terma.logb.config.ConfigServer;
import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteNodeRunner {

    private final static Pattern PORT_PATTERN = Pattern.compile("at port (\\d+)");
    private final static Logger LOGGER = Logger  .getLogger(RemoteNodeRunner.class.getName());

    public static void main(String[] args) throws FileNotFoundException {
        ConfigServer server = new ConfigServer();
        server.host = "localhost";
        safeStart(server, new FileInputStream("/Users/terma/Projects/logb/node/target/logb-node-0.2-SNAPSHOT.jar"));

        LOGGER.info("yspeh!");
    }

    public static int safeStart(final ConfigServer server, final InputStream jar) {
        try {
            return start(server, jar);
        } catch (IOException | JSchException | SftpException e) {
            throw new RuntimeException("Can't start node: " + server, e);
        }
    }

    private static String getPrivateKeyFile(final ConfigServer server) {
        return server.privateKeyFile == null ? "~/.ssh/id_rsa" : server.privateKeyFile;
    }

    private static int start(final ConfigServer server, final InputStream jar)
            throws JSchException, SftpException, IOException {
        final JSch jsch = new JSch();

        jsch.addIdentity(getPrivateKeyFile(server));
        Session session = jsch.getSession(server.user, server.host);
        final Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setConfig("PreferredAuthentications", "publickey");
        session.connect();

        LOGGER.info("Copying node data...");
        final ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        channelSftp.put(RemoteNodeRunner.class.getResourceAsStream("/logb-node.sh"), NodeRunner.NODE_FILE + ".sh");
        channelSftp.put(jar, NodeRunner.NODE_FILE + ".jar");
        channelSftp.chmod(500, NodeRunner.NODE_FILE + ".sh");
        channelSftp.disconnect();

        LOGGER.info("Executing start node script...");
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand("./" + NodeRunner.NODE_FILE + ".sh 2>&1");

        BufferedReader outReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

        channelExec.connect();

        String line;
        String lastLine = "";
        while ((line = outReader.readLine()) != null) {
            lastLine = line;
            LOGGER.info(line);
        }
        int exitStatus = channelExec.getExitStatus();
        channelExec.disconnect();
        session.disconnect();

        if (exitStatus != 0) {
            throw new RuntimeException("Can't start node: " + server
                    + ", exit code: " + exitStatus);
        }

        // from last log line get port if present
        final Matcher matcher = PORT_PATTERN.matcher(lastLine);
        if (!matcher.find())
            throw new RuntimeException("Can't find port of started node in out: " + lastLine + ", node: " + server);
        return Integer.parseInt(matcher.group(1));
    }

}

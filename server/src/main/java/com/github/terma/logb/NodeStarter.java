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

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeStarter {

    private final static Pattern PORT_PATTERN = Pattern.compile("at port (\\d+)");

    public static void main(String[] args) throws FileNotFoundException {
        safeStart("localhost", new FileInputStream("/Users/terma/Projects/logb/node/target/logb-node-0.1-SNAPSHOT.jar"));

        System.out.println("yspeh!");
    }

    public static int safeStart(final String host, final InputStream jarInputStream) {
        try {
            return start(host, jarInputStream);
        } catch (IOException | JSchException | SftpException e) {
            throw new RuntimeException("Can't start node on host: " + host, e);
        }
    }

    private static int start(final String host, final InputStream jarInputStream)
            throws JSchException, SftpException, IOException {
        final JSch jsch = new JSch();
        jsch.addIdentity("~/.ssh/id_rsa");
        Session session = jsch.getSession(host);
        final Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setConfig("PreferredAuthentications", "publickey");
        session.connect();

        System.out.println("Copying node data...");
        final ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        channelSftp.put(NodeStarter.class.getResourceAsStream("/logb-node.sh"), "logb-node.sh");
        channelSftp.put(jarInputStream, "logb-node.jar");
        channelSftp.chmod(500, "logb-node.sh");
        channelSftp.disconnect();

        System.out.println("Executing start node script...");
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand("./logb-node.sh 2>&1");

        BufferedReader outReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

        channelExec.connect();

        String line;
        String lastLine = "";
        while ((line = outReader.readLine()) != null) {
            lastLine = line;
            System.out.println(line);
        }
        int exitStatus = channelExec.getExitStatus();
        channelExec.disconnect();
        session.disconnect();

        if (exitStatus != 0) {
            throw new RuntimeException("Can't start node! Host: " + host
                    + ", exit code: " + exitStatus);
        }

        // from last log line get port if present
        final Matcher matcher = PORT_PATTERN.matcher(lastLine);
        if (!matcher.find())
            throw new RuntimeException("Can't find port of started node in out: " + lastLine + ", host: " + host);
        final int nodePort = Integer.parseInt(matcher.group(1));
        return nodePort;
    }

}

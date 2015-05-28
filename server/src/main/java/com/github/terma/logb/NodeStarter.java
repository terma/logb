package com.github.terma.logb;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class NodeStarter {

    public static void main(String[] args) throws JSchException, SftpException, IOException {
        final JSch jsch = new JSch();
        jsch.addIdentity("~/.ssh/id_rsa");
        Session session = jsch.getSession("localhost");
        final Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setConfig("PreferredAuthentications", "publickey");
        session.connect();

        // kill previous process


        System.out.println("Copying node data...");
        final ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        channelSftp.put(NodeStarter.class.getResourceAsStream("/logb-node.sh"), "/Users/terma/logb-node.sh");
        channelSftp.put(new FileInputStream("/Users/terma/Projects/logb/node/target/logb-node-0.1-SNAPSHOT.jar"), "/Users/terma/logb-node.jar");
        channelSftp.chmod(500, "/Users/terma/logb-node.sh");
        channelSftp.disconnect();

        System.out.println("Executing start node script...");
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
//        channelExec.setErrStream(null);
        channelExec.setCommand("/Users/terma/logb-node.sh 2>&1");

//        BufferedReader errorReader = new BufferedReader(new InputStreamReader(channelExec.getErrStream()));
        BufferedReader outReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

        channelExec.connect();

        String line;
//        while ((line = errorReader.readLine()) != null) System.err.println(line);

        while ((line = outReader.readLine()) != null) System.out.println(line);

        channelExec.disconnect();

        int exitStatus = channelExec.getExitStatus();
        session.disconnect();

        if (exitStatus != 0) {
            throw new RuntimeException("OPA " + exitStatus);
        }

        System.out.println("yspeh!");
        System.exit(0);

        // todo run jar
        // todo wait while it publish port info
        // todo connect by published port
    }

}

package com.qingyou.qynat.server;

import org.apache.commons.cli.*;
import com.qingyou.qynat.server.server.QyNatServer;

/**
 * @author whz
 * @date 2021/7/16 13:07
 **/
public class QyNatServerStart {
    public static void main(String[] args) throws ParseException, InterruptedException {

        // args
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("p", true, "QyNat server port");
        options.addOption("t", true, "QyNat server token");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {

            int port = Integer.parseInt(cmd.getOptionValue("p", "7777"));
            String token = cmd.getOptionValue("t");
            QyNatServer server = new QyNatServer();
            server.start(port, token);
            System.out.println("QyNat server started on port " + port);
        }
    }
}

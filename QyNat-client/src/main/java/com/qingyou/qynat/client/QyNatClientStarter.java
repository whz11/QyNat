package com.qingyou.qynat.client;


import com.qingyou.qynat.client.client.QyNatClient;
import com.qingyou.qynat.client.handler.QyNatClientHandler;
import org.apache.commons.cli.*;

/**
 * @author whz
 * @date 2021/7/16 13:40
 **/
public class QyNatClientStarter {
    public static void main(String[] args) throws Exception {

        // args
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("server_addr", true, "Nat com.qingyou.qynat.server address");
        options.addOption("server_port", true, "Nat com.qingyou.qynat.server port");
        options.addOption("pwd", true, "Nat com.qingyou.qynat.server password");
        options.addOption("proxy_addr", true, "Proxy com.qingyou.qynat.server address");
        options.addOption("proxy_port", true, "Proxy com.qingyou.qynat.server port");
        options.addOption("remote_port", true, "Proxy com.qingyou.qynat.server remote port");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

//        if (cmd.hasOption("h")) {
//            // print help
//            HelpFormatter formatter = new HelpFormatter();
//            formatter.printHelp("options", options);
//        } else {
//
//            String serverAddress = cmd.getOptionValue("server_addr");
//            if (serverAddress == null) {
//                System.out.println("server_addr cannot be null");
//                return;
//            }
//            String serverPort = cmd.getOptionValue("server_port");
//            if (serverPort == null) {
//                System.out.println("server_port cannot be null");
//                return;
//            }
//            String password = cmd.getOptionValue("pwd");
//            String proxyAddress = cmd.getOptionValue("proxy_addr");
//            if (proxyAddress == null) {
//                System.out.println("proxy_addr cannot be null");
//                return;
//            }
//            String proxyPort = cmd.getOptionValue("proxy_port");
//            if (proxyPort == null) {
//                System.out.println("proxy_port cannot be null");
//                return;
//            }
//            String remotePort = cmd.getOptionValue("remote_port");
//            if (remotePort == null) {
//                System.out.println("remote_port cannot be null");
//                return;
//            }

            QyNatClient client = new QyNatClient();
            String serverAddress = "1.116.186.185";
            String serverPort = "7777";
            String password = "123456";
            String remotePort = "8888";
            String proxyAddress = "localhost";
            String proxyPort = "8080";
            client.connect(serverAddress, serverPort, remotePort, password,
                    proxyAddress, proxyPort, QyNatClientHandler.class);
//        }
    }
}

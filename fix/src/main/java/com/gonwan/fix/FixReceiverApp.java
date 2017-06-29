package com.gonwan.fix;

import java.util.Scanner;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

/* 
 * See: http://www.tuicool.com/articles/v2me6r
 */
public class FixReceiverApp {

    public static void main(String[] args) throws ConfigError {
        SessionSettings settings = new SessionSettings("receiver.cfg");
        Application myApp = new FixReceiver();
        FileStoreFactory fileStoreFactory = new FileStoreFactory(settings);
        ScreenLogFactory screenLogFactory = new ScreenLogFactory(settings);
        DefaultMessageFactory msgFactory = new DefaultMessageFactory();
        SocketAcceptor acceptor = new SocketAcceptor(myApp, fileStoreFactory, settings, screenLogFactory, msgFactory);
        acceptor.start();
        Scanner reader = new Scanner(System.in);
        System.out.println("press <enter> to quit");

        // get user input for a
        reader.nextLine();

        acceptor.stop();
    }

}
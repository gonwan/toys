package com.gonwan.fix;

import java.util.Scanner;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

public class FixReceiverApp {

    public static void main(String[] args) throws ConfigError {
        SessionSettings settings = new SessionSettings("receiver.cfg");
        Application app = new FixReceiver();
        FileStoreFactory fileStoreFactory = new FileStoreFactory(settings);
        ScreenLogFactory screenLogFactory = new ScreenLogFactory(settings);
        DefaultMessageFactory msgFactory = new DefaultMessageFactory();
        SocketAcceptor acceptor = new SocketAcceptor(app, fileStoreFactory, settings, screenLogFactory, msgFactory);
        acceptor.start();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press <enter> to quit");
        scanner.nextLine();
        scanner.close();
        acceptor.stop();
    }

}


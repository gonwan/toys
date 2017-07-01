package com.gonwan.fix;

import java.util.Date;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.NewOrderSingle;

public class FixSenderApp {

    public static void main(String[] args) throws ConfigError, SessionNotFound, InterruptedException {
        SessionSettings settings = new SessionSettings("sender.cfg");
        Application app = new FixSender();
        FileStoreFactory fileStoreFactory = new FileStoreFactory(settings);
        ScreenLogFactory screenLogFactory = new ScreenLogFactory(settings);
        DefaultMessageFactory msgFactory = new DefaultMessageFactory();
        SocketInitiator initiator = new SocketInitiator(app, fileStoreFactory, settings, screenLogFactory, msgFactory);
        initiator.start();
        /* wait for start */
        Thread.sleep(3000);
        /* matching values from sender.cfg */
        SessionID sessionID = new SessionID("FIX.4.4", "CLIENT1", "FIX-SERVER");
        NewOrderSingle order = new NewOrderSingle(
                new ClOrdID("11111"), new Side(Side.BUY), new TransactTime(new Date()), new OrdType(OrdType.LIMIT));
        order.set(new OrderQty(45.00));
        order.set(new Price(25.40));
        order.set(new Symbol("AAPL"));
        Session.sendToTarget(order, sessionID);
        /* wait for send */
        Thread.sleep(60000);
        initiator.stop();
    }
}


package com.gonwan.fix;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.fix44.MessageCracker;
import quickfix.fix44.NewOrderSingle;

public class FixReceiver extends MessageCracker implements Application {

    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("Receiver onCreate: " + sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Receiver onLogon: " + sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("Receiver onLogout: " + sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("Receiver fromApp: " + sessionId);
        crack(message, sessionId);
    }

    @Override
    public void onMessage(NewOrderSingle message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Receiver onMessage: " + message);
    }

}


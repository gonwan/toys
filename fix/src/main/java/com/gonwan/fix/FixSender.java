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

public class FixSender implements Application {

    @Override
    public void fromAdmin(Message arg0, SessionID arg1)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void fromApp(Message arg0, SessionID arg1)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    }

    @Override
    public void onCreate(SessionID arg0) {
    }

    @Override
    public void onLogon(SessionID arg0) {
    }

    @Override
    public void onLogout(SessionID arg0) {
    }

    @Override
    public void toAdmin(Message arg0, SessionID arg1) {
    }

    @Override
    public void toApp(Message msg, SessionID sessionId) throws DoNotSend {
        System.out.println("Sender toApp: " + msg.toString());
    }

}
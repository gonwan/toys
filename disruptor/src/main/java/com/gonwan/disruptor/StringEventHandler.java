package com.gonwan.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

class StringEventHandler implements EventHandler<StringEvent>, WorkHandler<StringEvent> {

    private String name;
    private long millis;

    public StringEventHandler(String name, long millis) {
        this.name = name;
        this.millis = millis;
    }

    @Override
    public void onEvent(StringEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(String.format("[%s] seq=%d value=%s", name, sequence, event.getValue()));
        Thread.sleep(millis);
    }

    @Override
    public void onEvent(StringEvent event) throws Exception {
        System.out.println(String.format("[%s] value=%s", name, event.getValue()));
        Thread.sleep(millis);
    }

}

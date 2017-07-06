package com.gonwan.disruptor;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorSnippet {

    public static Disruptor<StringEvent> newDisruptor() {
        return new Disruptor<>(() -> new StringEvent(), 1024 * 32,
                r -> { return new Thread(r); }, ProducerType.SINGLE, new LiteBlockingWaitStrategy());
    }

    @SuppressWarnings("unchecked")
    public static void testSequence() {
        System.out.println("== testSequence ==");
        Disruptor<StringEvent> disruptor = newDisruptor();
        StringEventHandler h1 = new StringEventHandler("handler1", 30);
        StringEventHandler h2 = new StringEventHandler("handler2", 20);
        StringEventHandler h3 = new StringEventHandler("handler3", 10);
        disruptor.handleEventsWith(h1).then(h2).then(h3);
        disruptor.start();
        for (int i = 0; i < 10; i++) {
            int ii = i;
            disruptor.publishEvent((evt, seq) -> {
                evt.setValue("" + (ii));
            });
            /*
             * Sleep is necessary, otherwise _all_ new events are batch processed in the first handler
             * before passing to subsequent handlers. See {@link BatchEventProcessor}.
             */
            try {
                /* 100ms is large enough */
                Thread.sleep(100);
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
        disruptor.shutdown();
    }

    @SuppressWarnings("unchecked")
    public static void testDependency() {
        System.out.println("== testDependency ==");
        Disruptor<StringEvent> disruptor = newDisruptor();
        StringEventHandler h1 = new StringEventHandler("handler1", 50);
        StringEventHandler h2 = new StringEventHandler("handler2", 5);
        StringEventHandler h3 = new StringEventHandler("handler3", 10);
        disruptor.handleEventsWith(h1, h2).then(h3);
        disruptor.start();
        for (int i = 0; i < 10; i++) {
            int ii = i;
            disruptor.publishEvent((evt, seq) -> {
                evt.setValue("" + (ii));
            });
            try {
                /* 20ms is between 5ms and 50ms. So h2 finishes all events first. */
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
        disruptor.shutdown();
    }

    @SuppressWarnings("unchecked")
    public static void testComplexDependency() {
        System.out.println("== testComplexDependency ==");
        Disruptor<StringEvent> disruptor = newDisruptor();
        StringEventHandler h11 = new StringEventHandler("handler11", 50);
        StringEventHandler h12 = new StringEventHandler("handler12", 5);
        StringEventHandler h21 = new StringEventHandler("handler21", 50);
        StringEventHandler h22 = new StringEventHandler("handler22", 5);
        StringEventHandler h3 = new StringEventHandler("handler3", 10);
        disruptor.handleEventsWith(h11, h12);
        disruptor.after(h11).then(h21);
        disruptor.after(h12).then(h22);
        disruptor.after(h21, h22).then(h3);
        disruptor.start();
        for (int i = 0; i < 10; i++) {
            int ii = i;
            disruptor.publishEvent((evt, seq) -> {
                evt.setValue("" + (ii));
            });
            try {
                /* 20ms is between 5ms and 50ms. So h21/h22 finishes all events first. */
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
        disruptor.shutdown();
    }

    @SuppressWarnings("unchecked")
    public static void testWorkerPool() {
        System.out.println("== testWorkerPool ==");
        Disruptor<StringEvent> disruptor = newDisruptor();
        StringEventHandler h1 = new StringEventHandler("handler1", 10);
        StringEventHandler h2 = new StringEventHandler("handler2", 20);
        StringEventHandler h3 = new StringEventHandler("handler3", 30);
        StringEventHandler h4 = new StringEventHandler("handler4", 40);
        StringEventHandler h5 = new StringEventHandler("handler5", 50);
        disruptor.handleEventsWithWorkerPool(h1, h2, h3, h4, h5);
        disruptor.start();
        for (int i = 0; i < 100; i++) {
            int ii = i;
            disruptor.publishEvent((evt, seq) -> {
                evt.setValue("" + (ii));
            });
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
        disruptor.shutdown();
    }

    public static void main(String[] args) {
        //testSequence();
        //testDependency();
        //testComplexDependency();
        testWorkerPool();
    }

}

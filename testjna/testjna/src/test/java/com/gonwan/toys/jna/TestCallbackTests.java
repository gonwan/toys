package com.gonwan.toys.jna;

import com.sun.jna.Pointer;
import org.junit.jupiter.api.Test;

public class TestCallbackTests {

    @Test
    void test1() throws Exception {
        TestCallbackLibrary lib = TestCallbackLibrary.INSTANCE;
        /* print */
        lib.print("hahahaha\n");
    }

    @Test
    void test2() throws Exception {
        TestCallbackLibrary lib = TestCallbackLibrary.INSTANCE;
        /* pass array */
        TestCallbackLibrary.data_type1_t dt1 = new TestCallbackLibrary.data_type1_t();
        TestCallbackLibrary.data_type1_t[] dt1s = (TestCallbackLibrary.data_type1_t[]) dt1.toArray(2);
        dt1s[0].setValues(1, 11, 1.11, "11111".getBytes());
        dt1s[1].setValues(2, 22, 2.22, "22222".getBytes());
        TestCallbackLibrary.data_type2_t dt2 = new TestCallbackLibrary.data_type2_t();
        TestCallbackLibrary.data_type2_t[] dt2s = (TestCallbackLibrary.data_type2_t[]) dt2.toArray(2);
        dt2s[0].setValues(1, (byte) '1', (short) 11, 111111111111111L);
        dt2s[1].setValues(2, (byte) '2', (short) 22, 222222222222222L);
        TestCallbackLibrary.data_type2e_t.ByReference dt2e = new TestCallbackLibrary.data_type2e_t.ByReference();
        TestCallbackLibrary.data_type2e_t.ByReference[] dt2es = (TestCallbackLibrary.data_type2e_t.ByReference[]) dt2e.toArray(2);
        dt2es[0].setValues(33333);
        dt2es[1].setValues(44444);
        dt2s[0].setDD(dt2e, 2);
        dt2s[1].setDD(dt2e, 2);
        lib.test(dt1s, dt1s.length, dt2s, dt2s.length);
    }

    @Test
    void test3() throws Exception {
        TestCallbackLibrary lib = TestCallbackLibrary.INSTANCE;
        /* pass callbacks */
        TestCallbackLibrary.java_callbacks.ByValue cb = new TestCallbackLibrary.java_callbacks.ByValue();
        cb.on_test = () -> {
            System.out.println("callback test: 1111111");
            return 0;
        };
        cb.on_update_type1 = (pData, intSize) -> {
            TestCallbackLibrary.data_type1_t[] dt1Array = (TestCallbackLibrary.data_type1_t[]) pData.toArray(intSize);
            System.out.printf("callback data_type1: seq=%d, c1=%s, c2=%s\n", dt1Array[0].seq, new String(dt1Array[0].c).trim(), new String(dt1Array[1].c).trim());
            return 0;
        };
        cb.on_update_type2 = (pData, intSize) -> {
            TestCallbackLibrary.data_type2_t[] dt2Array = (TestCallbackLibrary.data_type2_t[]) pData.toArray(intSize);
            TestCallbackLibrary.data_type2e_t[] dt2eArray1 = (TestCallbackLibrary.data_type2e_t[]) dt2Array[0].dd.toArray(dt2Array[0].ddlen);
            TestCallbackLibrary.data_type2e_t[] dt2eArray2 = (TestCallbackLibrary.data_type2e_t[]) dt2Array[1].dd.toArray(dt2Array[1].ddlen);
            System.out.printf("callback data_type2: seq=%d, cc1=%d, cc1_aaa1=%d, cc2=%d, cc2_aaa2=%d\n",
                    dt2Array[0].seq, dt2Array[0].cc, dt2eArray1[0].aaa, dt2Array[1].cc, dt2eArray2[1].aaa);
            return 0;
        };
        Pointer mdh = lib.market_data_create();
        Pointer mdih = lib.market_data_if_create(cb);
        lib.market_data_add_callback(mdh, mdih);
        lib.market_data_run(mdh);
    }

    @Test
    void test4() throws Exception {
        TestCallbackLibrary lib = TestCallbackLibrary.INSTANCE;
        /* pass callbacks */
        TestCallbackLibrary.java_callbacks.ByValue cb = new TestCallbackLibrary.java_callbacks.ByValue();
        cb.on_perf = (pData, intSize) -> {
            System.out.println("callback on_perf");
            return 0;
        };
        Pointer mdh = lib.market_data_create();
        Pointer mdih = lib.market_data_if_create(cb);
        lib.market_data_add_callback(mdh, mdih);
        lib.market_data_run(mdh);
    }

}

package com.gonwan.toys.jna;

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;

/**
 * See: https://github.com/java-native-access/jna/blob/master/www/FrequentlyAskedQuestions.md
 */
public interface TestCallbackLibrary extends Library {

    TestCallbackLibrary INSTANCE = Native.load("marketjni", TestCallbackLibrary.class);

    void print(String s);

    class data_type1_t extends Structure {
        public int seq;
        public int a;
        public double b;
        public byte[] c = new byte[12];
        public static List<String> FIELDS = Arrays.asList("seq", "a", "b", "c");
        public data_type1_t() {
            super(ALIGN_NONE);
        }
        public void setValues(int seq, int a, double b, byte[] c) {
            this.seq = seq;
            this.a = a;
            this.b = b;
            this.c = c;
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class data_type2e_t extends Structure {
        public int aaa;
        public static List<String> FIELDS = Arrays.asList("aaa");
        public data_type2e_t() {
            super(ALIGN_NONE);
        }
        public void setValues(int aaa) {
            this.aaa = aaa;
        }
        public static class ByReference extends data_type2e_t implements Structure.ByReference { }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    };

    class data_type2_t extends Structure {
        public int seq;
        public byte aa;
        public short bb;
        public long cc;
        public data_type2e_t.ByReference dd;
        public int ddlen;
        public static List<String> FIELDS = Arrays.asList("seq", "aa", "bb", "cc", "dd", "ddlen");
        public data_type2_t() {
            super(ALIGN_NONE);
        }
        public void setValues(int seq, byte aa, short bb, long cc) {
            this.seq = seq;
            this.aa = aa;
            this.bb = bb;
            this.cc = cc;
        }
        public void setDD(data_type2e_t.ByReference dd, int ddlen) {
            this.dd = dd;
            this.ddlen = ddlen;
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    };

    class java_callbacks extends Structure {
        public static interface on_test_func extends Callback {
            int invoke();
        }
        public static interface on_perf_func extends Callback {
            int invoke(data_type1_t pData, int intSize);
        }
        public static interface on_update_type1_t_func extends Callback {
            int invoke(data_type1_t pData, int intSize);
        }
        public static interface on_update_type2_t_func extends Callback {
            int invoke(data_type2_t pData, int intSize);
        }
        public on_test_func on_test;
        public on_perf_func on_perf;
        public on_update_type1_t_func on_update_type1;
        public on_update_type2_t_func on_update_type2;
        public static List<String> FIELDS = Arrays.asList("on_test", "on_perf", "on_update_type1", "on_update_type2");
        public static class ByValue extends java_callbacks implements Structure.ByValue { }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    };

    void test(data_type1_t[] data1, int size1, data_type2_t[] data2, int size2);
    Pointer market_data_create();
    void market_data_destroy(Pointer mdh);
    void market_data_add_callback(Pointer mdh, Pointer mdih);
    void market_data_run(Pointer mdh);
    void market_data_stop(Pointer mdh);
    Pointer market_data_if_create(java_callbacks.ByValue cb);
    void market_data_if_destroy(Pointer mdih);

}

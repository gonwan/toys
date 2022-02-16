package com.gonwan.toys.idgendist.controller;

import com.netease.nim.camellia.id.gen.segment.CamelliaSegmentIdGen;
import com.netease.nim.camellia.id.gen.snowflake.CamelliaSnowflakeIdGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    private DecimalFormat decimalFormat = new DecimalFormat("###.00");

    @Autowired
    private CamelliaSnowflakeIdGen camelliaSnowflakeIdGen;

    @Autowired
    private CamelliaSegmentIdGen camelliaSegmentIdGen;

    @GetMapping("/snowflake")
    public Object snowflake(@RequestParam("loop") int loop) {
        long start = System.currentTimeMillis();
        for (int i = 1; i <= loop; i++) {
            if (i % 1000000 == 0) {
                logger.info("Running loop: {}/{}", i, loop);
            }
            camelliaSnowflakeIdGen.genId();
        }
        double qps = loop / ((System.currentTimeMillis() - start) / 1000.0);
        logger.info("Loop QPS: {}", decimalFormat.format(qps));
        return "OK";
    }

    @GetMapping("/segment")
    public Object segment(@RequestParam("loop") int loop) {
        long start = System.currentTimeMillis();
        for (int i = 1; i <= loop; i++) {
            if (i % 100000 == 0) {
                logger.info("Running loop: {}/{}", i, loop);
            }
            camelliaSegmentIdGen.genId("a");
        }
        double qps = loop / ((System.currentTimeMillis() - start) / 1000.0);
        logger.info("Loop QPS: {}", decimalFormat.format(qps));
        return "OK";
    }

}

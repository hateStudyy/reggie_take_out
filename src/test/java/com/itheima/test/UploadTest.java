package com.itheima.test;

import org.junit.jupiter.api.Test;

/**
 * @author coldwind
 * @version 1.0
 */
public class UploadTest {

    @Test
    public void test1(){
        String fileName = "ererewe.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        System.out.println(suffix);
    }
}

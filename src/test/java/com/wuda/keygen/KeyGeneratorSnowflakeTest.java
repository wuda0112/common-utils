package com.wuda.keygen;

import com.wuda.keygen.KeyGeneratorSnowflake;
import org.junit.Assert;
import org.junit.Test;

public class KeyGeneratorSnowflakeTest {

    @Test
    public void keyGen() {
        KeyGeneratorSnowflake snowflake = new KeyGeneratorSnowflake(1);
        int count = 0;
        long previous = 0;
        while (count < 100000) {
            count++;
            long current = 0;
            try {
                current = snowflake.next();
                System.out.println("key:" + current);
                Assert.assertTrue(current > previous); // 后生成的key必须大于之前生成的
                previous = current;
            } catch (KeyGenExceedMaxValueException | KenGenTimeBackwardsException e) {
                e.printStackTrace();
            }
        }
    }
}

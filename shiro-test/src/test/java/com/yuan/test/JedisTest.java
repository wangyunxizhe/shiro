package com.yuan.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * Created by wangy on 2018/10/15.
 */
public class JedisTest {

    @Test
    public void testLink() {
        Jedis jedis = new Jedis("160.6.92.79", 6379);
        jedis.set("wyuan", "123321");
        String val = jedis.get("wyuan");
        System.out.println(val);
        jedis.del("wyuan");
        jedis.close();
    }

}

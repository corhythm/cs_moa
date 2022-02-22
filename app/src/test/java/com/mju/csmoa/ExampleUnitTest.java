package com.mju.csmoa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void hashMapRemoveTest() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("크라운", "10.04");
        hashMap.put("CJ", "10.05");
        hashMap.put("해태제과", "10.011");

        hashMap.forEach((k, v) -> {
            System.out.print(hashMap.get(k));
        });
    }

}
package com.feasycom.feasyblue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

/**
 * Example local unit test, which will execute store_on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Stream<Integer> stream = numbers.stream();
        stream.filter((x) -> {
            return x % 2 == 0;
        }).map((x) -> {
            return x * x;
        }).forEach(System.out::println);
    }

    @Test
    public void test() throws InterruptedException {
        LinkedBlockingQueue<Byte> dataQueue = new LinkedBlockingQueue<Byte>(81920);
        dataQueue.put(new Byte("1"));
        System.out.println(dataQueue.size());
    }

    @Test
    public void t(){
        Collection<Integer> arr=new ArrayList<Integer>();
        arr.add(5);
        arr.add(10);
        arr.add(6);
        //迭代器迭代:在迭代的时候不能改变集合长度
        Iterator<Integer> it=arr.iterator();//获取对象
        //迭代
        while (it.hasNext()) {
            int i=it.next();//next()取下一个
            if (i==10) {
                System.out.println(i);
            }
        }
    }
}
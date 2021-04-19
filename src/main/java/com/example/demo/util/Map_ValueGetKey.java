package com.example.demo.util;

import java.util.*;

public class Map_ValueGetKey {
    HashMap map;

    public Map_ValueGetKey(HashMap map) { // 初始化操作
        this.map = map;
    }

    public Object getKey(Object value) {
        Object o = null;
        ArrayList all = new ArrayList(); // 建一个数组用来存放符合条件的KEY值

        /*
         * 这里关键是那个entrySet()的方法,它会返回一个包含Map.Entry集的Set对象.
         * Map.Entry对象有getValue和getKey的方法,利用这两个方法就可以达到从值取键的目的了 *
         */

        Set set = map.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(value)) {
                o = entry.getKey();
                all.add(o); // 把符合条件的项先放到容器中,下面再一次性打印出
            }
        }
        return all;
    }

}

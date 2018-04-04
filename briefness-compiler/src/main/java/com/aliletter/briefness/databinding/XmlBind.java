package com.aliletter.briefness.databinding;

import java.util.ArrayList;
import java.util.List;

/**
 * e-mail : aliletter@qq.com
 * time   : 2018/04/02
 * desc   :
 * version: 1.0
 */
public class XmlBind {
    public String clazz;
    public String name;
    public String alisa;
    public List<XmlViewInfo> list;

    public XmlBind(String clazz, String name, String alisa) {
        this.clazz = clazz;
        this.name = name;
        this.alisa = alisa;
        list = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "XmlBind{" +
                "clazz='" + clazz + '\'' +
                ", name='" + name + '\'' +
                ", alisa='" + alisa + '\'' +
                ", list=" + list +
                '}';
    }
}
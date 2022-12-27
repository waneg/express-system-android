package com.example.express.api;

import java.util.List;

public class ExpressInfoList {
    public List<ExpressInfoBean> getList() {
        return list;
    }

    public void setList(List<ExpressInfoBean> list) {
        this.list = list;
    }

    private List<ExpressInfoBean> list;
}

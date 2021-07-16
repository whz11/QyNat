package com.qingyou.qynat.commom.protocol;

/**
 * @author whz
 * @date 2021/7/16 10:04
 **/
public class TestMsg {
    String msg;
    String data;

    public String getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public TestMsg(String data, String msg){
        this.msg=msg;
        this.data=data;
    }
    public TestMsg(){

    }
}

package com.qingyou.qynat.commom.protocol;

import com.qingyou.qynat.commom.exception.QyNatException;

import java.util.Arrays;

/**
 * @author whz
 * @date 2021/7/15 20:24
 **/
public enum NatMessageType {
    /**
     * 注册
     */
    REGISTER(1),
    /**
     * 注册
     */
    REGISTER_RESULT(2),
    /**
     * 注册
     */
    CONNECTED(3),
    /**
     * 注册
     */
    DISCONNECTED(4),
    /**
     * 注册
     */
    DATA(5),
    /**
     * 注册
     */
    KEEPALIVE(6);

    private int code;

    NatMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static NatMessageType valueOf(int code) throws QyNatException {
        return Arrays.stream(NatMessageType.values())
                .filter(item -> item.code == code)
                .findAny()
                .orElseThrow(new QyNatException("NatMessageType code error: " + code));
    }
}

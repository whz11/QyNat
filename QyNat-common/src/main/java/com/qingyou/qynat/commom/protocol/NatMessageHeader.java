package com.qingyou.qynat.commom.protocol;

import lombok.Data;

/**
 * @author whz
 * @date 2021/7/15 20:33
 **/
@Data
public class NatMessageHeader {
    public static final int MAGIC_CODE = 0xabef0101;
    private long requestId;
    private String token;
    private NatMessageType natMessageType;
}

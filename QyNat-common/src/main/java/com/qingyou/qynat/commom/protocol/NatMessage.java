package com.qingyou.qynat.commom.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author whz
 * @date 2021/7/15 20:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NatMessage {
    private NatMessageHeader messageHeader;
    private Map<String, Object> metaData;
    private byte[] data;
}

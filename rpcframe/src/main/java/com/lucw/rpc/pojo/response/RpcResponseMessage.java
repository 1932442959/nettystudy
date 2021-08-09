package com.lucw.rpc.pojo.response;

import com.lucw.rpc.pojo.Message;
import lombok.Data;

@Data
public class RpcResponseMessage extends Message {

    /**
     * 返回值
     */
    private Object returnValue;

    /**
     * 异常值
     */
    private Exception exceptionValue;

    public RpcResponseMessage(Object returnValue, Exception exceptionValue) {
        this.returnValue = returnValue;
        this.exceptionValue = exceptionValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}

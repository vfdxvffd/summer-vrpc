package com.vfd.vrpc.message;

/**
 * @PackageName: com.vfd.message
 * @ClassName: RpcResponseMessage
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:02
 */
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    public RpcResponseMessage() {
    }

    public RpcResponseMessage(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
    }

    public RpcResponseMessage(Object returnValue) {
        this.returnValue = returnValue;
    }

    public RpcResponseMessage(Object returnValue, Exception exceptionValue) {
        this.returnValue = returnValue;
        this.exceptionValue = exceptionValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Exception getExceptionValue() {
        return exceptionValue;
    }

    public void setExceptionValue(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
    }
}

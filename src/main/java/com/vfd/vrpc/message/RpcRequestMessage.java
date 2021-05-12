package com.vfd.vrpc.message;

/**
 * @PackageName: com.vfd.message
 * @ClassName: RpcRequestMessage
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:01
 */
public class RpcRequestMessage extends Message {
    /**
     * 容器中的beanName服务端根据它找到实现
     */
    private String beanName;
    /**
     * 容器中的beanType服务端根据它找到实现
     */
    private Class<?> beanType;
    /**
     * 调用接口中的方法名
     */
    private String methodName;
    /**
     * 方法返回类型
     */
    private Class<?> returnType;
    /**
     * 方法参数类型数组
     */
    private Class<?>[] parameterTypes;
    /**
     * 方法参数值数组
     */
    private Object[] parameterValue;

    public RpcRequestMessage() {
    }

    public RpcRequestMessage(int sequenceId, String beanName, Class<?> beanType, String methodName, Class<?> returnType, Class<?>[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.beanName = beanName;
        this.beanType = beanType;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(Object[] parameterValue) {
        this.parameterValue = parameterValue;
    }
}

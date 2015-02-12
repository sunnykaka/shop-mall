package com.kariqu.tradecenter.excepiton;

/**
 * <pre>
 * 订单相关异常<br/>
 *   分为两种(都是 check 异常):
 *     1. 仅仅只是带了某些检查信息, 没有干扰到事务,
 *        spring 管理事务时不需要为了此种异常额外的去释放资源.
 *     2. 参与过数据库操作之后的某些异常, 又不能单独的 throw 一个 RuntimeException,
 *        让被调用的地方显示指定处理此种异常的同时并带有某些信息. 此种异常要导致事务回滚.
 *
 * 在同一个方法中, 先进行数据校验, 查询db并校验, 准备数据并创建更新删除等操作.
 * 做完一些创建更新删除的操作后, 有可能还会有其他数据的校验等,
 * 这时如果后面的数据不正常, 是应该事务回滚的.
 *
 * <span style="color:red">为了避免这种情况出现, 正确的做法是在进行 db 操作之前, 保证所有的数据校验都已经通过了.
 * 但是这么一来, 就可能会导致对 db 的多次查询操作</span>
 *
 * 当前此异常是第2种 >> db 操作之后的异常, 会事务回滚.
 * </pre>
 */
public class OrderTransactionalException extends OrderBaseException {

    public OrderTransactionalException() {}

    public OrderTransactionalException(String message) {
        super(message);
    }

    public OrderTransactionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderTransactionalException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

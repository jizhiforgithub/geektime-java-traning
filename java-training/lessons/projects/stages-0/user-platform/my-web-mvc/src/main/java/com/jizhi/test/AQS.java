package com.jizhi.test;

import sun.misc.Unsafe;

/**
 * 2021/3/11
 * jizhi7
 **/
public abstract class AQS {

    static class Node {
        //等待状态
        volatile int waitStatus;
        // 等待线程
        volatile Thread thread;
        // 上一节点
        volatile Node prev;
        // 下一节点
        volatile Node next;
        // 同步队列节点时，nextWaiter可能有两个值：EXCLUSIVE、SHARED标识当前节点是独占模式还是共享模式；
        // 在作为等待队列节点使用时，nextWaiter保存后继节点。
        Node nextWaiter;
    }

    // Unsafe类，底层操作CAS
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    // 资源状态的的地址偏移量
    private static final long stateOffset;
    // 链表头节点的地址偏移量
    private static final long headOffset;
    // 链表尾结点的地址偏移量
    private static final long tailOffset;
    // Node 节点的waitStatus状态地址偏移量
    private static final long waitStatusOffset;
    private static final long nextOffset;


    static {
        try {
            stateOffset = unsafe.objectFieldOffset
                    (AQS.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset
                    (AQS.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                    (AQS.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("next"));

        } catch (Exception ex) {
            throw new Error(ex);
        }
    }


    // 头节点
    private transient volatile Node head;
    // 尾节点
    private transient volatile Node tail;
    // 资源状态或数量
    private volatile int state;

    // 排他资源的拥有线程
    private transient Thread exclusiveOwnerThread;

    // 设置排他资源拥有的线程
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    // 过去排他资源拥有的线程
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }


    // 获取资源状态
    protected final int getState() {
        return state;
    }

    // 初始化资源状态
    protected final void setState(int newState) {
        state = newState;
    }

    // CAS更新资源状态
    protected final boolean compareAndSetState(int expect, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    // CAS设置链表的表头
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    // CAS设置链表的表尾，expect表尾节点
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    // CAS设置Node节点的nextWaiter状态
    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
    }

    // CAS更新链表的下一节点
    private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }

    // 入队，加在队列的尾部
    private Node enq(final Node node) {
        // 循环进行CAS操作
        for (; ; ) {
            Node t = tail;
            // 队列的尾节点为空，需要进行队列的初始化
            if (t == null) {
                // CAS设置头节点为一个空节点，头尾节点指向同一个节点
                if (compareAndSetHead(new Node()))
                    tail = head;
            }
            // 将节点插入队列尾部
            else {
                // CAS操作
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
}

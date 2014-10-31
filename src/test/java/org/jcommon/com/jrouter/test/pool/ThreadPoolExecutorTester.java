package org.jcommon.com.jrouter.test.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jcommon.com.jrouter.noio.ConnectionWorkerThread;

public class ThreadPoolExecutorTester {
	public static void main(String[] args){
		ThreadPoolExecutorTester tester = new ThreadPoolExecutorTester();
		ThreadPoolExecutor threadpool = tester.createThreadPool("321323123123");
		threadpool.execute(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("342232323");
			}
			
		});
	}
	
	private ThreadPoolExecutor createThreadPool(String connect_group_id) {
        // Create a pool of threads that will process queued packets.
    	ThreadPoolExecutor threadPool = new ConnectionWorkerThreadPool(1, 1, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new ConnectionsWorkerFactory(connect_group_id), new ThreadPoolExecutor.CallerRunsPolicy());
    	return threadPool;
    }
	
	private class ConnectionWorkerThreadPool extends ThreadPoolExecutor{
		public ConnectionWorkerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                TimeUnit unit,
                BlockingQueue<Runnable> workQueue,
                ThreadFactory threadFactory,
                RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
		handler);
		}
	}
	
	/**
     * Factory of threads where is thread will create and keep its own connection
     * to the server. If creating new connections to the server failes 2 consecutive
     * times then existing client connections will be closed.
     */
    private class ConnectionsWorkerFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final AtomicInteger failedAttempts = new AtomicInteger(0);

        public String connect_group_id;
        
        ConnectionsWorkerFactory(String connect_group_id) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.connect_group_id = connect_group_id;
        }

//		@Override
//		public Thread newThread(Runnable r) {
//			// TODO Auto-generated method stub
//			return new Thread(r);
//		}

        public Thread newThread(Runnable r) {
            // Create new worker thread that will include a connection to the server
            ConnectionWorkerThread t = new ConnectionWorkerThread(group, r,
                    "Connection Worker - " + threadNumber.getAndIncrement(), 0,connect_group_id);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            // Return null if failed to create worker thread
//            if (!t.isValid()) { 
//                return null;
//            }
            // Clean up the counter of failed attemps to create new connections
            failedAttempts.set(0);
            return t;
        }
    }
}

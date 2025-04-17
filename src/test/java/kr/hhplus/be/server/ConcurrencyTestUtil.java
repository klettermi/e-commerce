package kr.hhplus.be.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ConcurrencyTestUtil {
    /**
     * @param threads  동시 실행할 스레드 수
     * @param task     수행할 Runnable
     * @return         실행 중 발생한 Exception 리스트
     */
    public static List<Exception> runConcurrentCapturingErrors(int threads, Runnable task) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);
        List<Exception> errors = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                    try {
                        task.run();
                    } catch (Exception e) {
                        synchronized (errors) {
                            errors.add(e);
                        }
                    }
                } catch (InterruptedException ignored) {}
                done.countDown();
            }).start();
        }
        ready.await();    // 모든 스레드 준비 완료
        start.countDown(); // 동시 시작
        done.await();     // 모두 종료 대기
        return errors;
    }

    /** 예외는 무시하고 단순 실행만 검증할 때 */
    public static void runConcurrent(int threads, Runnable task) throws InterruptedException {
        runConcurrentCapturingErrors(threads, task);
    }
}

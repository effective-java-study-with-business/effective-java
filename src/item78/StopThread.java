package item78;

import java.util.concurrent.TimeUnit;

public class StopThread {
    private static boolean stopRequested; // false 초기화
    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            int i = 0;
            while(!stopRequested){
                i++;
                //System.out.println(i);
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}

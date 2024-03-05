package item80;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorFramework {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        executor.scheduleAtFixedRate(() -> {
            System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                .format(LocalDateTime.now()));
        }, 0, 2, TimeUnit.SECONDS);
    }
}

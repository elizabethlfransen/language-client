package io.github.elizabethlfransen.languageclient.util;

public class LambdaUtil {
    public static Runnable runIgnoringExceptions(ThrowableRunnable throwableRunnable) {
        return () -> {
            try {
                throwableRunnable.run();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        };
    }
}

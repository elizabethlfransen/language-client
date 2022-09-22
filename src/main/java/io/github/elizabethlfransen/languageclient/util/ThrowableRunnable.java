package io.github.elizabethlfransen.languageclient.util;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Throwable;
}

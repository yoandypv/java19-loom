package com.yoandypv.structured_concurrency;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Future;

public class BestPriceStructuredTaskScope extends StructuredTaskScope<BigDecimal> {

    private final Collection<BigDecimal> prices = new ConcurrentLinkedDeque<>();
    private final Collection<Throwable> errors = new ConcurrentLinkedDeque<>();

    @Override
    protected void handleComplete(Future<BigDecimal> future) {
       switch (future.state()) {
           case RUNNING -> throw new IllegalStateException("nothing todo ...");
           case SUCCESS -> this.prices.add(future.resultNow());
           case FAILED -> this.errors.add(future.exceptionNow());
           case CANCELLED -> {}
       }
    }

    private RuntimeException getException() {
        RuntimeException ex = new RuntimeException();
        this.errors.stream().forEach(ex::addSuppressed);
        return ex;
    }

    public BigDecimal getBetterPrice() {
        return this.prices.stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(this::getException);
    }

    public static void main(String[] args) {

    }
}

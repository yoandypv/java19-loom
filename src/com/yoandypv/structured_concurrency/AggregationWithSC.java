package com.yoandypv.structured_concurrency;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AggregationWithSC {

    //=============== StructuredTaskScope.ShutdownOnFailure ===========================

    // StructuredTaskScope.ShutdownOnFailure
    // Caso 1: Si falla una tarea se propaga la cancelacion a la otra.
    // Se evitan los thread leaks y se usa la propagacion de la cancelacion
    public static void sellProduct() throws ExecutionException, InterruptedException {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<Integer> inv = scope.fork(() -> updateInventory()); // falla
            Future<Integer> ord = scope.fork(() -> updateOrder()); // se cancela

            scope.join(); // unir los forks
            scope.throwIfFailed(); // si falla alguno propagar el error

            System.out.println("Result is inv = " + inv.resultNow() + " ord = " + ord.resultNow());
        }
    }

    public static Integer updateInventory() throws InterruptedException {
        return 1;
    }

    public static Integer updateOrder() {
        return 2;
    }

    //=============== StructuredTaskScope.ShutdownOnSuccess ===========================

    // StructuredTaskScope.ShutdownOnSuccess
    // Cerramos el scope con al menos una respuesta valida.
    // Evitamos seguir llamando otras operaciones paralelas
    // el propio scope realiza la cancelacion de los futuros en curso
    // si una tarea falla no es tenida en cuenta
    // si todas las tareas fallan se toma como causa raiz la excepcion de la primera
    public static Integer sell() throws ExecutionException, InterruptedException {

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Integer>()) {
            Future<Integer> t1 = scope.fork(() -> tienda1());
            Future<Integer> t2 = scope.fork(() -> tienda2());
            Future<Integer> t3 = scope.fork(() -> tienda3());

            scope.join();

            System.out.println("resultado t1="+t1.state()+"t2="+t2.state()+"t3="+t3.state());

            return scope.result();
        }
    }

    public static Integer tienda1() throws InterruptedException {
        Thread.sleep(1500);
        throw new RuntimeException();
       // return 1;
    }
    public static Integer tienda2() throws InterruptedException {
        Thread.sleep(2000);
        //throw new RuntimeException();
        return 2;
    }
    public static Integer tienda3() throws InterruptedException {
        Thread.sleep(40000);
        return 3;
    }

    //=============== Custom StructuredTaskScope ===========================

    // Obtener el mejor precio para el cliente de 3 tiendas

    public static BigDecimal bestPrice() throws ExecutionException, InterruptedException {

        try (var scope = new BestPriceStructuredTaskScope()) {
            Future<BigDecimal> t4 = scope.fork(() -> tienda4());
            Future<BigDecimal> t5 = scope.fork(() -> tienda5());
            Future<BigDecimal> t6 = scope.fork(() -> tienda6());

            scope.join();

            System.out.println("resultado t1="+t4.state()+"t2="+t5.state()+"t3="+t6.state());

            return scope.getBetterPrice();
        }
    }

    public static BigDecimal tienda4() throws InterruptedException {
        Thread.sleep(1500);
        return BigDecimal.valueOf(2.34);
    }
    public static BigDecimal tienda5() throws InterruptedException {
        Thread.sleep(2000);
        return BigDecimal.valueOf(1.89);
    }
    public static BigDecimal tienda6() throws InterruptedException {
        Thread.sleep(4000);
        return BigDecimal.valueOf(5.32);
    }



    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try {
           // sellProduct();
           // Integer r = sell();
           // System.out.println("StructuredTaskScope.ShutdownOnSuccess result is " + r);
            BigDecimal best = bestPrice();
            System.out.println("Custom StructuredTaskScope result is " + best);
        } catch (InterruptedException e) {
            System.out.println("---");
        }

    }
}

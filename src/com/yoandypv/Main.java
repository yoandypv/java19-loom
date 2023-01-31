package com.yoandypv;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Thread.startVirtualThread(() -> {
            System.out.println("Hello world");
        });
    }
}

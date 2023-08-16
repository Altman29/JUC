package org.example;

import java.util.concurrent.locks.LockSupport;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        LockSupport.park();
    }
}
package com.example;

import net.thauvin.erik.bitly.Bitly;

public class BitlySample {
    public static void main(String[] args) {
        System.out.println(new Bitly("YOUR_API_KEY").bitlinks().shorten("https://erik.thauvin.net/blog"));
        System.exit(0);
    }
}

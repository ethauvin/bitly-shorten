package com.example;

import net.thauvin.erik.bitly.Bitly;

public final class BitlySample {
    public static void main(final String[] args) {
        if (args.length > 0) {
            final Bitly bitly = new Bitly(/* "YOUR_API_TOKEN from https://bitly.is/accesstoken" */);
            if (!bitly.getAccessToken().isEmpty()) {
                for (final String arg : args) {
                    if (arg.contains("bit.ly")) {
                        System.out.println(arg + " <-- " + bitly.bitlinks().expand(arg));
                    } else {
                        System.out.println(arg + " --> " + bitly.bitlinks().shorten(arg));
                    }
                }
            } else {
                System.err.println("Please specify a Bitly API access token.");
            }
        } else {
            System.err.println("Try specifying one or more URLs as arguments.");
        }


        System.exit(0);
    }
}

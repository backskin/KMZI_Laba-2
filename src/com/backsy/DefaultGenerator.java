package com.backsy;

import java.util.Random;

import static java.lang.Math.abs;

public class DefaultGenerator implements RandomBitGenerator {

    private Random r;

    private DefaultGenerator(){
        r = new Random();
    }

    public static RandomBitGenerator factory(){
        return new DefaultGenerator();
    }

    @Override
    public int next() {
        return r.nextInt(2);
    }
}

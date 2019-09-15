package com.backsy;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.StrictMath.pow;

public class BBSGenerator implements RandomBitGenerator {

    private static long gcd(long n, long m){
        while (n != 0 && m != 0) {
            if (n > m) n %= m;
            else m %= n;
        }
        return m + n;
    }

    private static boolean isPrime(int n) {
        if ((n < 2) || n%2 == 0)
            return false;
        if (n < 9)
            return true;
        if (n % 3 == 0)
            return false;
        int r = (int) pow(n, 0.5);
        int f = 5;
        while (f <= r) {
            if (n % f == 0 || n % (f + 2) == 0)
                return false;
            f += 6;
        }
        return true;
    }

    private static int prime() {

        int border = (new Random()).nextInt(100000);
        int last = 3;
        for (int i = 3; i < border; i++) {
            if (isPrime(i)) last = i;
        }
        return last;
    }

    private long X;
    private long M;

    private BBSGenerator(){

        int p = 0;
        int q = 0;
        while (!(p % 4 == 3)){
            p = prime();
        }
        while (!(q % 4 == 3) || p == q){
            q = prime();
        }
        M =  (long) p * q;
        long s = 0;
        while (s == 0 || gcd(s, M) != 1){
            s = abs((new Random()).nextLong()) % (M-1);
        }
        s = (long) pow(s, 2) % M;
        X = (long) pow(s, 2) % M;
    }

    @Override
    public int next(){

        int result = (int)(X % 2);
        X = ((long) pow(X,2)) % M;
        return result;
    }

    static BBSGenerator factory(){

        return new BBSGenerator();
    }

}

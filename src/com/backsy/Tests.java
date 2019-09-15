package com.backsy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.StrictMath.*;
import static org.apache.commons.math3.special.Erf.erfc;
import static org.apache.commons.math3.special.Gamma.regularizedGammaQ;

class Tests {

    private static double ALPHA = 0.01;
    private static PrintWriter statsWriter = null;

    private static void createStats(String name, File folder){
        try {
            String path = "";
            if (folder != null)
                path = folder.getPath() + "\\";
            path += "stats-" + name + ".txt";
            statsWriter = new PrintWriter(path, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void writeToStats(String info){
        if (statsWriter == null)
            createStats("unknown", null);

        statsWriter.print(info);
     }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
                        U N I V E R S A L  T E S T
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     static void universalMaurer(File folder, List<Byte> bitSequence) {

         int n = bitSequence.size();
         int decRep;

         HashMap<Integer, Long> T = new HashMap<>();

         double[] expected_value = {0, 0, 0, 0, 0, 0, 5.2177052, 6.1962507, 7.1836656,
                 8.1764248, 9.1723243, 10.170032, 11.168765,
                 12.168070, 13.167693, 14.167488, 15.167379};

         double[] variance = {0, 0, 0, 0, 0, 0, 2.954, 3.125, 3.238, 3.311, 3.356, 3.384,
                 3.401, 3.410, 3.416, 3.419, 3.421};
         int L = 5;
         if (n >= 387840) L = 6;
         if (n >= 904960) L = 7;
         if (n >= 2068480) L = 8;
         if (n >= 4654080) L = 9;
         if (n >= 10342400) L = 10;
         if (n >= 22753280) L = 11;
         if (n >= 49643520) L = 12;
         if (n >= 107560960) L = 13;
         if (n >= 231669760) L = 14;
         if (n >= 496435200) L = 15;
         if (n >= 1059061760) L = 16;

         createStats("maurersUniversal", folder);

         int Q = 10 * (int) pow(2, L);
         int K = (int) (floor((double) n / L) - (double) Q);                /* BLOCKS TO TEST */

         int p = (int) pow(2, L);
         if ((L < 6) || ((double) Q < 10 * pow(2, L))) {
             System.out.println("ERROR: L IS OUT OF RANGE -OR- Q IS LOW");
             writeToStats("\t\tUNIVERSAL STATISTICAL TEST\n");
             writeToStats("\t\t---------------------------------------------\n");
             writeToStats("\t\tERROR:  L IS OUT OF RANGE.\n");
             writeToStats("\t\t-OR- :  Q IS LESS THAN " + 10 * pow(2, L) + ".");
             statsWriter.close();
             statsWriter = null;
             return;
         }

         double c = 0.7 - 0.8 / (double) L + (4 + 32 / (double) L) * pow(K, - 3 / (double) L) / 15;
         double sigma = c * sqrt(variance[L] / (double) K);
         double sqrt2 = sqrt(2);
         double sum = 0.0;
         for (int i = 0; i < p; i++)
             T.put(i, (long) 0);
         for (int i = 1; i <= Q; i++) {        /* INITIALIZE TABLE */
             decRep = 0;
             for (int j = 0; j < L; j++)
                 decRep += bitSequence.get((i - 1) * L + j) * (long) pow(2, L - 1 - j);
             T.put(decRep, (long) i);
         }
         for (int i = Q + 1; i <= Q + K; i++) {    /* PROCESS BLOCKS */
             decRep = 0;
             for (int j = 0; j < L; j++)
                 decRep += bitSequence.get((i - 1) * L + j) * (long) pow(2, L - 1 - j);
             sum += log(i - T.get(decRep)) / log(2);
             T.put(decRep, (long) i);
         }
         double phi = sum / (double) K;

         writeToStats("\t\tUNIVERSAL STATISTICAL TEST\n");
         writeToStats("\t\t--------------------------------------------\n");
         writeToStats("\t\tCOMPUTATIONAL INFORMATION:\n");
         writeToStats("\t\t--------------------------------------------\n");
         writeToStats(  "\t\t(a) L         = " + L);
         writeToStats("\n\t\t(b) Q         = " + Q);
         writeToStats("\n\t\t(c) K         = " + K);
         writeToStats("\n\t\t(d) sum       = " + sum);
         writeToStats("\n\t\t(e) sigma     = " + sigma);
         writeToStats("\n\t\t(f) variance  = " + variance[L]);
         writeToStats("\n\t\t(g) exp_value = " + expected_value[L]);
         writeToStats("\n\t\t(h) phi       = " + phi);
         writeToStats("\n\t\tWARNING:  " + (n - (Q + K) * L) + " bits were discarded.\n");
         writeToStats("\t\t-----------------------------------------\n");

         double p_value = erfc(abs(phi - expected_value[L]) / (sqrt2 * sigma));
         if (p_value < 0.0000 || p_value > 1.0000)
             writeToStats("\t\tWARNING:  P_VALUE IS OUT OF RANGE\n");

         writeToStats( (p_value < ALPHA ? "  FAILURE" : "  SUCCESS") + "\t\tp_value = " + p_value);
         statsWriter.close();
         statsWriter = null;
     }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
                A P P R O X I M A T E  E N T R O P Y   T E S T
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    static void approximateEntropy(File folder, List<Byte> bitSequence)
    {
        int m = 10;
        int n = bitSequence.size();
        double[] ApEn = new double[2];
        HashMap<Integer, Integer> P = new HashMap<>();

        createStats("approximateEntropy", folder);

        writeToStats( "\t\t\tAPPROXIMATE ENTROPY TEST\n");
        writeToStats("\t\t--------------------------------------------\n");
        writeToStats("\t\tCOMPUTATIONAL INFORMATION:\n");
        writeToStats("\t\t--------------------------------------------\n");
        writeToStats("\t\t(a) m (block length)    = " + m + "\n");

        int v = (int) (log(n) / log(2)) - 5;
        if (m > v) {
            writeToStats("\n\t\tNote: The blockSize = " + m + " exceeds recommended value of " + v);
            writeToStats("\t\tResults are inaccurate!\n");
            writeToStats("\t\t--------------------------------------------\n");
        }

        for (int iglobal = 0; iglobal < 2; iglobal++ ) {

            if ( m + iglobal == 0 ) {
                ApEn[0] = 0.00;
                continue;
            }

            int powLen = (int) pow(2, m + iglobal + 1) - 1;

            for (int j = 1; j < powLen - 1; j++)
                P.put(j, 0);
            for (int i1 = 0; i1 < n; i1++) { /* COMPUTE FREQUENCY */
                int k = 1;
                for (int i2 = 0; i2 < m + iglobal; i2++) {
                    k <<= 1;
                    if ( (int)bitSequence.get((i1 + i2) % n) == 1 )
                        k++;
                }
                P.put(k-1, (P.get(k-1) == null ? 0 : P.get(k-1)) +1);
            }
            /* DISPLAY FREQUENCY */
            double sum = 0.0;
            int index = (int) pow(2, m + iglobal) - 1;
            for ( int i = 0; i < (int) pow(2, m + iglobal); i++ ) {
                if ( P.get(index) != null && P.get(index) > 0 )
                    sum += P.get(index)*log(P.get(index)/ (double) n);
                index++;
            }
            sum /= 0.0 + n;
            ApEn[iglobal] = sum;
        }
        double apen = ApEn[0] - ApEn[1];
        double chi_squared = 2.0 * n * (log(2) - apen);
        double p_value = regularizedGammaQ(pow(2, m - 1), chi_squared / 2.0);

        writeToStats( "\t\t(b) n (sequence length) = " + n);
        writeToStats( "\n\t\t(c) Chi^2               = " + chi_squared);
        writeToStats("\n\t\t(d) Phi(m)	       = " + ApEn[0]);
        writeToStats("\n\t\t(e) Phi(m+1)	       = " + ApEn[1]);
        writeToStats("\n\t\t(f) ApEn                = " + apen);
        writeToStats("\n\t\t(g) Log(2)              = " + log(2.0));
        writeToStats("\n\t\t--------------------------------------------\n");

        writeToStats((p_value < ALPHA ? "FAILURE" : "SUCCESS") +"\t\tp_value = " + p_value);
        statsWriter.close();
        statsWriter = null;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
           R A N D O M  E X C U R S I O N S  V A R I A N T  T E S T
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    static void randomExcursionsVariant(File folder, List<Byte> bitSequence) {

        int n = bitSequence.size();
        List<Integer> S_k = new ArrayList<>();
        int[] stateX = {-9, -8, -7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        double p_value;

        S_k.add(2 * bitSequence.get(0) - 1);

        int J = 0;
        for (int i = 1; i < n; i++) {
            int tmp = S_k.get(i - 1) + 2 * bitSequence.get(i) - 1;
            S_k.add(i, tmp);
            if (tmp == 0)
                J++;

        }
        if (S_k.get(n - 1) != 0)
            J++;

        createStats("randExcursionsVar", folder);

        writeToStats("\t\t\tRANDOM EXCURSIONS VARIANT TEST\n");
        writeToStats("\t\t--------------------------------------------\n");
        writeToStats("\t\tCOMPUTATIONAL INFORMATION:\n");
        writeToStats("\t\t--------------------------------------------\n");
        writeToStats("\t\t(a) Number Of Cycles (J) = " + J);
        writeToStats("\n\t\t(b) Sequence Length (n)  = " + n);
        writeToStats("\n\t\t--------------------------------------------\n");

        if (J < 250) { //(int) 0.005 * pow(n, 0.5)) {
            writeToStats("\n\t\tWARNING:  TEST NOT APPLICABLE.  THERE ARE AN\n");
            writeToStats("\t\t\t  INSUFFICIENT NUMBER OF CYCLES.\n");
            writeToStats("\t\t---------------------------------------------\n");
        }

        for (int p = 0; p < 18; p++) {
            int x = stateX[p];
            int count = 0;
            for (int i = 0; i < n; i++)
                if (S_k.get(i) == x)
                    count++;
            p_value = erfc(abs(count - J) / (sqrt(2.0 * J * (4.0 * abs(x) - 2))));

            if (p_value < 0.0000 || p_value > 1.0000)
                writeToStats("\t\t(b) WARNING: P_VALUE IS OUT OF RANGE.\n");
            writeToStats("\t\t" + (p_value < ALPHA ? "FAILURE" : "SUCCESS"));
            writeToStats("(x = " + x + ") Total visits = " + count + "; p-value = " + p_value + "\n");
        }
        writeToStats( "\n");
        statsWriter.close();
        statsWriter = null;
    }
}

package com.backsy;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    private static File workfolder;

    private static List<Byte> seqFromFile(String path){
        List<Byte> sequence = new ArrayList<>();
        File file = new File(path);

        if (!file.exists()){
            System.out.println("ERROR: File \'" + path +"\' not found!");
        }

        try {
            byte[] array = Files.readAllBytes(file.toPath());
            for (byte b : array) {
                if (b-48 == 1 || b-48 == 0)
                    sequence.add((byte)(b - 48));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sequence;
    }

    private static List<Byte> generateMillionBits(RandomBitGenerator a) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(workfolder.getPath() + "\\generated.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            System.out.println("!!Error during generating a sequence!!");
            return new ArrayList<>();
        }
        for (int i = 0; i < 1000000; i++) {
            writer.print(a.next());
        }
        writer.close();
        System.out.print("Sequence generated! ");
        return seqFromFile(workfolder.getPath() + "\\generated.txt");
    }


    private static void createInstanceFolder(String index){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String date = dtf.format( LocalDateTime.now());
        workfolder = new File( "experiment-" + date + index);
        boolean created =  workfolder.mkdir();
        if(created)
            System.out.println("Folder '" + workfolder.getPath() + "' was created");
        else
            System.out.println("Unable to create folder");
    }

    private static int seqPeriod(List<Byte> list){

        if (list.size() < 2)
            return 1;

        List<Byte> segment = new ArrayList<>();
        segment.add(list.get(0));

        int i = 0;
        while (i < list.size() / segment.size() - 1) {

            boolean repeating = true;

            for (int j = 0; j < segment.size(); j++) {
                if (!Objects.equals(segment.get(j), list.get((i+1) * segment.size() + j))) {
                    repeating = false;
                    break;
                }
            }

            if (!repeating){
                i = 0;
                segment.add(list.get(segment.size()));
            } else {
                i += 1;
            }
        }
        if (list.size() <= 2*segment.size())
            return list.size();
        else
            return segment.size();
    }

    public static void main(String[] args) {

        List<Byte> sequence;

        /*
        *    Repeated Sequence Test
        *
        * */
        System.out.println("=== Cycled sequence testing ===");
        createInstanceFolder("-Cy");

        sequence = seqFromFile("opa.txt");
        System.out.print("File read!");
        System.out.println(" (Seq period = " + seqPeriod(sequence) + ")");

        System.out.println("Test 1 is in process...");
        Tests.universalMaurer(workfolder, sequence);
        System.out.println("Test 1 is complete!");

        System.out.println("Test 2 is in process...");
        Tests.randomExcursionsVariant(workfolder, sequence);
        System.out.println("Test 2 is complete!");

        System.out.println("Test 3 is in process...");
        Tests.approximateEntropy(workfolder, sequence);
        System.out.println("Test 3 is complete!");

        /*
        *       Pi Test
        * */

        System.out.println("\n=== Pi testing ===");
        createInstanceFolder("-Pi");

        sequence = seqFromFile("pi");
        System.out.print("File read!");
        System.out.println(" (Seq period = " + seqPeriod(sequence) + ")");

        System.out.println("Test 1 is in process...");
        Tests.universalMaurer(workfolder, sequence);
        System.out.println("Test 1 is complete!");

        System.out.println("Test 2 is in process...");
        Tests.randomExcursionsVariant(workfolder, sequence);
        System.out.println("Test 2 is complete!");

        System.out.println("Test 3 is in process...");
        Tests.approximateEntropy(workfolder, sequence);
        System.out.println("Test 3 is complete!");

        /*
        *        Default Java Generator test
        * */
        System.out.println("\n=== Default Generator testing ===");

        createInstanceFolder("-DefJava");

        sequence = generateMillionBits(DefaultGenerator.factory());
        System.out.println(" (Seq period = " + seqPeriod(sequence) + ")");

        System.out.println("Test 1 is in process...");
        Tests.universalMaurer(workfolder, sequence);
        System.out.println("Test 1 is complete!");

        System.out.println("Test 2 is in process...");
        Tests.randomExcursionsVariant(workfolder, sequence);
        System.out.println("Test 2 is complete!");

        System.out.println("Test 3 is in process...");
        Tests.approximateEntropy(workfolder, sequence);
        System.out.println("Test 3 is complete!");

        /*
         *      BLUM-BLUM-SHUB generator test
         * */

        System.out.println("\n=== My own generator testing ===");

        createInstanceFolder("-BBS");

        sequence = generateMillionBits(BBSGenerator.factory());
        System.out.println(" (Seq period = " + seqPeriod(sequence) + ")");

        System.out.println("Test 1 is in process...");
        Tests.universalMaurer(workfolder, sequence);
        System.out.println("Test 1 is complete!");

        System.out.println("Test 2 is in process...");
        Tests.randomExcursionsVariant(workfolder, sequence);
        System.out.println("Test 2 is complete!");

        System.out.println("Test 3 is in process...");
        Tests.approximateEntropy(workfolder, sequence);
        System.out.println("Test 3 is complete!");
    }
}

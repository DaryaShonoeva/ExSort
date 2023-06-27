import java.io.*;
import java.util.*;

public class ExternalSort {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "input.txt";
        String outputFilePath = "output.txt";
        int chunkSize = 5;

        splitFile(inputFilePath, chunkSize);
        sortChunks(chunkSize);
        mergeSortedChunks(chunkSize, outputFilePath);

        System.out.println("Sorted file in output.txt.");
    }

    private static void splitFile(String inputFilePath, int chunkSize) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
        String line;
        int chunkIndex = 0;
        BufferedWriter writer = null;

        while ((line = reader.readLine()) != null) {
            if (chunkIndex % chunkSize == 0) {
                if (writer != null) {
                    writer.close();
                }
                writer = new BufferedWriter(new FileWriter("chunk" + chunkIndex + ".txt"));
            }

            writer.write(line);
            writer.newLine();
            chunkIndex++;
        }

        if (writer != null) {
            writer.close();
        }

        reader.close();
    }

    private static void sortChunks(int chunkSize) throws IOException {
        File folder = new File(".");
        File[] chunkFiles = folder.listFiles((dir, name) -> name.startsWith("chunk"));

        for (File chunkFile : chunkFiles) {
            List<String> lines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(chunkFile))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            Collections.sort(lines);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile))) {
                for (String sortedLine : lines) {
                    writer.write(sortedLine);
                    writer.newLine();
                }
            }
        }
    }


    private static void mergeSortedChunks(int chunkSize, String outputFilePath) throws IOException {
        File folder = new File(".");
        File[] chunkFiles = folder.listFiles((dir, name) -> name.startsWith("chunk"));
        BufferedReader[] readers = new BufferedReader[chunkFiles.length];

        for (int i = 0; i < chunkFiles.length; i++) {
            readers[i] = new BufferedReader(new FileReader(chunkFiles[i]));
        }

        PriorityQueue<String> minHeap = new PriorityQueue<>(chunkFiles.length);

        for (int i = 0; i < chunkFiles.length; i++) {
            String line = readers[i].readLine();
            if (line != null) {
                minHeap.add(line + ";" + i); // ????????? ????? ????? ? ??????
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

        while (!minHeap.isEmpty()) {
            String minLine = minHeap.poll();
            String[] lineParts = minLine.split(";");
            int partIndex = Integer.parseInt(lineParts[1]);

            writer.write(lineParts[0]);
            writer.newLine();

            String nextLine = readers[partIndex].readLine();
            if (nextLine != null) {
                minHeap.add(nextLine + ";" + partIndex);
            }
        }

        writer.close();

        for (File chunkFile : chunkFiles) {
            chunkFile.delete();
        }
    }

}
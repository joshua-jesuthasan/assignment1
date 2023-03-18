package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;

public class SpamDetector {

    public List<TestFile> performTrainAndTest(File mainDirectory) throws IOException {
        Map<String, Integer> spamWordFrequencies = new TreeMap<>();
        Map<String, Integer> hamWordFrequencies = new TreeMap<>();
        Map<String, Double> wordSpamProbabilities = new TreeMap<>();
        List<TestFile> testFilesResult = new ArrayList<>();

        spamWordFrequencies = computeWordFrequencies("data/test");
        hamWordFrequencies = computeWordFrequencies("data/train");

        for (String word : spamWordFrequencies.keySet()) {
            int spamWordCount = spamWordFrequencies.get(word);
            int hamWordCount = hamWordFrequencies.getOrDefault(word, 0);
            double spamProbabilityGivenWord = (double) spamWordCount / (spamWordCount + hamWordCount);
            wordSpamProbabilities.put(word, spamProbabilityGivenWord);
        }

        File[] testFiles = mainDirectory.listFiles();
        for (File file : testFiles) {
            double logProbabilityDifference = 0.0;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.toLowerCase().split("\\W+");
                    for (String word : words) {
                        if (isValidWord(word) && wordSpamProbabilities.containsKey(word)) {
                            logProbabilityDifference += (Math.log(1 - wordSpamProbabilities.get(word)) - Math.log(wordSpamProbabilities.get(word)));
                        }
                    }
                }
            }
            double spamProbabilityGivenFile = 1 / (1 + Math.pow(Math.E, logProbabilityDifference));
            testFilesResult.add(new TestFile(file.getName(), spamProbabilityGivenFile));
        }

        return testFilesResult;
    }

    private Map<String, Integer> computeWordFrequencies(String directory) throws IOException {
        File dir = new File(directory);
        File[] files = dir.listFiles();
        Map<String, Integer> wordFrequencyMap = new TreeMap<>();

        for (File file : files) {
            Map<String, Integer> wordCountMap = countWordsInFile(file);
            for (String word : wordCountMap.keySet()) {
                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + wordCountMap.get(word));
            }
        }
        return wordFrequencyMap;
    }

    private Map<String, Integer> countWordsInFile(File file) throws IOException {
        Map<String, Integer> wordCountMap = new TreeMap<>();

        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase();
                    if (isValidWord(word)) {
                        wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
        return wordCountMap;
    }

    private boolean isValidWord(String word) {
        if (word == null) {
            return false;
        }

        String pattern = "^[a-zA-Z]*$";
        return word.matches(pattern);
    }
}



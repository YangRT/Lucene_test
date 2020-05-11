package com.yang.lec;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimilarityUtil {


    public static void main(String[] args) {

      //  System.out.println(getSimilarity("经济贸易学院的巴基斯坦外教是谁?","来自经济贸易学院的陈同学告诉我们，一开始丹尼斯讲课的语速很慢，后来才知道是因为老师之前为了照顾部分一些使用其他语言的外国研究生同学而降低了语速，收到同学反馈之后老师马上调整了过来，班上的学习的效率也提高了很多"));
    }

    static {
        CustomDictionary.add("子类");
        CustomDictionary.add("父类");
    }

    private SimilarityUtil() {
    }

    //计算两个句子的相似度
    public static double getSimilarity(String sentence1, String sentence2) {
        List<String> sent1Words = getSplitWords(sentence1);
        List<String> sent2Words = getSplitWords(sentence2);
        List<String> stopWords = getStopWords();
        sent1Words.removeAll(stopWords);
        sent2Words.removeAll(stopWords);
        List<String> allWords = mergeList(sent1Words, sent2Words);

        int[] statistic1 = statistic(allWords, sent1Words);
        int[] statistic2 = statistic(allWords, sent2Words);

        double dividend = 0;
        double divisor1 = 0;
        double divisor2 = 0;
        for (int i = 0; i < statistic1.length; i++) {
            dividend += statistic1[i] * statistic2[i];
            divisor1 += Math.pow(statistic1[i], 2);
            divisor2 += Math.pow(statistic2[i], 2);
        }

        return dividend / (Math.sqrt(divisor1) * Math.sqrt(divisor2));
    }

    private static int[] statistic(List<String> allWords, List<String> sentWords) {
        int[] result = new int[allWords.size()];
        for (int i = 0; i < allWords.size(); i++) {
            result[i] = Collections.frequency(sentWords, allWords.get(i));
        }
        return result;
    }

    private static List<String> mergeList(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();
        result.addAll(list1);
        result.addAll(list2);
        return result.stream().distinct().collect(Collectors.toList());
    }

    private static List<String> getSplitWords(String sentence) {
        // 标点符号会被单独分为一个Term，去除之
        return HanLP.segment(sentence).stream().map(a -> a.word).filter(s -> !"`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？ ".contains(s)).collect(Collectors.toList());
    }

    private static List<String> getStopWords(){
        // 中文 停用词 .txt 文件路径
        String filePath = "D:\\IDEA projects\\test\\chineseStopWords.txt";
        File file = new File(filePath);
        List<String> stopWords = new ArrayList<>();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                //System.out.println("*" + temp+ "*");
                stopWords.add(temp.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stopWords;
    }
}


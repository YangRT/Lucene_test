package com.yang.lec;


import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.BM25Similarity;



public class MySimilarity extends ClassicSimilarity {

    // 直接返回 tf
    @Override
    public float tf(float freq) {
        return freq;
    }

    // 计算idf  公式： log ( (docCount+1) / (docFreq+1) )
    @Override
    public float idf(long docFreq, long docCount) {
        return (float)(Math.log((double)(docCount + 1L) / (double)(docFreq + 1L)));
    }

    // 比较：
    // IndexSearcher 默认Similarity为 BM25Similarity
    // 使用BM25算法 调节因子k1和b默认值为1.2F, 0.75F
    // 其idf计算 ：
    // (float)Math.log(1.0D + ((double)(docCount - docFreq) + 0.5D) / ((double)docFreq + 0.5D))
    // 对于同一词 ‘图书馆’ 进行搜索
    // 使用BM25Similarity 返回新闻文档顺序为 new_15.txt new_6.txt new_11.txt
    // 使用MySimilarity 返回新闻文档顺序为 new_15.txt new_11.txt new_20.txt
    // 使用 StandardAnalyzer 按单字进行分词，统计新闻文本：
    // new_15.txt 出现 4 次 ‘图书馆’
    // new_6.txt 出现 ‘图书馆‘ 1次
    // new_11.txt 出现 1 次 ’书‘  8次 ’馆‘
    // new_20.txt 仅出现 ‘图’6次
    // 根据结果可知 使用BM25Similarity效果更好，考虑查询词的完整，将new_6.txt靠前返回
    // 使用MySimilarity由于 new_11.txt new_20.txt 中匹配到的单字出现次数多 导致排序靠前




}

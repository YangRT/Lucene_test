package com.yang.lec;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class IndexSearchUtils {

    /**
     * 索引查询
     *
     * @param indexDir  ：Lucene 索引文件所在目录
     * @param queryWord ：检索的内容，默认从文章内容进行查询
     * @throws Exception
     */
    public static String indexSearch(File indexDir, String queryWord) throws Exception {
        if (indexDir == null || queryWord == null || "".equals(queryWord)) {
            return null;
        }
        /** 创建分词器
         * 1）创建索引 与 查询索引 所用的分词器必须一致
         */
        //IKAnalyzer analyzer = new IKAnalyzer();
        //StandardAnalyzer analyzer = new StandardAnalyzer();
        CJKAnalyzer analyzer = new CJKAnalyzer();
        QueryParser queryParser = new QueryParser("fileName", analyzer);


        Query query = queryParser.parse("fileContext:" + queryWord);

        /** 与创建 索引 和 Lucene 文档 时一样，指定 索引和文档 的目录
         * 即指定查询的索引库
         */
        Path path = Paths.get(indexDir.toURI());
        Directory dir = FSDirectory.open(path);

        /*** 创建 索引库读 对象
         * DirectoryReader 继承于org.apache.lucene.index.IndexReader
         * */
        DirectoryReader directoryReader = DirectoryReader.open(dir);

        /** 根据 索引对象创建 索引搜索对象
         **/
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
//        MySimilarity similarity = new MySimilarity();
//        indexSearcher.setSimilarity(similarity);
        /**search(Query query, int n) 搜索
         * 第一个参数：查询语句对象
         * 第二个参数：指定查询最多返回多少条数据，此处则表示返回个数最多5条
         */
        TopDocs topdocs = indexSearcher.search(query, 1);

        //System.out.println("查询结果总数：：：=====" + topdocs.totalHits);


        /**从搜索结果对象中获取结果集
         * 如果没有查询到值，则 ScoreDoc[] 数组大小为 0
         * */
        ScoreDoc[] scoreDocs = topdocs.scoreDocs;
        int id = scoreDocs[0].doc;
        Document document = directoryReader.document(id);
        System.out.println(document.get("fileName"));
        return document.get("fileName");
//        ScoreDoc loopScoreDoc = null;
//        for (int i = 0; i < scoreDocs.length; i++) {
//
//            System.out.println("=======================" + (i + 1) + "=====================================");
//            loopScoreDoc = scoreDocs[i];
//
//            /**获取 文档 id 值
//             * 这是 Lucene 存储时自动为每个文档分配的值，相当于 Mysql 的主键 id
//             * */
//            int docID = loopScoreDoc.doc;
//
//            /**通过文档ID从硬盘中读取出对应的文档*/
//            Document document = directoryReader.document(docID);
//
//            /**get方法 获取对应域名的值
//             * 如域名 key 值不存在，返回 null*/
//            System.out.println("doc id：" + docID);
//            System.out.println("fileName:" + document.get("fileName"));
//            System.out.println("fileSize:" + document.get("fileSize"));
//            /**防止内容太多影响阅读，只取前20个字*/
//            System.out.println("fileContext:" + document.get("fileContext").substring(0, 20) + "......");
//        }
    }

    private static final String indexSavePath = "D:\\luceneIndex";

    public static void main(String[] args) throws Exception {
        File indexDir = new File(indexSavePath);

        System.out.println(indexSearch(indexDir, "秘书长 校友 总会"));
    }
}

package com.yang.lec;


import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class IndexManagerUtils {

    private static final String indexSavePath = "D:\\luceneIndex";
    private static final String targetFileDirPath = "D:\\news";

    /**
     * 为指定目录下的文件创建索引,包括其下的所有子孙目录下的文件
     *
     * @param targetFileDir ：需要创建索引的文件目录
     * @param indexSaveDir  ：创建好的索引保存目录
     * @throws IOException
     */
    public static void indexCreate(File targetFileDir, File indexSaveDir) throws IOException {
        /** 如果传入的路径不是目录或者目录不存在，则放弃*/
        if (!targetFileDir.isDirectory() || !targetFileDir.exists()) {
            return;
        }

        /** 创建 Lucene 文档列表，用于保存多个 Docuemnt*/
        List<Document> docList = new ArrayList<Document>();

        /**循环目标文件夹，取出文件
         * 然后获取文件的需求内容，添加到 Lucene 文档(Document)中
         * 此例会获取 文件名称、文件内容、文件大小
         * */
        for (File file : targetFileDir.listFiles()) {
            if (file.isDirectory()) {
                /**如果当前是目录，则进行方法回调*/
                indexCreate(file, indexSaveDir);
            } else {
                /**如果当前是文件，则进行创建索引*/
                /** 文件名称：如  abc.txt*/
                String fileName = file.getName();

                /**文件内容：org.apache.commons.io.FileUtils 操作文件更加方便
                 * readFileToString：直接读取整个文本文件内容*/
                String fileContext = FileUtils.readFileToString(file,"utf-8");


                /**文件大小：sizeOf，单位为字节*/
                Long fileSize = FileUtils.sizeOf(file);


                Document luceneDocument = new Document();


                TextField nameFiled = new TextField("fileName", fileName, Store.YES);
                TextField contextFiled = new TextField("fileContext", fileContext, Store.YES);


                TextField sizeFiled = new TextField("fileSize", fileSize.toString(), Store.YES);

                /**将所有的域都存入 Lucene 文档中*/
                luceneDocument.add(nameFiled);
                luceneDocument.add(contextFiled);
                luceneDocument.add(sizeFiled);

                /**将文档存入文档集合中，之后再同统一进行存储*/
                docList.add(luceneDocument);
            }
        }

        /** 创建分词器 */
        //Analyzer analyzer = new StandardAnalyzer();
        //Analyzer analyzer = new IKAnalyzer();
        CJKAnalyzer analyzer = new CJKAnalyzer();
        Directory directory = FSDirectory.open(Paths.get(indexSavePath));


        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        /**创建 索引写对象，用于正式写入索引和文档数据*/
        IndexWriter indexWriter = new IndexWriter(directory, config);

        /**将 Lucene 文档加入到 写索引 对象中*/
        for (int i = 0; i < docList.size(); i++) {
            indexWriter.addDocument(docList.get(i));
        }

        indexWriter.commit();
        indexWriter.close();
        indexWriter.close();
    }

    public static void main(String[] args) throws IOException {
        File file1 = new File(targetFileDirPath);
        File file2 = new File(indexSavePath);
        indexCreate(file1, file2);
    }
}
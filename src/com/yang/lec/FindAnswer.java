package com.yang.lec;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAnswer {

    private static final String indexSavePath = "D:\\luceneIndex";
    private static final String targetFileDirPath = "D:\\news";

    // 存储问题文本
    private final static ArrayList<String> questions = new ArrayList<>();
    // 存储疑问词集合
    private final static List<String> interrogatives = new ArrayList<>();
    // 存储每个问题类型
    private final static List<String> typeOfQuestion = new ArrayList<>();
    // 存储每个问题搜索最佳文档
    private final static List<String> documents = new ArrayList<>();
    // 存储每个问题疑问词
    private final static List<String> interrogativeOfQuestion = new ArrayList<>();

    public static void main(String[] args) {
        // 1. 加载问题 疑问词
        initQuestions();
        initInterrogatives();
        // 2.疑问词提取 问句类型分析
        for(int i = 0;i < questions.size();i++){
            System.out.print("第"+(i+1)+"个问题的疑问词为：");
            // 疑问词提取
            String result = getInterrogatives(questions.get(i));
            interrogativeOfQuestion.add(result);
            getTypeOfQuestion(result);
        }
       // 3.搜索获取问题所在文档
        File indexDir = new File(indexSavePath);
        for(int i = 0;i < questions.size();i++){
            // 对问题进行处理
            String question = questions.get(i);
            question =question.replace(interrogativeOfQuestion.get(i),"");
            question = question.replace("?","");
            question = question.replace("？","");
            try {
                // 记录问题搜索所在文档
                documents.add(IndexSearchUtils.indexSearch(indexDir,question));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 4. 读取问题所在文档
        for (int i =  0;i < questions.size();i++){
            // 读取文档 获取答案 返回答案
            String answer =  getAnswer(documents.get(i),i);
            // 打印答案
            System.out.println(questions.get(i));
            System.out.println("答案："+answer);
            System.out.println(" ");
        }


    }

    // 获取答案
    private static String getAnswer(String path,int position){
        // 打开搜索问题所在文档
        File file = new File(targetFileDirPath+"\\"+path);
        ArrayList<String> arrayList = new ArrayList<>();
        String str;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((str = reader.readLine()) != null) {
                arrayList.add(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 将文档内容转化成字符串
        StringBuilder builder = new StringBuilder();
        for(int i = 1;i < arrayList.size();i++){
            builder.append(arrayList.get(i));
        }
        String news = builder.toString();
        // 分句
        String[] sentences = news.split("[。？！]");
        // 将每个句子与问题计算相似度，记录最大值句子
        int index = -1;
        double max = -1;
        for(int i = 0;i <sentences.length;i++){
            // 计算相似度
            double goal = SimilarityUtil.getSimilarity(questions.get(position),sentences[i]);
            if (goal > max){
                max = goal;
                index = i;
            }
        }
        //System.out.println(sentences[index]);
        // 对最大值句子进行命名实体识别
        Segment segment = HanLP.newSegment().enableNameRecognize(true).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        List<Term> termList = segment.seg(sentences[index]);
        // 获取问题类型
        String type = typeOfQuestion.get(position);
        for (Term term : termList) {
            String nature = term.nature != null ? term.nature.toString() : "空";
            // 根据问题类型获取答案
            switch (type) {
                case "nr":
                    if (nature.equals("nrf") || nature.equals("nr")) {
                        return term.word;
                    }
                    break;
                case "ns":
                    if (nature.equals("nsf") || nature.equals("ns")) {
                        return term.word;
                    }
                    break;
                case "time":
                    if (nature.equals("t")) {
                        String[] s = sentences[index].split("，");
                        for (String value : s) {
                            if (value.contains(term.word)) {
                                return value;
                            }
                        }
                    }
                    break;
                case "m":
                    // 句子中一般含有多个数字，计算每个数字所在短句与问题相似度，返回最大值短句
                    Pattern pattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
                    Matcher matcher = pattern.matcher(sentences[index]);
                    List<String> result = new ArrayList<>();
                    while (matcher.find()) {
                        result.add(matcher.group(0));
                    }
                    if (result.size() == 1) {
                        return result.get(0);
                    } else if (result.size() > 0) {
                        // 按逗号分句
                        String[] s = sentences[index].split("，");
                        double maxPoint = -1;
                        int pos = -1;
                        for (int j = 0; j < s.length; j++) {
                            // 计算相似度
                            double p = SimilarityUtil.getSimilarity(questions.get(position), s[j]);
                            if (p > maxPoint) {
                                maxPoint = p;
                                pos = j;
                            }
                        }
                        if (pos == -1) {
                            return sentences[index];
                        } else {
                            return s[pos];

                        }
                    } else {
                        return sentences[index];
                    }
                case "wh":
                    if (nature.equals("nt") || nature.equals("ns") || nature.equals("nsf")) {
                        return term.word;
                    }
                    break;
            }
        }
        return sentences[index];
    }

    // 获取问题类型
    private static void getTypeOfQuestion(String interrogative){
        if("谁".equals(interrogative)){
            typeOfQuestion.add("nr");
        }else if("哪".equals(interrogative) || "哪里".equals(interrogative) || "哪个地方".equals(interrogative)){
            typeOfQuestion.add("ns");
        }else if ("什么时候".equals(interrogative) || "何时".equals(interrogative) || "哪年".equals(interrogative) || "什么时间".equals(interrogative)){
            typeOfQuestion.add("time");
        }else if("多少".equals(interrogative)){
            typeOfQuestion.add("m");
        }else if("哪个".equals(interrogative)){
            typeOfQuestion.add("wh");
        }
    }


    // 疑问词提取
    private static String getInterrogatives(String str){
        List<String> result = new ArrayList<>();
        //创建分词对象
        Analyzer anal=new IKAnalyzer(true);
        StringReader reader=new StringReader(str);
        //分词
        TokenStream ts=anal.tokenStream("", reader);
        try {
            ts.reset();
            CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
            //遍历分词数据
            while(ts.incrementToken()){
                result.add(term.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
        for (String interrogative : interrogatives) {
            // 返回疑问词
            if (result.contains(interrogative)) {
                System.out.println(interrogative);
                return interrogative;
            }
        }
        return null;
    }

    // 初始化问题
    private static void initQuestions(){
        questions.add("经济贸易学院的巴基斯坦外教是谁?");
        questions.add("阿拉伯语系教师艾河旭作为中国红十字会援外专家团成员去了哪里?");
        questions.add("谁做了题目为《总体国家安全观视角下的疫情防控》的演讲?");
        questions.add("联合国国际贸易中心月系列活动由联合国国际贸易中心与我校哪个机构联合举办?");
        questions.add("中国银行广州白云支行向我校捐赠的一次性防护口罩有多少？");
        questions.add("校友总会秘书长是谁?");
        questions.add("本学期我校开设了多少门在线教学课程?");
        questions.add("《论坛报》是哪个国家的主流媒体?");
        questions.add("我校于何时开学并全面启动在线教学工作?");
        questions.add("广州市政府新闻办第55场疫情防控新闻通气会在什么时间举行?");
    }

    // 初始化疑问词
    private static void initInterrogatives(){
        interrogatives.add("谁");
        interrogatives.add("什么时候");
        interrogatives.add("何时");
        interrogatives.add("哪年");
        interrogatives.add("什么时间");
        interrogatives.add("哪");
        interrogatives.add("哪里");
        interrogatives.add("什么地方");
        interrogatives.add("多少");
        interrogatives.add("几");
        interrogatives.add("多大");
        interrogatives.add("多高");
        interrogatives.add("哪个");
        interrogatives.add("哪儿");
    }

}

package com.yang.lec;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.sun.deploy.util.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) throws IOException {
//            String text="广州市政府新闻办第55场疫情防控新闻通气会在举行";
//            //创建分词对象
//            Analyzer anal=new IKAnalyzer(true);
//            StringReader reader=new StringReader(text);
//            //分词
//            TokenStream ts=anal.tokenStream("", reader);
//            ts.reset();
//            CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
//            //遍历分词数据
//            while(ts.incrementToken()){
//                System.out.print(term.toString()+"|");
//            }
//            reader.close();
//            System.out.println();
//        String content = "校友总会秘书长是谁?";
//        List<String> keywordList = HanLP.extractKeyword(content, 5);
//         System.out.println(StringUtils.join(keywordList," "));
//        System.out.println(NLPTokenizer.segment("你好，3月24日欢迎使用HanLP汉语处理包！"));
//        String news = "本网讯 近日，广东省红十字会向我校专门发来感谢信，感谢我校阿拉伯语系教师艾河旭作为中国红十字会援外专家团成员出征伊拉克，日夜奋战在伊拉克抗疫第一线的优异表现。 艾河旭工作照2020年3月7日下午，应伊拉克方面的请求，中国红十字会总会派遣志愿专家团队一行七人从广州飞赴巴格达支援伊拉克新冠肺炎疫情防控工作。艾河旭老师积极响应号召，不畏艰险，担任阿拉伯语翻译，随同专家团奔赴伊拉克。他努力克服当地局势不稳、语言不畅、设施简陋、水土不服等困难，为多场专家会议、外出调研、临床培训和防护指导等活动提供翻译服务，为专家团在当地顺利开展活动发挥了重要作用，为专家组制定伊拉克国家防疫预案、建立新冠肺炎研究中心和PCR实验室、加强定点医院影像技术建设等工作的顺利开展提供了坚实的语言保障。他勇做“最美国际逆行者”的事迹得到了新华社、环球时报、南方都市报等多家媒体的关注报道。 感谢信";
//        String[] sen = news.split("[。！？]");
//        for(int i = 0;i < sen.length;i++){
//            System.out.println(sen[i]);
//        }
//        Segment segment = HanLP.newSegment().enableNameRecognize(true).enablePlaceRecognize(true).enableOrganizationRecognize(true);
//        List<Term> termList = segment.seg("本学期我校开设了多少门在线教学课程");
//        System.out.println(termList);
//        List<Term> terms = NLPTokenizer.segment("本学期我校开设了多少门在线教学课程?");
//        List<String> keywordList = new ArrayList<>();
//        for(Term t: termList) {
//            keywordList.add(t.word);
//        }
//        System.out.println(StringUtils.join(keywordList," "));
        String sentences = "12日，做广告有12万成本，12门课。";
        Pattern pattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
        Matcher matcher = pattern.matcher(sentences);
        List<String> result = new ArrayList<>();
        while (matcher.find()){
            System.out.println(matcher.group(0));
        }
    }
}

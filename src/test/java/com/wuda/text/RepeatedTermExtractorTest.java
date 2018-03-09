package com.wuda.text;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RepeatedTermExtractorTest {

    public static void main(String[] args) {
        RepeatedTermExtractor extractor = new RepeatedTermExtractor();
        extractor.setMinTermLength(2);
        extractor.setMaxTermLength(10);
        extractor.getMinTermLength();
        extractor.getMaxTermLength();
//        String text = "豆豆鞋批发,豆豆鞋采购";
        String text = "LOREAL润发精油,欧莱雅润发精油,欧莱雅润发精油报价,LOREAL润发精油报价";
//        String text = "【欧莱雅润发精油】京东JD.COM提供欧莱雅润发精油正品行货，并包括LOREAL润发精油网购指南，以及欧莱雅润发精油图片、润发精油参数、润发精油评论、润发精油心得、润发精油技巧等信息，网购欧莱雅润发精油上京东,放心又轻松";
//        String text = "【阿道夫护发乳液】京东JD.COM提供阿道夫护发乳液正品行货，并包括ADOLPH护发乳液网购指南，以及阿道夫护发乳液图片、护发乳液参数、护发乳液评论、护发乳液心得、护发乳液技巧等信息，网购阿道夫护发乳液上京东,放心又轻松";
        Map<String, AtomicInteger> map = extractor.getTerms(text);
        System.out.println(map);
    }
}

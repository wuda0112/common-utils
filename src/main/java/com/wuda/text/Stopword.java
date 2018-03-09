package com.wuda.text;

import java.util.HashSet;
import java.util.Set;

/**
 * 停止词.
 *
 * @author wuda
 */
public class Stopword {

    /**
     * 停止词集合.
     */
    private final static Set<String> stopwords = new HashSet<>();

    /*
     * 初始化
     */
    static {
        stopwords.add("an");
        stopwords.add("and");
        stopwords.add("are");
        stopwords.add("as");
        stopwords.add("at");
        stopwords.add("be");
        stopwords.add("but");
        stopwords.add("by");
        stopwords.add("for");
        stopwords.add("if");
        stopwords.add("in");
        stopwords.add("into");
        stopwords.add("is");
        stopwords.add("it");
        stopwords.add("no");
        stopwords.add("not");
        stopwords.add("of");
        stopwords.add("on");
        stopwords.add("or");
        stopwords.add("such");
        stopwords.add("that");
        stopwords.add("the");
        stopwords.add("their");
        stopwords.add("then");
        stopwords.add("there");
        stopwords.add("these");
        stopwords.add("they");
        stopwords.add("this");
        stopwords.add("to");
        stopwords.add("was");
        stopwords.add("will");
        stopwords.add("with");
        stopwords.add("产品");
        stopwords.add("仓供");
        stopwords.add("的");
        stopwords.add("你");
        stopwords.add("我");
        stopwords.add("他");
        stopwords.add("它");
        stopwords.add("不");
        stopwords.add("在");
        stopwords.add("又");
        stopwords.add("查看");
        stopwords.add("-");
        stopwords.add("一块");
        stopwords.add("上等");
        stopwords.add("与");
        stopwords.add("专供");
        stopwords.add("专卖店");
        stopwords.add("专用");
        stopwords.add("专营");
        stopwords.add("专营店");
        stopwords.add("个人");
        stopwords.add("之道");
        stopwords.add("也");
        stopwords.add("了");
        stopwords.add("于");
        stopwords.add("仍");
        stopwords.add("从");
        stopwords.add("仓");
        stopwords.add("以");
        stopwords.add("优级");
        stopwords.add("优良");
        stopwords.add("优质");
        stopwords.add("但");
        stopwords.add("使");
        stopwords.add("公司");
        stopwords.add("出版社");
        stopwords.add("却");
        stopwords.add("及");
        stopwords.add("和");
        stopwords.add("她");
        stopwords.add("好吃的");
        stopwords.add("好的");
        stopwords.add("实业");
        stopwords.add("对的");
        stopwords.add("就");
        stopwords.add("并");
        stopwords.add("店铺");
        stopwords.add("当");
        stopwords.add("很");
        stopwords.add("或");
        stopwords.add("旗舰");
        stopwords.add("旗舰店");
        stopwords.add("无用");
        stopwords.add("是");
        stopwords.add("是的");
        stopwords.add("最");
        stopwords.add("有限");
        stopwords.add("有限公司");
        stopwords.add("有限责任公司");
        stopwords.add("比");
        stopwords.add("热销");
        stopwords.add("特供");
        stopwords.add("用品");
        stopwords.add("着");
        stopwords.add("系列");
        stopwords.add("给");
        stopwords.add("而");
        stopwords.add("股份有限公司");
        stopwords.add("被");
        stopwords.add("让");
        stopwords.add("责任公司");
        stopwords.add("贸易");
        stopwords.add("还");
        stopwords.add("送");
        stopwords.add("通用");
        stopwords.add("陆桥店");
        stopwords.add("院仓");
    }

}

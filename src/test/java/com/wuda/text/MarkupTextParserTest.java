package com.wuda.text;

import java.util.TreeMap;

public class MarkupTextParserTest {

    public static void main(String[] args) {
//        String document = "今天/a 下午/b [广州/c 车主/d]/e    停车场/f 内/g 堵/h 3/i    小时/j [非常 痛苦/k]n";
        String document = "今晚/t  的/u  长安街/ns  流光溢彩/l  ，/w  火树银花/i  ；/w  [人民/n  大会堂/n]ns  里/f  灯火辉煌/i  ，/w  充满/v  欢乐/a  祥和/a  的/u  喜庆/v  气氛/n  。/w  在/p  这/r  场/q  由/p  [中共/j  北京/ns  市委/n  宣传部/n]nt  、/w  市政府/n  办公厅/n  等/u  单位/n  主办/v  的/u  题/n  为/v  “/w  世纪/n  携手/v  、/w  共/d  奏/v  华章/n  ”/w  的/u  新年/t  音乐会/n  上/f  ，/w  中国/ns  三/m  个/q  著名/a  交响乐团/n  ———/w  [中国/ns  交响乐团/n]nt  、/w  [上海/ns  交响乐团/n]nt  、/w  [北京/ns  交响乐团/n]nt  首/m  次/q  联袂/d  演出/v  。/w  著名/a  指挥家/n  陈/nr  佐湟/nr  、/w  陈/nr  燮阳/nr  、/w  谭/nr  利华/nr  分别/d  指挥/v  演奏/v  了/u  一/m  批/q  中外/j  名曲/n  ，/w  京/j  沪/j  两地/n  ２００/m  多/m  位/q  音乐家/n  组成/v  的/u  大型/b  乐队/n  以/p  饱满/a  的/u  激情/n  和/c  精湛/a  的/u  技艺/n  为/p  观众/n  奉献/v  了/u  一/m  台/q  高/a  水准/n  的/u  交响音乐会/n  。/w  ";
        MarkupTextParser parser = new MarkupTextParser();
        TreeMap<String, String> map = parser.parse(document);
        System.out.println(map);
    }
}

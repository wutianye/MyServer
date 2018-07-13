package com.example.demo.Utils;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: yunxi
 * Date: 2018/7/13
 * Time: 16:17
 * Description: String类的一些方法
 */
public class StringUtil {

    /* *
     *
     * 功能描述:
     *
     * @param: [str]
     * @return: boolean
     * @auther: liuyunxing
     * @Description 过滤空字符串//TODO
     * @date: 2018/7/13 16:18
     */
    private static  String[]  validRoles = {"common", "admin"} ; // 用户的合法身份
    public static boolean isEmpty(String str) {
        return str == null ||str.trim().equals("");
    }

    public static boolean isValidRole(String role){
        return Arrays.asList(validRoles).contains(role.trim()); // 检测是否为正确合法身份
    }

    /*
        public static void main(String[] args) throws IOException {
        System.out.println(StringUtil.isEmpty("    "));
    }*/
}

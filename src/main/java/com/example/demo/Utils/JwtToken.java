package com.example.demo.Utils;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.Service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class JwtToken {

    //生成token
    public static String createToken() throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                .withHeader(map)//header
                .sign(Algorithm.HMAC256("secret"));//加密
        return token;
    }


}

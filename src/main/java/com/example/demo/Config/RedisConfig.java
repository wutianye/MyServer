package com.example.demo.Config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created with IntelliJ IDEA.
 * User: yunxi
 * Date: 2018/7/12
 * Time: 9:33
 * Description: No Description
 */

@Configuration
@EnableCaching // 启用缓存

public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    RedisConnectionFactory factory;

    /**
     * 实例化  RedisTemplate对象
     * @return
     */
    /*
     * 1.此处为什么要序列化
     * 原因：普通的连接使用没有办法把Java对象直接存入Redis，
     * 而需要我们自己提供方案-对象序列化，然后存入redis，
     * 取回序列化内容后，转换为java对象
     * */

    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        initDomainRedisTemplate(template,factory);
        return template;
    }

    /**
     * 设置数据存入redis的序列化方式
     * @param redisTemplate
     * @param factory
     */
    private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory){

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的 value 值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper(); //ObjectMapper是jackson的主要类，作用序列化
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY); //设置任何字段可见
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); //反序列化
        serializer.setObjectMapper(mapper);

        //定义key生成策略
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
    }

    //实例化HashOperations对象，可以使用Hash类型操作
    @Bean
    public HashOperations<String,String,Object> hashOperations(RedisTemplate<String,Object> redisTemplate){
        return redisTemplate.opsForHash(); //opsForHash()该方法用于操作Hash
    }

    //实例化ListOperations对象，可以使用List类型操作
    @Bean
    public ListOperations<String,Object> listOperations(RedisTemplate<String,Object> redisTemplate){
        return redisTemplate.opsForList(); //opsForList()该方法用于操作List
    }
}


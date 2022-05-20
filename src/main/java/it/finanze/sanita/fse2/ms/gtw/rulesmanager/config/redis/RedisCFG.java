package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;

/**
 *  @author vincenzoingenito
 *  Configuration class redis.
 */
@Configuration
public class RedisCFG {

    /** 
     *  Host.
     */
	@Value("${redis.hostname}")
	private String host;

	/** 
     *  Port.
     */
	@Value("${redis.port}")
	private Integer port;

	/** 
     *  Password.
     */
	@Value("${redis.password}")
	private transient char[] password;

	/** 
     *  Pool max.
     */
	@Value("${redis.jedis.pool.max-total}")
	private Integer poolMaxTotal;

	/** 
     *  Idle min.
     */
	@Value("${redis.jedis.pool.min-idle}")
	private Integer idleMin;

	/** 
     *  Idle max.
     */
	@Value("${redis.jedis.pool.max-idle}")
	private Integer idleMax;
	     
	/** 
     *  Jedis client configuration.
     */
	@Bean(name = "jedisClientConfiguration")  
	public JedisClientConfiguration getJedisClientConfiguration() {
		JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisPoolingClientConfigurationBuilder = (
				JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
		
		GenericObjectPoolConfig<Object> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
		genericObjectPoolConfig.setMaxTotal(poolMaxTotal);
		genericObjectPoolConfig.setMinIdle(idleMin);
		genericObjectPoolConfig.setMaxIdle(idleMax); 

		return jedisPoolingClientConfigurationBuilder.poolConfig(genericObjectPoolConfig).build();
	}
 
	/** 
     *  Jedis client configuration access list.
     */
	@Primary
	@Bean(name = "jedisConnectionFactory")
	public JedisConnectionFactory getJedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration (); 
	    redisStandaloneConfiguration.setHostName(host);
	    redisStandaloneConfiguration.setPort(port);
	    
	    if(password!=null && password.length>0) {
		    redisStandaloneConfiguration.setPassword(RedisPassword.of(String.valueOf(password)));
	    }
	    return new JedisConnectionFactory(redisStandaloneConfiguration, getJedisClientConfiguration());
	}

	/** 
     * Rest template access list.
     */
	@Bean(name = "stringRedisTemplate")
	public StringRedisTemplate getStringRedisTemplate() { 
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(getJedisConnectionFactory());
		return redisTemplate;
	}
	 
	@Bean
	public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
	    return new RedisLockProvider(connectionFactory, "lock");
	}
}

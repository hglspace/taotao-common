package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService {
  
	@Autowired(required=false)
	private ShardedJedisPool shardedJedisPool;
	
	/**
	 * T
	 * @param fun
	 * @return
	 * description:第一个T是为了限制execute方法中T的作用域
	 */
	private <T> T execute(Function<T,ShardedJedis> fun){
		ShardedJedis shardedJedis = null;
        try {
            // 从连接池中获取到jedis分片对象
            shardedJedis = shardedJedisPool.getResource();
            return fun.callback(shardedJedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != shardedJedis) {
                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
                shardedJedis.close();
            }
        }
        return null;
	}
	
	/**
	 * String
	 * @param key
	 * @param value
	 * @return
	 * description:执行set操作
	 */
	public String set(final String key,final String value){
		return this.execute(new Function<String,ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.set(key, value);
			}
		});
	}
	
	/**
	 * String
	 * @param key
	 * @return
	 * description:执行get操作
	 */
	public String get(final String key){
		return this.execute(new Function<String,ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.get(key);
			}
		});
	}
	
	/**
	 * Long
	 * @param key
	 * @return
	 * description:执行del操作
	 */
	public Long del(final String key){
		return this.execute(new Function<Long,ShardedJedis>(){
			@Override
			public Long callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.del(key);
			}
		});
	}
	
	
	/**
	 * Long
	 * @param key
	 * @param seconds
	 * @return
	 * description:设置生存时间，单位为秒
	 */
	public Long expire(final String key,final Integer seconds){
		return this.execute(new Function<Long,ShardedJedis>(){
			@Override
			public Long callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				return e.expire(key, seconds);
			}
		});
	}
	
	
	/**
	 * String
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 * description:执行set操作，并设置时间
	 */
	public String set(final String key,final String value,final Integer seconds){
		return this.execute(new Function<String,ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				// TODO Auto-generated method stub
				String str =  e.set(key, value);
				e.expire(key, seconds);
				return str;
			}
		});
	}
}

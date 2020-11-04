/*
* File name: JedisClusterPipeLine.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			钟滨远		2020年9月2日
* ...			...			...
*
***************************************************/

package com.run.redis.config;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.google.common.collect.Lists;

import redis.clients.jedis.Client;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterInfoCache;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.PipelineBase;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisMovedDataException;
import redis.clients.jedis.exceptions.JedisRedirectionException;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年9月2日
*/
public class JedisClusterPipeLine extends PipelineBase implements Closeable{
	
	 
	//一次pipeline过程中使用到jedis缓存
    private final Map<JedisPool,Jedis> poolToJedisMap = new HashMap<>();
    // JedisClusterInfoCache中封装了每个节点对应的Jedis操作客户端
    // 还有槽点对应的Jedis操作客户端
    private final JedisClusterInfoCache clusterInfoCache;
    private final JedisSlotBasedConnectionHandler connectionHandler;
    // 根据顺序存储每个命令对应的Client
    private Queue<Client> clients = new LinkedList<Client>();
    private boolean hasDataInBuff = false;   // 是否有数据在缓存区
    
    /**
     * 根据jedisCluster实例生成对应的JedisClusterPipeline
     */
    public JedisClusterPipeLine(Set<HostAndPort> node,JedisPoolConfig poolConfig) {
    	JedisCluster jedisCluster=new JedisCluster(node, poolConfig);
        //根据jedisCluster实例获取其父类BinaryJedisCluster的属性值
        connectionHandler = ReflectUtil.getObject(jedisCluster,"connectionHandler");
        //根据刚刚从jedisCluster实例获取的connectionHandler实例
        // 获取其父类JedisClusterConnectionHandler的属性值
        clusterInfoCache = ReflectUtil.getObject(connectionHandler,"cache");
    }
    
    /**
     * 由于集群模式存在节点的动态添加删除，且client不能实时感知（只有在执行命令时才可能知道集群发生变更），
     * 因此，该实现不保证一定成功，建议在批量操作之前调用 refreshCluster() 方法重新获取集群信息。
     * 也可以定时刷新以便获取最新的集群信息
     * 还有可以在捕获异常中刷新集群节点信息，重试操作，设置重试次数，不能无线重试
     */
    public void refreshClusterInfo(){
        connectionHandler.renewSlotCache();
    }


    
	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close()  {
		 clean();
	        //清除客户端队列
	        clients.clear();
	        //关闭缓存的jedis连接
	        for (Jedis jedis : poolToJedisMap.values()) {
	            if (hasDataInBuff) {
	                flushCachedData(jedis);
	            }
	            jedis.close();
	        }
	        //清除jedis连接缓存数据
	        poolToJedisMap.clear();
	        //还原初始状态
	        hasDataInBuff = false;

	}

	/**
	* @Description:
	* @param jedis
	*/
	
	private void flushCachedData(Jedis jedis) {
		try {
            jedis.getClient().getAll();
        } catch (RuntimeException ex) {
            //捕获异常，保证代码的高可用
        }
	}

	/**
	 * @see redis.clients.jedis.PipelineBase#getClient(java.lang.String)
	 */
	@Override
	protected Client getClient(String key) {
		byte[] bKey = SafeEncoder.encode(key);
		 
        return getClient(bKey);
	}

	/**
	 * @see redis.clients.jedis.PipelineBase#getClient(byte[])
	 */
	@Override
	protected Client getClient(byte[] bkey) {
		//1.根据key值计算槽点
        Jedis jedis = getJedis(JedisClusterCRC16.getSlot(bkey));
 
        Client client = jedis.getClient();
        clients.add(client);
 
        return client;

	}

	/**
	* @Description:
	* @param slot
	* @return
	*/
	
	private Jedis getJedis(int slot) {
		 JedisPool slotJedisPool = clusterInfoCache.getSlotPool(slot);
		 
	        //3.验证分配到同一个节点的所有槽点对应的JedisPool是否已经存在
	        Jedis jedis = poolToJedisMap.get(slotJedisPool);
	        //4. 如果不存在
	        if(jedis == null){
	            //4.1 则获取客户端连接，存储到容器中去
	            jedis = slotJedisPool.getResource();
	            poolToJedisMap.put(slotJedisPool,jedis);
	        }
	        //4.2 如果已经存储直接返回
	        hasDataInBuff = true;
	        return jedis;

	}
	/**
     * 同步读取所有数据. 与syncAndReturnAll()相比，sync()只是没有对数据做反序列化
     */
    public void sync() {
        innerSync(null);
    }
 
    /**
     * 同步读取所有数据 并按命令顺序返回一个列表
     *
     * @return 按照命令的顺序返回所有的数据
     */
    public List<Object> syncAndReturnAll() {
        List<Object> responseList = new ArrayList<Object>();
 
        innerSync(responseList);
 
        return responseList;
    }
 
    private void innerSync(List<Object> responseList){
        if(responseList == null){
            responseList = new ArrayList<>();
        }
 
        HashSet<Client> clientSet = new HashSet<Client>();
 
        try {
            for(Client client : clients){
                // 在sync()调用时其实是不需要解析结果数据的，
                // 但是如果不调用get方法，发生了JedisMovedDataException这样的错误应用是不知道的，
                // 因此需要调用get()来触发错误。
                // 其实如果Response的data属性可以直接获取，可以省掉解析数据的时间，
                // 然而它并没有提供对应方法，要获取data属性就得用反射，不想再反射了，所以就这样了
                Object response = generateResponse(client.getOne()).get();
                if(response != null){
                    responseList.add(response);
                }
                // size相同说明所有的client都已经添加，就不用再调用add方法了
                if (clientSet.size() != poolToJedisMap.size()) {
                    clientSet.add(client);
                }
            }
        } catch (JedisRedirectionException ex) {
            if(ex instanceof JedisMovedDataException){
                // if MOVED redirection occurred, rebuilds cluster's slot cache,
                // recommended by Redis cluster specification
                refreshClusterInfo();
            }
        } finally {
            if (clientSet.size() != poolToJedisMap.size()) {
                // 所有还没有执行过的client要保证执行(flush)，防止放回连接池后后面的命令被污染
                for (Jedis jedis : poolToJedisMap.values()) {
                    if (clientSet.contains(jedis.getClient())) {
                        continue;
                    }
 
                    flushCachedData(jedis);
                }
            }
 
            hasDataInBuff = false;
            close();
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String,Object>> clusterPiplineGet(List<String> keys,RedisTemplate redisTemplate) {
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        RedisSerializer<Map<String,Object>> valueSerializer =redisTemplate.getValueSerializer();
        HashMap<RedisClusterNode, List<String>> nodeKeyMap = new HashMap<>();
        List<Map<String,Object>> result = Lists.newArrayList();
        RedisClusterConnection redisClusterConnection =redisTemplate.getConnectionFactory().getClusterConnection();
        try {
            //通过计算每个key的槽点，并获取相应节点
            Iterable<RedisClusterNode> redisClusterNodes = redisClusterConnection.clusterGetNodes();
            for (RedisClusterNode redisClusterNode : redisClusterNodes) {
                RedisClusterNode.SlotRange slotRange = redisClusterNode.getSlotRange();
                for (String key : keys) {
                    int slot = JedisClusterCRC16.getSlot(key);
                    if (slotRange.contains(slot)) {
                        List<String> list = nodeKeyMap.get(redisClusterNode);
                        if (null == list) {
                            list = new ArrayList<>();
                            nodeKeyMap.putIfAbsent(redisClusterNode, list);
                        }
                        list.add(key);
                    }
                }
            }
            for (Map.Entry<RedisClusterNode, List<String>> clusterNodeListEntry : nodeKeyMap.entrySet()) {
                RedisClusterNode redisClusterNode = clusterNodeListEntry.getKey();
                //连接节点
                JedisPool jedisPool = ((JedisCluster) redisClusterConnection.getNativeConnection()).getClusterNodes().get(new HostAndPort(redisClusterNode.getHost(), redisClusterNode.getPort()).toString());
                List<String> nodeListEntryValue = clusterNodeListEntry.getValue();
 
                byte[][] arr = new byte[nodeListEntryValue.size()][];
                int count = 0;
                //获取key数据
                for (String nodeKey : nodeListEntryValue) {
                    arr[count++] = keySerializer.serialize(nodeKey);
                }
                Jedis jedis = jedisPool.getResource();
                List<Response<byte[]>> responses = new ArrayList<>();
                try {
                    Pipeline pipeline = jedis.pipelined();
                    for (String nodeKey : nodeListEntryValue) {
                        responses.add(pipeline.get(keySerializer.serialize(nodeKey)));
                    }
                    pipeline.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    jedis.close();
                }
                for (Response<byte[]> response : responses) {
                    byte[] data = response.get();
                    result.add(valueSerializer.deserialize(data));
                }
            }
        } finally {
            RedisConnectionUtils.releaseConnection(redisClusterConnection, redisTemplate.getConnectionFactory());
        }
        return result;
    }

}

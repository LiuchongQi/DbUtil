
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

/**
 * 自定义连接池, 管理连接
 * 代码实现：
	1.  MyPool.java  连接池类，   
	2.  指定全局参数：  初始化数目、最大连接数、当前连接、   连接池集合
	3.  构造函数：循环创建3个连接
	4.  写一个创建连接的方法
	5.  获取连接
	------>  判断： 池中有连接， 直接拿
	 ------>                池中没有连接，
	------>                 判断，是否达到最大连接数； 达到，抛出异常；没有达到最大连接数，
			创建新的连接
	6. 释放连接
	 ------->  连接放回集合中(..)
 *
 */
public class MyPool {
	private int current_count=0;
	private int max_count=6;
	private int init_count=3;
	private LinkedList<Connection> pool=new LinkedList<>();
	/**
	 * 构造连接池
	 */
	public MyPool(){
		for(int i=0;i<init_count;i++){
			pool.add(createConnection());
		}
	}
	/**
	 * 创建连接并代理
	 * @return
	 */
	private Connection createConnection() {	
		current_count++;
		Connection con=DbUtil.getConnection();
		//代理
		Connection proxy=(Connection) Proxy.newProxyInstance(
				con.getClass().getClassLoader()		//类加载器
				,new Class[]{Connection.class}		//代理类实现的接口列表
				, new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Object result=null;
						String method_name=method.getName();
						if("close".equals(method_name)){
							pool.add(con);
						}else{
							result=method.invoke(con, args);
						}
						return result;
					}
				});
		
		
		return proxy;
	}
	/**
	 * 获取连接
	 * @return
	 */
	public Connection getConnection(){
		if(pool.size()>0){
			
			return pool.removeFirst();
		}
		else if(pool.size()==0&&current_count<max_count){
			return createConnection();
		}
		else{
			throw new RuntimeException("当前已达到最大连接数！！！");
		}
		
	}
	/**
	 * 释放连接
	 * @param con
	 */
	public void pushConnection(Connection con){
		if(pool.size()<init_count){
			pool.add(con);
		}
		else{
			DbUtil.closeDb(con);
			current_count--;
		}
	}
}

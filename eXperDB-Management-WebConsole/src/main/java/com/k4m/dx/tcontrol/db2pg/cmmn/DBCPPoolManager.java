package com.k4m.dx.tcontrol.db2pg.cmmn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;

import org.apache.tomcat.dbcp.pool.ObjectPool;
import org.json.simple.JSONObject;

import com.k4m.dx.tcontrol.cmmn.client.ClientProtocolID;


public class DBCPPoolManager {
	public DBCPPoolManager(){}

	public static  Map<String, Object> setupDriver(JSONObject serverObj, String poolName, int maxActive) throws Exception {
		System.out.println( "/************************************************************/");
		System.out.println( "DBCPPool을 생성합니다. ["+poolName+"]");
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Connection conn = null;

		// JDBC 클래스 로딩
				try {
				String driver = "";
				 String connectURI = "";
				 Properties props = new Properties();
				String DB_TYPE = serverObj.get("DB_TYPE").toString();
					
				switch (DB_TYPE) {
				//오라클
					case "TC002201" :
						System.out.println("DB_TYPE.ORACLE");
						driver = "oracle.jdbc.driver.OracleDriver";
						connectURI = "jdbc:oracle:thin:@"+serverObj.get("SERVER_IP")+":"+serverObj.get("SERVER_PORT")+"/"+serverObj.get("DATABASE_NAME");
						break;
				//PostgreSQL		
					case "TC002204" :
						System.out.println("DB_TYPE.PostgreSQL");
						driver = "org.postgresql.Driver" ;
						connectURI = "jdbc:postgresql://"+serverObj.get("SERVER_IP")+":"+serverObj.get("SERVER_PORT")+"/"+serverObj.get("DATABASE_NAME");
						break;
				//MS-SQL
					case "TC002202" :
						System.out.println("DB_TYPE.MS-SQL");
						driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver" ;
						connectURI = "jdbc:sqlserver://"+serverObj.get("SERVER_IP")+":"+serverObj.get("SERVER_PORT")+";databaseName="+serverObj.get("DATABASE_NAME");
						break;
				//SyBaseASE	
					case "TC002206" :
						System.out.println("DB_TYPE.Sybase ASE");
						/*driver = "com.sybase.jdbc4.jdbc.SybDriver" ;
						connectURI = "jdbc:sybase:Tds:"+configInfo.SERVERIP+":"+configInfo.PORT+"/"+configInfo.DBNAME;

						props.put("DATABASE", configInfo.DBNAME);
						if (configInfo.LOAD_MODE != null && configInfo.LOAD_MODE.equals(Constant.DIRECT_PATH_LOAD)){
							props.put("ENABLE_BULK_LOAD", "ARRAYINSERT_WITH_MIXED_STATEMENTS");
							Log.info(0, DBCPPoolManager.class, "PROPERTY : ENABLE_BULK_LOAD=ARRAYINSERT_WITH_MIXED_STATEMENTS");
						}*/
						break;
				//DB2		
					case "TC002205" :
						System.out.println("DB_TYPE.DB2");
						driver = "com.ibm.db2.jcc.DB2Driver" ;
						connectURI = "jdbc:db2://"+serverObj.get("SERVER_IP")+":"+serverObj.get("SERVER_PORT")+"/"+serverObj.get("DATABASE_NAME");
						System.setProperty("db2.jcc.charsetDecoderEncoder", "3");
						break;
				//Tibero		
					case "TC002208" :
						System.out.println("DB_TYPE.Tibero");
						System.out.println("DB_TYPE =" + DB_TYPE);
						/*driver = "com.tmax.tibero.jdbc.TbDriver";
						connectURI = "jdbc:tibero:thin:@"+configInfo.SERVERIP+":"+configInfo.PORT+":"+configInfo.DBNAME;*/
						break;										
	    			}
				
				Class.forName(driver);				
				
				//DB 연결대기 시간
				DriverManager.setLoginTimeout(5);
				
		        // ID and Password
		        props.put("user", serverObj.get("USER_ID"));
		        props.put("password", serverObj.get("USER_PWD"));

				conn = DriverManager.getConnection(connectURI, props);
						
	            conn.setAutoCommit(false);
	            System.out.println("DB_VERSION  = "+conn.getMetaData().getDatabaseMajorVersion());
	            System.out.println("ORG_SCHEMA_NM  = "+conn.getMetaData().getUserName());
	      		
	            result.put("RESULT_CODE", 0);
	            
				} catch (Exception e) {
					//shutdownDriver(poolName);
					System.out.println( e.toString() );
					result.put("RESULT_CODE", 1);
					result.put("ERR_MSG", e.toString() );
					return result;	
	
				}finally{
					if (conn != null){
						conn.close();
					}
				}
				
				System.out.println( "DBCPPool 생성 완료 하였습니다.["+poolName+"]");
				System.out.println( "/************************************************************/");
				
				return result;	
	}
	
	
	
	
	/*
	 * 풀명에 해당한는 풀 및 DB정보 close
	 */
    private static void shutdownDriver(String poolName) throws Exception {
		PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		driver.closePool(poolName);
	}
    
    
	/*
	 * connection get
	 */
    public static Connection getConnection(String poolName) throws Exception {
    	Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:" + poolName);
    	/*
    	switch (getConfigInfo(poolName).DB_TYPE){
    		case Constant.DB_TYPE.ASE :
    			conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
    	}
    	*/
    	conn.setAutoCommit(false);
    	
    	return conn;
    }

	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
    	JSONObject serverObj = new JSONObject();
    	
		try {
			
			//DB2
			/*serverObj.put(ClientProtocolID.SERVER_NAME, "192.168.56.200");
			serverObj.put(ClientProtocolID.SERVER_IP, "192.168.56.200");
			serverObj.put(ClientProtocolID.SERVER_PORT, "48789");
			serverObj.put(ClientProtocolID.DATABASE_NAME, "db2");
			serverObj.put(ClientProtocolID.USER_ID, "db2");
			serverObj.put(ClientProtocolID.USER_PWD, "db20225!!");
			serverObj.put(ClientProtocolID.DB_TYPE, "DB2");*/		
			
			//MS-SQL
			/*serverObj.put(ClientProtocolID.SERVER_NAME, "10.1.21.28");
			serverObj.put(ClientProtocolID.SERVER_IP, "10.1.21.28");
			serverObj.put(ClientProtocolID.SERVER_PORT, "1444");
			serverObj.put(ClientProtocolID.DATABASE_NAME, "mizuho");
			serverObj.put(ClientProtocolID.USER_ID, "mizuho");
			serverObj.put(ClientProtocolID.USER_PWD, "mizuho");
			serverObj.put(ClientProtocolID.DB_TYPE, "MSS");*/
			
			//Oracle
			serverObj.put(ClientProtocolID.SERVER_NAME, "192.168.56.118");
			serverObj.put(ClientProtocolID.SERVER_IP, "192.168.56.118");
			serverObj.put(ClientProtocolID.SERVER_PORT, "1521");
			serverObj.put(ClientProtocolID.DATABASE_NAME, "ora12c");
			serverObj.put(ClientProtocolID.USER_ID, "migrator");
			serverObj.put(ClientProtocolID.USER_PWD, "migrator");
			serverObj.put(ClientProtocolID.DB_TYPE, "ORA");
			
			//PostgreSQL
			/*serverObj.put(ClientProtocolID.SERVER_NAME, "192.168.56.112");
			serverObj.put(ClientProtocolID.SERVER_IP, "192.168.56.112");
			serverObj.put(ClientProtocolID.SERVER_PORT, "5432");
			serverObj.put(ClientProtocolID.DATABASE_NAME, "kimjy");
			serverObj.put(ClientProtocolID.USER_ID, "kimjy");
			serverObj.put(ClientProtocolID.USER_PWD, "kimjy");
			serverObj.put(ClientProtocolID.DB_TYPE, "POG");*/
			
			Map<String, Object> result = DBCPPoolManager.setupDriver(serverObj, "TEST", 2);
			
			System.out.println(result.get("RESULT_CODE"));
			System.out.println(result.get("ERR_MSG"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

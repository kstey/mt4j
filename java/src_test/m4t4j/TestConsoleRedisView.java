package m4t4j;

import com.cnx.jedis.CNXJedis;
import com.cnx.jedis.DefaultJedisConfig;
import com.cnx.jedis.JedisCreator;
import com.cnx.locale.AppLocalization;

public class TestConsoleRedisView 
{
	static AppLocalization localize = AppLocalization.getInstance(true);
	
	public static void main(String[] argv)
	{
		DefaultJedisConfig.configJedisCreator();
		JedisCreator.configure("127.0.0.1", 15934);
		CNXJedis jedis = new CNXJedis(true);
		
		while(true)
		{
			byte[] message = jedis.poll("MT4Log".getBytes(), 5);
			if( message !=null )
			{
				System.out.println(new String(message));
			}
		}
	}
}

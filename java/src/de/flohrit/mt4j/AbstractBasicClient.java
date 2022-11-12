package de.flohrit.mt4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.cnx.jedis.CNXJedis;
import com.cnx.jedis.DefaultJedisConfig;
import com.cnx.jedis.JedisCreator;

public abstract class AbstractBasicClient implements MT4BasicClient 
{
	static final boolean use_jedis;
	static
	{
		String jedis_console_str = System.getProperty("JEDIS_CONSOLE");
		use_jedis = jedis_console_str==null || jedis_console_str.equals("1");
	}
	
	static 
	{
		
		if( use_jedis )
		{
			DefaultJedisConfig.configJedisCreator();
			JedisCreator.configure("127.0.0.1", 15934);
			
			try 
			{

				final CNXJedis log_jedis = new CNXJedis();
				
				PrintStream out = new PrintStream(new OutputStream() 
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					@Override
					public void write(int b) throws IOException 
					{
						switch( b)
						{
						case '\n':
							byte[] content = baos.toByteArray();
							log_jedis.offer("MT4Log".getBytes(), content);
							baos.reset();
							break;
							
						case '\r':
							break;
							
						default:
							baos.write(b);
						}
					}
				});
				
				System.setOut(out);
				System.setErr(out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			try 
			{
				File log = new File("T:/SampleEA/experts/logs/SampleEA.log");
				log.getParentFile().mkdirs();
				
				PrintStream out = new PrintStream(log);
				System.setOut(out);
				
				File error_log = new File("T:/SampleEA/experts/logs/SampleEA_error.log");
				error_log.getParentFile().mkdirs();
				System.setErr(new PrintStream(error_log));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<Double> high = new ArrayList<Double>();
	private List<Double> low = new ArrayList<Double>();
	private List<Double> open = new ArrayList<Double>();
	private List<Double> close = new ArrayList<Double>();
	private double bid;
	private double ask;
	private int maxBars = 100;
	
	@Override
	public int processTick(double bid, double ask) {
		return PROCESS_TICK_NONE;
	}

	@Override
	public void addNewBar(double high, double low, double open, double close) 
	{
		trimArray(this.high);
		this.high.add(0, high);
		
		trimArray(this.low);
		this.low.add(0, low);

		trimArray(this.open);
		this.open.add(0, open);
		
		trimArray(this.close);
		this.close.add(0, close);
	}
	
	/**
	 * @return last bars high value
	 */
	public double getHigh() {
		return high.get(0);
	}

	/**
	 * @param idx bar number (0 == last bar) 
	 * @return high value of bar
	 */
	public double getHigh(int idx) {
		return high.get(idx);
	}

	/**
	 * @return last bars low value
	 */
	public double getLow() {
		return low.get(0);
	}

	/**
	 * @param idx bar number (0 == last bar) 
	 * @return low value of bar
	 */
	public double getLow(int idx) {
		return low.get(idx);
	}

	/**
	 * @return last bars high value
	 */
	public double getOpen() {
		return open.get(0);
	}

	/**
	 * @param idx bar number (0 == last bar) 
	 * @return open value of bar
	 */
	public double getOpen(int idx) {
		return open.get(idx);
	}

	/**
	 * @return last bars high value
	 */
	public double getClose() {
		return close.get(0);
	}

	/**
	 * @param idx bar number (0 == last bar) 
	 * @return close value of bar
	 */
	public double getClose(int idx) {
		return close.get(idx);
	}

	/**
	 * @return last bid price
	 */
	public double getBid() {
		return bid;
	}

	/**
	 * @return last ask price
	 */
	public double getAsk() {
		return ask;
	}
	
	public void setMaxBars(int maxBars) {
		this.maxBars = maxBars;
	}
	
	private void trimArray(List<Double> values) {
		for (;values.size() >= maxBars;) {
			values.remove(values.size()-1);
		}
	}
}

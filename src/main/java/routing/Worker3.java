package routing;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import constant.Constant;


public class Worker3 {

	  private final static String TASK_QUEUE_NAME = "hello";

	  public static void main(String[] argv)
	      throws java.io.IOException,
	             java.lang.InterruptedException, TimeoutException {

	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    final Channel channel = connection.createChannel();

//	    int prefetchCount = 1;
//	    channel.basicQos(prefetchCount);

	    
	    final int workerId = new Random().nextInt(10000);
	    
	    channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	  //Declare exchange
	    channel.exchangeDeclare(Constant.EXCHANGE1, "fanout");
	    channel.queueBind(TASK_QUEUE_NAME, Constant.EXCHANGE1, Constant.ROUTING1);
	    

	    	
	    	final Consumer consumer = new DefaultConsumer(channel) {
	    		  @Override
	    		  public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
	    		    String message = new String(body, "UTF-8");

	    		    System.out.println(workerId + " [x] Received '" + message + "', routingKey:"+ envelope.getRoutingKey());
	    		    try {
	    		      doWork(message);
	    		    } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
	    		      System.out.println(" [x] Done");
	    		      channel.basicAck(envelope.getDeliveryTag(), false);
	    		    }
	    		  }
	    		};
	    		boolean autoAck = false; // acknowledgment is covered below
	    		channel.basicConsume(TASK_QUEUE_NAME, autoAck, consumer);
	    
    }
	  
	  private static void doWork(String task) throws InterruptedException {
		    for (char ch: task.toCharArray()) {
		        if (ch == '.') Thread.sleep(1000);
		    }
		}    
	
}
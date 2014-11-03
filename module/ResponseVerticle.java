package com.swordspa;

import java.net.UnknownHostException;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;


public class ResponseVerticle extends Verticle{
	
	@Override
	public void start(){
		
		EventBus eb = vertx.eventBus();
		
		
		eb.registerHandler("dingdong", new Handler<Message<String>>() {
			public void handle(Message<String> message){
				Logger log = container.logger();
				log.info("recieved!");
				message.reply("BEEPBEEP");
			}
		});
		
	}
	
//	private void addMongo(){
//		MongoClient mongo;
//		try {
//			
//			mongo = new MongoClient ("localhost", 27017);
//			DB db = mongo.getDB("database name");
//			boolean auth = db.authenticate("username", "password".toCharArray());
//			
//			
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		
//	}

}

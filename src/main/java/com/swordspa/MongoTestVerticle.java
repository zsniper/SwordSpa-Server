package com.swordspa;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MongoTestVerticle extends Verticle {
	
	public final static String MONGODB_BUSADDR = "mongo-persistor";
	public final static String MONGODB_DATABASE = "vertx";
	public final static String PERSISTENCE_MONGODB_HOST = "127.0.0.1";
	public final static Integer PERSISTENCE_MONGODB_PORT = Integer.valueOf(27017);
	
	public void start () {
		// load the general config object, loaded by using -config on command line
        
        JsonObject mongo_config = new JsonObject();
        mongo_config.putString("address", MONGODB_BUSADDR);
        mongo_config.putString("host", PERSISTENCE_MONGODB_HOST);
        mongo_config.putNumber("port", PERSISTENCE_MONGODB_PORT);
        mongo_config.putNumber("pool_size", 10);
        mongo_config.putString("db_name", MONGODB_DATABASE);
 
        // deploy the mongo-persistor module, which we'll use for persistence
        container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", mongo_config);
 
        // create and run the server
        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest httpServerRequest) {

//            	httpServerRequest.response().end("Hello world! 2");
                // we send the response from the mongo query back to the client.
                // first create the query
                JsonObject matcher = new JsonObject().putString("state", "AL");
                JsonObject json = new JsonObject().putString("collection", "zips")
                        .putString("action", "find")
                        .putObject("matcher", matcher);
                
                // send it over the bus
                vertx.eventBus().send(MONGODB_BUSADDR, json, new Handler<Message<JsonObject>>() {
 
                    @Override
                    public void handle(Message<JsonObject> message) {
                        // send the response back, encoded as string
                        httpServerRequest.response().end(message.body().encodePrettily());
                    }
                });
            }
        }).listen(8888, "localhost");
 
        // output that the server is started
        container.logger().info("Webserver started, listening on port: 8888");
	}
}

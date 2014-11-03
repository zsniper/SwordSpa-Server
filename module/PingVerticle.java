package com.swordspa;

/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
 This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class PingVerticle extends Verticle {

	public void start(final Future<Void> startedResult) {
		
		container.deployVerticle("ResponseVerticle.java", new AsyncResultHandler<String>() {
			public void handle(AsyncResult<String> deployResult){
				if (deployResult.succeeded()){
					startedResult.setResult(null);
				}else{
					startedResult.setFailure(deployResult.cause());
				}
			}
		});

		HttpServer server = vertx.createHttpServer();

		server.requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
				request.response().end("Hello world!");
			}
		}).listen(8080, "localhost");

		EventBus eb = vertx.eventBus();
		
		eb.send("dingdong", "MESSAGE! MESSAGE!", new Handler<Message<String>>() {
			public void handle(Message<String> message){
				Logger log = container.logger();
				log.info("I GOT IT! " + message.body());
			}
		});
	}
}

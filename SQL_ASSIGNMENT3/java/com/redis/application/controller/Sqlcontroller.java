package com.redis.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.application.serviceimp.SqlService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

@RestController
public class Sqlcontroller extends AbstractVerticle {
	
		@Autowired
		SqlService sqlservice;
		
		
		
		
		
		public void start(Future<Void> startFuture)
		{
			Router router = Router.router(vertx);
			
			//router.route().handler(BodyHandler.create());
			
			router.post("/execute").handler(executeQuery(null));
			
			vertx.createHttpServer()
			.requestHandler(router)
			.listen(1045);
		}
	
	
	  
		 @PostMapping("/execute")
		   public Handler<RoutingContext> executeQuery(@RequestBody String requestBody) {
			    try {
			        ObjectMapper objectMapper = new ObjectMapper();
			        JsonNode jsonNode = objectMapper.readTree(requestBody);
			        
			        if (jsonNode.has("query")) {
			            String query = jsonNode.get("query").asText();
			            processQuery(query);
			        } else {
			            System.out.println("Invalid request format. Please provide a 'query' property.");
			        }
			    } catch (Exception e) {
			        System.out.println("Error processing the request: " + e.getMessage());
			    }
				return null;
			}
		private void processQuery(String query) {
		    if (query.toUpperCase().startsWith("CREATE TABLE")) {
		        sqlservice.createTable(query);
		    } else if (query.toUpperCase().startsWith("INSERT INTO")) {
		        sqlservice.insertIntoTable(query);
		    } else {
		        System.out.println("Unsupported query. Please enter valid SQL-like queries.");
		    }
		}


	

}

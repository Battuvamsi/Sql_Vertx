package com.redis.application.serviceimp;

public interface SqlService {
	
	public void executeQuery(String query);
	
	
    public void createTable(String query);
   

    public void insertIntoTable(String query);
 

}

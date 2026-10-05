package com.vaultx.model;


// first console prototype
public class Credential {

    private int id;
    private String serviceName;
    private String username;
    private String password;

    public Credential(int id, String serviceName, String username, String password){
        this.id = id;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
        
    }

    public int getId(){
        return id;
    }

    public String getServiceName(){
        return serviceName;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(String updatedUsername){
        this.username = updatedUsername;
    }

    public void setPassword(String updatedPassword){
        this.password = updatedPassword;
    }
}


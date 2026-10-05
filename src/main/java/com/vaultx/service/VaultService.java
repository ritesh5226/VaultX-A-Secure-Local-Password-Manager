package com.vaultx.service;

import com.vaultx.model.Credential;
import java.util.ArrayList;
import java.util.Scanner;

public class VaultService {
    private ArrayList<Credential> credentialList;

    public VaultService() {
        credentialList = new ArrayList<>();
    }

    public void addCredential(Scanner sc) {
        // entering service name
        System.out.print("enter Service name : ");
        String service = sc.next();

        // entering username
        System.out.print("enter user name : ");
        String name = sc.next();

        System.out.print("enter password : ");
        String pass = sc.next();

        int size = credentialList.size() + 1;

        credentialList.add(new Credential(size, service, name, pass));
    }

    public void viewCredentials() {
        System.out.println("=======CREDENTIALS=======");
        for (Credential element : credentialList) {
            System.out.println(element.getId());
            System.out.println(element.getUsername());
            System.out.println(element.getServiceName());
        }
    }

    public void searchCredential(Scanner sc) {

        // ask user to search credential
        System.out.println("enter service name to search : ");
        String find = sc.next();

        Credential foundCredential = findCredential(find);
        if (foundCredential != null) {
            System.out.println(foundCredential.getServiceName());
            System.out.println(foundCredential.getUsername());
        } else {
            System.out.println("Credential not found !");
        }
    }

    public void updateCredential(Scanner sc){
        System.out.println("enter service name to search : ");
        String find = sc.next();

        Credential foundCredential = findCredential(find);

        if (foundCredential != null) {
            System.out.print("enter updated Username : ");
            String updatedUsername = sc.next();
            System.out.println("enter updated Password : ");
            String updatedPassword = sc.next();
            foundCredential.setPassword(updatedPassword);
            foundCredential.setUsername(updatedUsername);
            System.out.println("Credentials Updated ! ");
        } else {
            System.out.println("Credential not found !");
        }
    }

    public void deleteCredential(Scanner sc) {
        System.out.println("enter service name to search : ");
        String find = sc.next();

        Credential foundCredential = findCredential(find);
  

        if (foundCredential != null) {
            credentialList.remove(foundCredential);
            System.out.println("Credential Removed ");
        } else {
            System.out.println("credential not found ! ");
        }
    }

    private Credential findCredential(String serviceName) {
        Credential foundCredential = null;

        for (Credential element : credentialList) {
            if (element.getServiceName().equals(serviceName)) {
                foundCredential = element;
                break;
            }
        }

        return foundCredential;
    }
}

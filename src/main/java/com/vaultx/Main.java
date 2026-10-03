/* This is not a final project, this is subjected to be changed as more things will be addded, this is merely a prototype verrsion */

package com.vaultx;
import com.vaultx.model.Credential;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // scanner to input credential from user
        Scanner sc = new Scanner(System.in);

        // using arrayList so we can dynamicay add objetcs
        ArrayList<Credential> credentialList = new ArrayList<>();


        // will delete these pre stack credential these are just for testing purpose
        credentialList.add(new Credential(1, "git", "shishir", "Testpassword123!"));
        credentialList.add(new Credential(2, "gmail", "shishir", "Testpassword123!"));
        credentialList.add(new Credential(3, "amazon", "shishir", "Testpassword123!"));

        //entering service name 
        System.out.print("enter Service name : ");
        String service = sc.next();

        // entering username 
        System.out.print("enter user name : ");
        String name = sc.next();

        System.out.print("enter password : ");
        String pass = sc.next();

        int size =  credentialList.size();
        size++;

        credentialList.add(new Credential(size, service, name, pass));

        //print credential
        for (Credential element : credentialList) {
            System.out.println("=======CREDENTIALS=======");            
            System.out.println(element.getId());
            System.out.println(element.getUsername());
            System.out.println(element.getServiceName());
            System.out.println(element.getUsername());
            }
        }

}


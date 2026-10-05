/* This is not a final project, this is subjected to be changed as more things will be addded, this is merely a prototype verrsion */

package com.vaultx;

import java.util.Scanner;
import com.vaultx.service.VaultService;

public class Main {
    public static void main(String[] args) {

        // scanner to input credential from user
        Scanner sc = new Scanner(System.in);

        // creatred vaultx service to manage the credential
        VaultService vault = new VaultService();

        int choice;
        do{

        // printing the menu 
        System.out.println("========== VaultX ==========");
        System.out.println("1. Add Credential");
        System.out.println("2. View Credentials");
        System.out.println("3. Search Credential");
        System.out.println("4. Update Credential");
        System.out.println("5. Delete Credential");
        System.out.println("6. Exit");
        System.out.println("=============================");
        choice = sc.nextInt();  

        switch(choice){
            case 1 : 
                // calling function to add credential
                vault.addCredential(sc);
                break;

            case 2 :
                // calling function to view credential
                vault.viewCredentials();
                break;

            case 3 : 
                //search Credential
                vault.searchCredential(sc);
                break;

            case 4 :
                //update Credential
                vault.updateCredential(sc);
                break;

            case 5 : 
                //delete credential
                vault.deleteCredential(sc);
                break;

            case 6:
                System.out.println("Exiting VaultX....");
                break;
            
            default :
                System.out.println("enter a valid operation !!");

        }
    }while(choice != 6);

    }
}



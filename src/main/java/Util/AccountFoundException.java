package Util;

import Model.Account;

public class AccountFoundException extends Exception{

    public  AccountFoundException  (Account account){

      
         super("This account: " + account.username + "already exists. /n Please sign in!"
       );

      //  custom exception. Prints message with username if account already exists.
     
      
    }
    
}

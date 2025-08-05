package Util;
import Model.Account;

public class AccountNotFoundException extends Exception {

   public AccountNotFoundException(Account account){

    super("This account" + account.getAccount_id() + "does not exist. Please navigate to /aforementioned link/ and register.");
   }
        
    
    

}

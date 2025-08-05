package DAO;


import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import Model.Account;
import Util.AccountFoundException;
import Util.AccountNotFoundException;

import java.sql.Connection;
import Util.ConnectionUtil;

import Util.AccountFoundException;

public class AccountDao {

    public void registerAccount(Account account) throws AccountFoundException{
   try (Connection connection = ConnectionUtil.getConnection()) {
    // Print if connection is valid!
    System.out.println("Connection: " + connection.isValid(0));

    // Check to see if account exists. If so throw exception!
    String sql = "select * from account where username = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, account.getUsername());
    ResultSet rs = statement.executeQuery();
    
    if (rs.next()) {
        throw new AccountFoundException(account);
        
    }
    // if Account does not exist, It will insert the user into the database
     String insertSql  = "insert into account ( username, password) values (?,?)";
     statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
     statement.setString(1, account.getUsername());
     statement.setString(2, account.getPassword());
     statement.executeUpdate();

     ResultSet generatedKeys = statement.getGeneratedKeys();
     if (generatedKeys.next()){
         int newId = generatedKeys.getInt(1);
        account.setAccount_id(newId);
     }


   } catch (SQLException e) {
    System.out.println(e.getMessage() + e.getErrorCode());
    e.printStackTrace();
    // TODO: handle exception
   }
   
}


public Account Login(Account account) throws AccountNotFoundException{
    try {
        Connection connection = ConnectionUtil.getConnection();
        System.out.println("Connection: " + connection.isValid(0));
        String sql = "select * from account where username = ? and password = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, account.getUsername());
        statement.setString(2, account.getPassword());
        ResultSet result = statement.executeQuery();

        if (result.next()){
           Account login = new Account();
          login.setAccount_id(result.getInt("account_id"));
          login.setUsername(result.getString("username"));
          login.setPassword(result.getString("password"));
           return login;
        }
       else {
        throw new AccountNotFoundException(account);
       }
       

    } catch (SQLException e) {
        // TODO: handle exception
        System.out.println(e.getErrorCode() + e.getMessage());
        e.printStackTrace();
    }
    return null;
}


    public Account getAccountByID(int account_id){
        String sql = "select * from account where account_id = ?";
        Account account = null;
    try { Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
       statement.setInt(1, account_id);
       ResultSet rs = statement.executeQuery();

       if(rs.next()){
         account = new Account();
        account.setAccount_id(rs.getInt("account_id"));
        account.setPassword(rs.getString("password"));
        account.setUsername(rs.getString("username"));
       }

        
    } catch (SQLException e) {
        System.out.println(e.getErrorCode() + e.getMessage());
        e.printStackTrace();
        // TODO: handle exception
    }    
    return account;
   

}


public Account getAccountByUsername(String username){
    String sql = "select * from account where username = ?";
    Account account = null;
    try {
        Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet rs =  statement.executeQuery();

        if (rs.next()) {
            account = new Account();
            account.setAccount_id(rs.getInt("account_id"));
            account.setPassword(rs.getString("password"));
            account.setUsername(rs.getString("username"));
            
        }
        
    } catch (SQLException e) {
        System.out.println(e.getErrorCode() + e.getMessage());
        e.printStackTrace();
        
        // TODO: handle exception
    }
    return account;
}
}
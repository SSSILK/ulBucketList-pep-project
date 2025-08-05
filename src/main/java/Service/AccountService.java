package Service;

import javax.security.auth.login.AccountNotFoundException;

import DAO.AccountDao;
import Model.Account;
import Util.AccountFoundException;

public class AccountService {

    private AccountDao accountDao;

    public AccountService() {
        this.accountDao = new AccountDao();
    }

    public Account register(Account account) {
    if (account == null 
        || account.getUsername() == null || account.getUsername().isBlank()
        || account.getPassword() == null || account.getPassword().isBlank()
        || account.getPassword().length() < 4)  {
        return null;
    }

   
    Account existing = accountDao.getAccountByUsername(account.getUsername());
    if (existing != null) {
        System.out.println("Username: " + account.getUsername() + " is already taken!");
        return null;
    }

    try {
        accountDao.registerAccount(account);
        return account;
    } catch (AccountFoundException e) {
        System.out.println("Exception while registering account: " + e.getMessage());
        return null;
    }
}


    public Account logAccountService(String username, String password) throws AccountNotFoundException, Util.AccountNotFoundException {
    Account tempAccount = new Account();
    tempAccount.setUsername(username);
    tempAccount.setPassword(password);
    return accountDao.Login(tempAccount);
}



    public Account getAccountById(int id) {
        return accountDao.getAccountByID(id);
    }
}

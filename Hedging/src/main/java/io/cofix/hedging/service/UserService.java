package io.cofix.hedging.service;

public interface UserService {
    String  findPasswordByUserName(String userName);
    void setPassword(String password);
    String  getUserName();
    String getPasswd();
}

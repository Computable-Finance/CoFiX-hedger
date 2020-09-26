package io.cofix.hedging.service.serviceImpl;

import io.cofix.hedging.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Value("${cofix.user.name}")
    private String userName;

    @Value("${cofix.user.password}")
    private String passwd;


    @Override
    public String findPasswordByUserName(String userName) {
        return passwd;
    }

    @Override
    public void setPassword(String password) {
        this.passwd = password;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public String getPasswd() {
        return this.passwd;
    }
}

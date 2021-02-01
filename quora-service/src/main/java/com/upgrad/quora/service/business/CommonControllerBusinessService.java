package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CommonControllerBusinessService {


    @Autowired
    private CommonDao commonDao;

    @Transactional
    public UserEntity getUser(String accessToken, String userId)  throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthToken = commonDao.getUserAuthToken(accessToken);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if(userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
            else {
                UserEntity userEntity = commonDao.getUserByUserId(userId);
                if (userEntity == null){
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                }
                else {
                    return userEntity;
                }
            }
        }
    }

}

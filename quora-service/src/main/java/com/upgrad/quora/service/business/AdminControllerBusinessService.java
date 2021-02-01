package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AdminControllerBusinessService {

    @Autowired
    private AdminDao adminDao;

    @Transactional
    public String deleteUser(String accessToken, String userId)  throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthToken = adminDao.getUserAuthToken(accessToken);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else {
            if (userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
            else {
                UserEntity loggedUser = userAuthToken.getUser();
                if (loggedUser.getRole() == "nonadmin"){
                    throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
                }
                else{
                    UserEntity userEntity = adminDao.getUserByUserId(userId);
                    if (userEntity == null) {
                        throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
                    }
                    else {
                        String uuid = userEntity.getUuid();
                        adminDao.deleteUser(userEntity);
                        return uuid;
                    }
                }
            }
        }
    }

}

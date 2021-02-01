package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionControllerBusinessService {


    @Autowired
    private QuestionDao questionDao;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String accessToken)  throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthToken = questionDao.getUserAuthToken(accessToken);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if(userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            }
            else {
                questionEntity.setUser(userAuthToken.getUser());
                final ZonedDateTime now = ZonedDateTime.now();
                questionEntity.setDate(now);
                questionDao.createQuestion(questionEntity);
                return questionEntity;
            }
        }
    }

    public QuestionEntity editQuestionContent(String content,String questionId, String accessToken)  throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthToken = questionDao.getUserAuthToken(accessToken);

        QuestionEntity questionEntity = questionDao.getQuestionEntityById(questionId);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if (userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            } else {
                if (questionEntity == null) {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                } else {
                    if (questionEntity.getUser() != userAuthToken.getUser()) {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    } else {
                        questionEntity.setContent(content);
                        final ZonedDateTime now = ZonedDateTime.now();
                        questionEntity.setDate(now);
                        questionDao.editQuestion(questionEntity);
                        return questionEntity;
                    }

                }
            }
        }
    }


    public List<QuestionEntity> getAllQuestions (String accessToken)  throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthToken = questionDao.getUserAuthToken(accessToken);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if(userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            }
            else {

                List<QuestionEntity> allQuestions = questionDao.getAllQuestions();
                return allQuestions;
            }
        }
    }


    public String deleteQuestion(String questionId, String accessToken)  throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthToken = questionDao.getUserAuthToken(accessToken);

        QuestionEntity questionEntity = questionDao.getQuestionEntityById(questionId);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if (userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            } else {
                if (questionEntity == null) {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                } else {
                    if ((questionEntity.getUser() == userAuthToken.getUser()) && (questionEntity.getUser().getRole() != "nonadmin")) {

                        String uuid = questionEntity.getUuid();
                        questionDao.deleteQuestion(questionEntity);
                        return uuid;

                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
                    }

                }
            }
        }
    }

    public List<QuestionEntity> getAllQuestionsByUser (String userId, String accessToken)  throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthToken = questionDao.getUserAuthToken(accessToken);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if(userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
            }
            else {
                UserEntity userEntity = questionDao.getUserByUserId(userId);
                if (userEntity == null){
                    throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
                }
                else {
                    List<QuestionEntity> allQuestions = questionDao.getAllQuestionsById(userId);
                    return allQuestions;
                }
            }
        }
    }


}

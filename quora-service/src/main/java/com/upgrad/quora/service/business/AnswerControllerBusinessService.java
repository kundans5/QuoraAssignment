package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AnswerControllerBusinessService {

    @Autowired
    private AnswerDao answerDao;


    @Transactional
    public AnswerEntity createAnswer (AnswerEntity answerEntity, String questionId , String accessToken)  throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = answerDao.getQuestionEntityById(questionId);
        UserAuthTokenEntity userAuthTokenEntity = answerDao.getUserAuthToken(accessToken);


        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        } else {
            if( userAuthTokenEntity == null) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            }
            else {
                if(userAuthTokenEntity.getLogoutAt() != null)
                {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
                }
                else {
                    answerEntity.setQuestion(questionEntity);
                    answerEntity.setUser(userAuthTokenEntity.getUser());
                    final ZonedDateTime now = ZonedDateTime.now();
                    answerEntity.setDate(now);
                    answerDao.createAnswer(answerEntity);
                    return answerEntity;
                }
            }
        }
    }


    public AnswerEntity editAnswerContent(String content,String answerId, String accessToken)  throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthToken = answerDao.getUserAuthToken(accessToken);

       AnswerEntity answerEntity = answerDao.getAnswerEntityById(answerId);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if (userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
            } else {
                if (answerEntity == null) {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                } else {
                    if (answerEntity.getUser() != userAuthToken.getUser()) {
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
                    } else {
                        answerEntity.setAnswer(content);
                        final ZonedDateTime now = ZonedDateTime.now();
                        answerEntity.setDate(now);
                        answerDao.editAnswer(answerEntity);
                        return answerEntity;
                    }

                }
            }
        }
    }


    public String deleteAnswer(String answerId, String accessToken)  throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthToken = answerDao.getUserAuthToken(accessToken);

        AnswerEntity answerEntity = answerDao.getAnswerEntityById(answerId);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else {
            if (userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
            } else {
                if (answerEntity == null) {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                } else {
                    if ((answerEntity.getUser() == userAuthToken.getUser()) && (answerEntity.getUser().getRole() != "nonadmin")) {

                        String uuid = answerEntity.getUuid();
                        answerDao.deleteAnswer(answerEntity);
                        return uuid;

                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
                    }

                }
            }
        }
    }

    public List<AnswerEntity> getAllAnswersByQuestion (String questionId, String accessToken)  throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthToken = answerDao.getUserAuthToken(accessToken);

        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if(userAuthToken.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
            }
            else {
                QuestionEntity questionEntity = answerDao.getQuestionEntityById(questionId);
                if (questionEntity == null){
                    throw new InvalidQuestionException("USR-001", "The question with entered uuid whose details are to be seen does not exist");
                }
                else {
                    List<AnswerEntity> allAnswers = answerDao.getAllAnswersById(questionId);
                    return allAnswers;
                }
            }
        }
    }

}

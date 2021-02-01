package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthTokenEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByUserName", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public QuestionEntity editQuestion(final QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
        return questionEntity;
    }

    public QuestionEntity getQuestionEntityById(final String questionId) {
        return entityManager.createNamedQuery("getQuestionByQuestionId", QuestionEntity.class).setParameter("uuid",questionId).getSingleResult();
    }

    public List<QuestionEntity> getAllQuestions() {
        return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
    }

    public List<QuestionEntity> getAllQuestionsById(String uuid) {
        return entityManager.createNamedQuery("getAllQuestionsByUserId", QuestionEntity.class).setParameter("uuid",uuid).getResultList();
    }

    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.detach(questionEntity);
    }

    public UserEntity getUserByUserId(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUserId", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}

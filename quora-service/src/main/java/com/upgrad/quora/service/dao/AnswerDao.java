package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity getQuestionEntityById(final String questionId) {
        return entityManager.createNamedQuery("getQuestionByQuestionId", QuestionEntity.class).setParameter("uuid",questionId).getSingleResult();
    }

    public AnswerEntity getAnswerEntityById(final String answerId) {
        return entityManager.createNamedQuery("getAnswerByAnswerId", AnswerEntity.class).setParameter("uuid",answerId).getSingleResult();
    }

    public UserAuthTokenEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByUserName", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity editAnswer(final AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public void deleteAnswer(final AnswerEntity answerEntity) {
        entityManager.detach(answerEntity);
    }
    public List<AnswerEntity> getAllAnswersById(String questionId) {
        return entityManager.createNamedQuery("getAllAnswersByUserId", AnswerEntity.class).setParameter("uuid",questionId).getResultList();
    }

}

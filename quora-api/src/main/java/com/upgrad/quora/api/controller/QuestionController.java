package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionControllerBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionControllerBusinessService questionControllerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion (@RequestHeader("authorization") final String authorization, QuestionRequest questionRequest) throws AuthorizationFailedException {

        String[] bearerToken = authorization.split("Bearer ");

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());

        final QuestionEntity createdQuestion = questionControllerBusinessService.createQuestion(questionEntity,bearerToken[1]);

        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions (@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        String[] bearerToken = authorization.split("Bearer ");

        List<QuestionEntity> allQuestions = questionControllerBusinessService.getAllQuestions(bearerToken[1]);

        ArrayList<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<QuestionDetailsResponse>();

        while (allQuestions.iterator().hasNext()) {

            QuestionDetailsResponse temp = new QuestionDetailsResponse();
            temp.id(allQuestions.iterator().next().getUuid());
            temp.content(allQuestions.iterator().next().getContent());
            questionDetailsResponse.add(temp);
        }
        return new ResponseEntity<QuestionDetailsResponse>(questionDetailsResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent (@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization, QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {

        String[] bearerToken = authorization.split("Bearer ");

        QuestionEntity questionEntity = questionControllerBusinessService.editQuestionContent(questionEditRequest.getContent(),questionId,bearerToken[1]);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion (@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        String[] bearerToken = authorization.split("Bearer ");

        String uuid = questionControllerBusinessService.deleteQuestion(questionId,bearerToken[1]);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(uuid).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestionsByUser (@PathVariable("questionId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {


        String[] bearerToken = authorization.split("Bearer ");

        List<QuestionEntity> allQuestions =  questionControllerBusinessService.getAllQuestionsByUser(userId, bearerToken[1]);

        ArrayList<QuestionDetailsResponse> allQuestionsResponse = new ArrayList<QuestionDetailsResponse>();

        while (allQuestions.iterator().hasNext()) {

            QuestionDetailsResponse temp = new QuestionDetailsResponse();
            temp.id(allQuestions.iterator().next().getUuid());
            temp.content(allQuestions.iterator().next().getContent());
            allQuestionsResponse.add(temp);
        }
        return new ResponseEntity<QuestionDetailsResponse>(allQuestionsResponse, HttpStatus.OK);
    }
}

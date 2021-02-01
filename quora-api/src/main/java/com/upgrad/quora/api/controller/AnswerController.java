package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerControllerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerControllerBusinessService answerControllerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer (@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization, AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        String[] bearerToken = authorization.split("Bearer ");

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUuid(UUID.randomUUID().toString());

        final AnswerEntity createdAnswerEntity = answerControllerBusinessService.createAnswer(answerEntity,questionId,bearerToken[1]);

        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent (@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization, AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        String[] bearerToken = authorization.split("Bearer ");

        AnswerEntity answerEntity = answerControllerBusinessService.editAnswerContent(answerEditRequest.getContent(),answerId,bearerToken[1]);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer (@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        String[] bearerToken = authorization.split("Bearer ");

        String uuid = answerControllerBusinessService.deleteAnswer(answerId,bearerToken[1]);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(uuid).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>( answerDeleteResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswersToQuestion  (@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        String[] bearerToken = authorization.split("Bearer ");

        List<AnswerEntity> allAnswers =  answerControllerBusinessService.getAllAnswersByQuestion(questionId, bearerToken[1]);

        ArrayList<AnswerDetailsResponse> allAnswersResponse = new ArrayList<AnswerDetailsResponse>();

        while (allAnswers.iterator().hasNext()) {

            AnswerDetailsResponse temp = new AnswerDetailsResponse();
            temp.id(allAnswers.iterator().next().getUuid());
            temp.questionContent(allAnswers.iterator().next().getQuestion().getContent());
            temp.answerContent(allAnswers.iterator().next().getAnswer());
            allAnswersResponse.add(temp);
        }

        return new ResponseEntity<AnswerDetailsResponse>(allAnswersResponse, HttpStatus.OK);
    }

}

package com.kariqu.om.service.impl;

import com.kariqu.om.domain.*;
import com.kariqu.om.repository.QuestionnaireRepository;
import com.kariqu.om.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-23 11:34
 * @since 1.0.0
 */
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Override
    public List<Survey> queryAllSurvey() {
        return questionnaireRepository.queryAllSurvey();
    }

    @Override
    public Survey querySurveyById(int surveyId) {
        return questionnaireRepository.querySurveyById(surveyId);
    }

    @Override
    @Transactional
    public void createSurvey(Survey survey) {
        questionnaireRepository.createSurvey(survey);
    }

    @Override
    @Transactional
    public int updateSurvey(Survey survey) {
        return questionnaireRepository.updateSurvey(survey);
    }

    @Override
    @Transactional
    public void deleteSurvey(int surveyId) {
        questionnaireRepository.deleteSurvey(surveyId);
    }


    @Override
    public List<Question> queryAllQuestion() {
        return questionnaireRepository.queryAllQuestion();
    }

    @Override
    public Question queryQuestionById(int questionId) {
        return questionnaireRepository.queryQuestionById(questionId);
    }

    @Override
    public List<Question> queryQuestionBySurveyId(int surveyId) {
        return questionnaireRepository.queryQuestionBySurveyId(surveyId);
    }

    @Override
    @Transactional
    public void createQuestion(Question question) {
        questionnaireRepository.createQuestion(question);
    }

    @Override
    @Transactional
    public int updateQuestion(Question question) {
        return questionnaireRepository.updateQuestion(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(int questionId) {
        questionnaireRepository.deleteQuestion(questionId);
    }


    @Override
    public List<Answer> queryAllAnswer() {
        return questionnaireRepository.queryAllAnswer();
    }

    @Override
    public Answer queryAnswerById(int answerId) {
        return questionnaireRepository.queryAnswerById(answerId);
    }

    @Override
    public List<Answer> queryAnswerByQuestionId(int questionId) {
        return questionnaireRepository.queryAnswerByQuestionId(questionId);
    }

    @Override
    @Transactional
    public void createAnswer(Answer answer) {
        questionnaireRepository.createAnswer(answer);
    }

    @Override
    @Transactional
    public int updateAnswer(Answer answer) {
        return questionnaireRepository.updateAnswer(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(int answerId) {
        questionnaireRepository.deleteAnswer(answerId);
    }

    @Override
    @Transactional
    public void submitResult(Result result) {
        questionnaireRepository.createResult(result);
        for (ResultDetail resultDetail : result.getResultDetailList()) {
            resultDetail.setResultId(result.getId());
            questionnaireRepository.createResultDetail(resultDetail);
        }
    }

    @Override
    public Survey querySurvey(int surveyId) {
        Survey survey = querySurveyById(surveyId);
        if (survey != null) {
            List<Question> questionList = queryQuestionBySurveyId(surveyId);
            for (Question question : questionList) {
                question.setAnswerList(queryAnswerByQuestionId(question.getId()));
            }
            survey.setQuestionList(questionList);
        }
        return survey;
    }

    @Override
    public Survey statisticsSurvey(int surveyId) {
        Survey survey = querySurveyById(surveyId);
        if (survey != null) {
            List<Question> questionList = queryQuestionBySurveyId(surveyId);
            for (Question question : questionList) {
                List<Answer> answerList = queryAnswerByQuestionId(question.getId());
                for (Answer answer : answerList) {
                    // 统计答案数量
                    answer.setNumber(questionnaireRepository.queryAnswerCountByAnswerId(answer.getId()));
                }
                question.setNumber(questionnaireRepository.queryQuestionCountByQuestionId(question.getId()));
                question.setAnswerList(answerList);
            }
            survey.setNumber(questionnaireRepository.querySurveyCountBySurveyId(surveyId));
            survey.setQuestionList(questionList);
        }
        return survey;
    }

}

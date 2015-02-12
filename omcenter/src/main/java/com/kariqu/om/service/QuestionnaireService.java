package com.kariqu.om.service;

import com.kariqu.om.domain.Answer;
import com.kariqu.om.domain.Question;
import com.kariqu.om.domain.Result;
import com.kariqu.om.domain.Survey;

import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-23 11:34
 * @since 1.0.0
 */
public interface QuestionnaireService {

    /**
     * 查询所有的问卷
     *
     * @return
     */
    List<Survey> queryAllSurvey();

    /**
     * 查询单个问卷
     *
     * @param surveyId
     * @return
     */
    Survey querySurveyById(int surveyId);

    /**
     * 创建问卷
     * 
     * @param survey
     */
    void createSurvey(Survey survey);

    /**
     * 修改问卷
     * 
     * @param survey
     * @return
     */
    int updateSurvey(Survey survey);

    /**
     * 删除问卷
     * 
     * @param surveyId
     */
    void deleteSurvey(int surveyId);
    

    /**
     * 查询单个问卷下的所有问题
     * 
     * @return
     */
    List<Question> queryAllQuestion();

    /**
     * 查询单个问题
     * 
     * @param questionId
     * @return
     */
    Question queryQuestionById(int questionId);

    /**
     * 查询单个问卷的所有问题
     * 
     * @param surveyId
     * @return
     */
    List<Question> queryQuestionBySurveyId(int surveyId);

    /**
     * 创建问题
     * 
     * @param question
     */
    void createQuestion(Question question);

    /**
     * 修改问题
     * 
     * @param question
     * @return
     */
    int updateQuestion(Question question);

    /**
     * 删除问题
     * 
     * @param questionId
     */
    void deleteQuestion(int questionId);

    /**
     * 查询所有答案
     * 
     * @return
     */
    List<Answer> queryAllAnswer();

    /**
     * 查询单个答案
     * 
     * @param answerId
     * @return
     */
    Answer queryAnswerById(int answerId);

    /**
     * 查询单个问题的所有答案
     * 
     * @param questionId
     * @return
     */
    List<Answer> queryAnswerByQuestionId(int questionId);

    /**
     * 创建答案
     * @param answer
     */
    void createAnswer(Answer answer);

    /**
     * 修改答案
     * 
     * @param answer
     * @return
     */
    int updateAnswer(Answer answer);

    /**
     * 删除答案
     * 
     * @param answerId
     */
    void deleteAnswer(int answerId);

    /**
     * 用户提交问卷(问卷中包含所有的答案)
     *
     * @param result
     */
    void submitResult(Result result);

    /**
     * 查询问卷(包含问卷中的所有问题和答案)
     *
     * @param surveyId
     * @return
     */
    Survey querySurvey(int surveyId);

    /**
     * 统计问卷(问卷中包含有问题信息, 问题中包含有答案信息, 答案中统计有提交数)
     *
     * @param surveyId
     * @return
     */
    Survey statisticsSurvey(int surveyId);

}

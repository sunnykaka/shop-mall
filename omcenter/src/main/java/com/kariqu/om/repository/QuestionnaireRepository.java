package com.kariqu.om.repository;

import com.kariqu.om.domain.*;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-23 10:54
 * @since 1.0.0
 */
public class QuestionnaireRepository extends SqlMapClientDaoSupport  {

    
    public List<Survey> queryAllSurvey() {
        return getSqlMapClientTemplate().queryForList("selectSurvey");
    }

    
    public Survey querySurveyById(int surveyId) {
        Map map = new HashMap();
        map.put("surveyId", surveyId);
        return (Survey) getSqlMapClientTemplate().queryForObject("selectSurvey", map);
    }

    
    public void createSurvey(Survey survey) {
        getSqlMapClientTemplate().insert("insertSurvey", survey);
    }

    
    public int updateSurvey(Survey survey) {
        return getSqlMapClientTemplate().update("updateSurveySelective", survey);
    }

    
    public void deleteSurvey(int surveyId) {
        getSqlMapClientTemplate().delete("deleteSurvey", surveyId);
    }


    
    public List<Question> queryAllQuestion() {
        return getSqlMapClientTemplate().queryForList("selectQuestion");
    }

    
    public Question queryQuestionById(int questionId) {
        Map map = new HashMap();
        map.put("questionId", questionId);
        return (Question) getSqlMapClientTemplate().queryForObject("selectQuestion", map);
    }

    
    public List<Question> queryQuestionBySurveyId(int surveyId) {
        Map map = new HashMap();
        map.put("surveyId", surveyId);
        return getSqlMapClientTemplate().queryForList("selectQuestionBySurveyId", map);
    }

    
    public void createQuestion(Question question) {
        getSqlMapClientTemplate().insert("insertQuestion", question);
    }

    
    public int updateQuestion(Question question) {
        return getSqlMapClientTemplate().update("updateQuestionSelective", question);
    }

    
    public void deleteQuestion(int questionId) {
        getSqlMapClientTemplate().delete("deleteQuestion", questionId);
    }


    
    public List<Answer> queryAllAnswer() {
        return getSqlMapClientTemplate().queryForList("selectAnswer");
    }

    
    public Answer queryAnswerById(int answerId) {
        Map map = new HashMap();
        map.put("answerId", answerId);
        return (Answer) getSqlMapClientTemplate().queryForObject("selectAnswer", map);
    }

    
    public List<Answer> queryAnswerByQuestionId(int questionId) {
        Map map = new HashMap();
        map.put("questionId", questionId);
        return getSqlMapClientTemplate().queryForList("selectAnswerByQuestionId", map);
    }

    
    public void createAnswer(Answer answer) {
        getSqlMapClientTemplate().insert("insertAnswer", answer);
    }

    
    public int updateAnswer(Answer answer) {
        return getSqlMapClientTemplate().update("updateAnswerSelective", answer);
    }

    
    public void deleteAnswer(int answerId) {
        getSqlMapClientTemplate().delete("deleteAnswer", answerId);
    }

    
    public void createResult(Result result) {
        getSqlMapClientTemplate().insert("insertResult", result);
    }

    
    public void createResultDetail(ResultDetail resultDetail) {
        getSqlMapClientTemplate().insert("insertResultDetail", resultDetail);
    }

    
    public int querySurveyCountBySurveyId(int surveyId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountForSurveyId", surveyId);
    }

    
    public int queryQuestionCountByQuestionId(int questionId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountForQuestionId", questionId);
    }

    
    public int queryAnswerCountByAnswerId(int answerId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountForAnswerId", answerId);
    }

}

package com.kariqu.omcenter.web;

import com.kariqu.categorymanager.helper.PropertyTreeJson;
import com.kariqu.common.JsonResult;
import com.kariqu.om.domain.Answer;
import com.kariqu.om.domain.Question;
import com.kariqu.om.domain.Survey;
import com.kariqu.om.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Asion
 * Date: 13-4-23
 * Time: 上午10:03
 */
@Controller
public class SurveysController {

    @Autowired
    private QuestionnaireService questionnaireService;

    @RequestMapping(value = "/surveys/list")
    public void surveysGrid(HttpServletResponse response) throws IOException {
        List<Survey> surveys = questionnaireService.queryAllSurvey();
        new JsonResult(true).addData("totalCount", surveys.size()).addData("result", surveys).toJson(response);
    }

    @RequestMapping(value = "/surveys/add", method = RequestMethod.POST)
    public void addSurveys(Survey survey, HttpServletResponse response) throws IOException {
        questionnaireService.createSurvey(survey);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/delete", method = RequestMethod.POST)
    public void deleteSurveys(int[] ids, HttpServletResponse response) throws IOException {
        for (int id : ids) {
            questionnaireService.deleteSurvey(id);
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/update", method = RequestMethod.POST)
    public void updateSurveys(HttpServletResponse response, Survey survey) throws IOException {
        Survey current = questionnaireService.querySurvey(survey.getId());
        current.setSurveyName(survey.getSurveyName());
        current.setSurveyExplain(survey.getSurveyExplain());
        questionnaireService.updateSurvey(current);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/question", method = RequestMethod.POST)
    public void queryQuestion(int id, HttpServletResponse response) throws IOException {
        new JsonResult(true).addData("question", questionnaireService.queryQuestionById(id)).toJson(response);
    }

    @RequestMapping(value = "/surveys/question/add", method = RequestMethod.POST)
    public void addQuestion(Question question, HttpServletResponse response) throws IOException {
        questionnaireService.createQuestion(question);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/question/delete", method = RequestMethod.POST)
    public void deleteQuestion(int id, HttpServletResponse response) throws IOException {
        questionnaireService.deleteQuestion(id);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/question/update", method = RequestMethod.POST)
    public void updateQuestion(HttpServletResponse response, int id, String value, Question.AnswerType answerType, boolean mustReply) throws IOException {
        Question question = questionnaireService.queryQuestionById(id);
        question.setQuestionDetail(value);
        question.setAnswerType(answerType);
        question.setMustReply(mustReply);
        questionnaireService.updateQuestion(question);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/answer", method = RequestMethod.POST)
    public void queryAnswer(int id, HttpServletResponse response) throws IOException {
        new JsonResult(true).addData("answer", questionnaireService.queryAnswerById(id)).toJson(response);
    }

    @RequestMapping(value = "/surveys/answer/add", method = RequestMethod.POST)
    public void addAnswer(HttpServletResponse response, Answer answer) throws IOException {
        questionnaireService.createAnswer(answer);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/answer/update", method = RequestMethod.POST)
    public void updateAnswer(HttpServletResponse response, int id, boolean hasWrite, String value) throws IOException {
        Answer answer = questionnaireService.queryAnswerById(id);
        answer.setHasWrite(hasWrite);
        answer.setAnswerDetail(value);
        questionnaireService.updateAnswer(answer);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/surveys/answer/delete", method = RequestMethod.POST)
    public void deleteAnswer(int id, HttpServletResponse response) throws IOException {
        questionnaireService.deleteAnswer(id);
        new JsonResult(true).toJson(response);

    }


    @RequestMapping(value = "/surveys/answer/tree/{id}")
    public
    @ResponseBody
    List<PropertyTreeJson> answerTree(HttpServletResponse response, @PathVariable("id") int id) {

        List<PropertyTreeJson> nodeList = new LinkedList<PropertyTreeJson>();

        if (id == -1) {
            return nodeList;
        }
        List<Question> questions = questionnaireService.queryQuestionBySurveyId(id);

        for (Question question : questions) {
            PropertyTreeJson propertyNode = new PropertyTreeJson();
            propertyNode.setId(question.getId());
            propertyNode.setLeaf(false);
            propertyNode.setText(question.getQuestionDetail());

            List<Answer> answers = questionnaireService.queryAnswerByQuestionId(question.getId());
            List<PropertyTreeJson> children = new ArrayList<PropertyTreeJson>(answers.size());

            for (Answer answer : answers) {
                PropertyTreeJson valueNode = new PropertyTreeJson();
                valueNode.setId(answer.getId());
                valueNode.setLeaf(true);
                valueNode.setText(answer.getAnswerDetail());
                children.add(valueNode);
            }
            propertyNode.setChildren(children);

            nodeList.add(propertyNode);
        }
        return nodeList;
    }

    @RequestMapping(value = "/surveys/answer/report/{id}")
    public void answerReport(HttpServletResponse response, @PathVariable("id") int id) throws IOException {

        List qlist = new LinkedList();

        Survey survey = questionnaireService.statisticsSurvey(id);

        for (final Question question : survey.getQuestionList()) {

            List q = new LinkedList();

            for (final Answer answer : question.getAnswerList()) {
                q.add(new HashMap() {{
                    put("answer", answer.getAnswerDetail());
                    put("total", answer.getNumber());
                    put("questionDetail", question.getQuestionDetail());
                }});
            }

            qlist.add(q);
        }

        new JsonResult(true).addData("reportData", qlist).toJson(response);

    }


}

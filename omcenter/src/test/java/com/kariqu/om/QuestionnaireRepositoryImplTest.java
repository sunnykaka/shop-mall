package com.kariqu.om;

import com.kariqu.om.domain.*;
import com.kariqu.om.repository.QuestionnaireRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Athens(刘杰)
 * @Time 2012-11-30 09:31
 * @since 1.0.0
 */
@ContextConfiguration(locations = {"/omCenter.xml"})
public class QuestionnaireRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Test
    @Rollback(false)
    public void testCreateQuestionnaire() {
        Survey survey = new Survey();
        survey.setSurveyName("问卷调查");
        survey.setSurveyExplain("亲爱的网购用户，您好！\n" +
                "为了更好地为您服务，我们邀请您花5分钟填写以下问卷。我们将为每一位填写完整问卷的客户赠送精美礼品一份。\n" +
                "您的每一个回答都将帮助我们为您提供更好的服务！谢谢您的支持:)");
        questionnaireRepository.createSurvey(survey);
        List<Survey> surveyList = questionnaireRepository.queryAllSurvey();
        assertNotNull(surveyList);
        assertEquals(1, surveyList.size());


        Question question1 = new Question();
        question1.setSurveyId(survey.getId());
        question1.setQuestionDetail("性别");
        question1.setAnswerType(Question.AnswerType.radio);
        questionnaireRepository.createQuestion(question1);
        List<Question> questionList1 = questionnaireRepository.queryQuestionBySurveyId(survey.getId());
        assertNotNull(questionList1);
        assertEquals(1, questionList1.size());

        Answer answer1 = new Answer();
        answer1.setQuestionId(question1.getId());
        answer1.setAnswerDetail("男");
        questionnaireRepository.createAnswer(answer1);
        List<Answer> answerList1 = questionnaireRepository.queryAnswerByQuestionId(question1.getId());
        assertNotNull(answerList1);
        assertEquals(1, answerList1.size());

        Answer answer2 = new Answer();
        answer2.setQuestionId(question1.getId());
        answer2.setAnswerDetail("女");
        questionnaireRepository.createAnswer(answer2);
        List<Answer> answerList2 = questionnaireRepository.queryAnswerByQuestionId(question1.getId());
        assertNotNull(answerList2);
        assertEquals(2, answerList2.size());
		
		
        Question question2 = new Question();
        question2.setSurveyId(survey.getId());
        question2.setQuestionDetail("您的职业");
        question2.setAnswerType(Question.AnswerType.checkbox);
        questionnaireRepository.createQuestion(question2);
        List<Question> questionList2 = questionnaireRepository.queryQuestionBySurveyId(survey.getId());
        assertNotNull(questionList2);
        assertEquals(2, questionList2.size());

        Answer answer3 = new Answer();
        answer3.setQuestionId(question2.getId());
        answer3.setAnswerDetail("政府机关、事业单位职员");
        questionnaireRepository.createAnswer(answer3);
        List<Answer> answerList3 = questionnaireRepository.queryAnswerByQuestionId(question2.getId());
        assertNotNull(answerList3);
        assertEquals(1, answerList3.size());

        Answer answer4 = new Answer();
        answer4.setQuestionId(question2.getId());
        answer4.setAnswerDetail("其他");
        answer4.setHasWrite(true);
        questionnaireRepository.createAnswer(answer4);
        List<Answer> answerList4 = questionnaireRepository.queryAnswerByQuestionId(question2.getId());
        assertNotNull(answerList4);
        assertEquals(2, answerList4.size());

        // =======================================================================

        Result result = new Result();
        result.setSurveyId(survey.getId());
		result.setAddress("广东深圳");
        result.setUserName("张三");
        result.setEmail("a@b.c");
        result.setMobile("13012345678");
        result.setSuggest("不要让我写这么多好不好???");
        questionnaireRepository.createResult(result);

        ResultDetail detail1 = new ResultDetail();
        detail1.setResultId(result.getId());
        detail1.setQuestionId(question1.getId());
        detail1.setAnswerId(answer1.getId());
        questionnaireRepository.createResultDetail(detail1);

        // 第二个问题置 2 个答案
        ResultDetail detail2 = new ResultDetail();
        detail2.setResultId(result.getId());
        detail2.setQuestionId(question2.getId());
        detail2.setAnswerId(answer4.getId());
        detail2.setAnswerInput("学生");
        questionnaireRepository.createResultDetail(detail2);

        ResultDetail detail3 = new ResultDetail();
        detail3.setResultId(result.getId());
        detail3.setQuestionId(question2.getId());
        detail3.setAnswerId(answer3.getId());
        questionnaireRepository.createResultDetail(detail3);

        // 第二个人来提交
        Result result2 = new Result();
        result2.setSurveyId(survey.getId());
        result2.setAddress("北京");
        result2.setUserName("李四");
        result2.setEmail("x@y.z");
        result2.setMobile("13899998888");
        result2.setSuggest("写得有点多有木有???");
        questionnaireRepository.createResult(result);

        ResultDetail detail4 = new ResultDetail();
        detail4.setResultId(result2.getId());
        detail4.setQuestionId(question1.getId());
        detail4.setAnswerId(answer2.getId());
        questionnaireRepository.createResultDetail(detail4);

        ResultDetail detail5 = new ResultDetail();
        detail5.setResultId(result2.getId());
        detail5.setQuestionId(question2.getId());
        detail5.setAnswerId(answer4.getId());
        detail5.setAnswerInput("学生");
        questionnaireRepository.createResultDetail(detail5);

        Survey survey1 = questionnaireRepository.querySurveyById(survey.getId());
        if (survey1 != null) {
            List<Question> questionList = questionnaireRepository.queryQuestionBySurveyId(survey.getId());
            for (Question question : questionList) {
                List<Answer> answerList = questionnaireRepository.queryAnswerByQuestionId(question.getId());
                for (Answer answer : answerList) {
                    // 统计答案数量
                    answer.setNumber(questionnaireRepository.queryAnswerCountByAnswerId(answer.getId()));
                }
                question.setNumber(questionnaireRepository.queryQuestionCountByQuestionId(question.getId()));
                question.setAnswerList(answerList);
            }
            survey1.setNumber(questionnaireRepository.querySurveyCountBySurveyId(survey.getId()));
            survey1.setQuestionList(questionList);
        }

        assertNotNull(survey1);
        // 回答 <性别男> 的只有 1个
        assertEquals(1, survey1.getQuestionList().get(0).getAnswerList().get(0).getNumber());
        // 回答 <您的职业> 问题的, 有 3个
        assertEquals(3, survey1.getQuestionList().get(1).getNumber());
        // 回答 <您的职业> 是 <其他> 的, 有 2个(目的还没有针对可输入的数据做统计)
        assertEquals(2, survey1.getQuestionList().get(1).getAnswerList().get(1).getNumber());
    }

}

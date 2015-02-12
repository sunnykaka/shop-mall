package com.kariqu.designcenter.domain.util;

import com.kariqu.designcenter.domain.exception.ModuleConfigException;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.DataType;
import com.kariqu.designcenter.domain.model.prototype.FormType;
import com.kariqu.designcenter.domain.model.prototype.Option;
import com.kariqu.designcenter.domain.model.prototype.Parameter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块配置文件和模块参数的转化类
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-13 下午10:56:44
 */
public class ModuleParamAndXmlConverter {

    protected static final Log logger = LogFactory.getLog(ModuleParamAndXmlConverter.class);

    public static List<Parameter> convertXmlToParameters(String xmlContent) throws ModuleConfigException {
        List<Parameter> parameters = new ArrayList<Parameter>();
        Element root = getRootElement(xmlContent);
        //定位参数节点，如果没有将返回空列表
        List<Node> params = root.selectNodes("/module/params/param");
        for (Node param : params) {

            String formTypeString = param.valueOf("@formType");
            String dataTypeString = param.valueOf("@dataType");

            DataType dataType = DataType.String;
            FormType formType = FormType.text;
            if (StringUtils.isNotEmpty(dataTypeString)) {
                dataType = DataType.valueOf(dataTypeString);
            }
            if (StringUtils.isNotEmpty(formTypeString)) {
                formType = FormType.valueOf(formTypeString);
            }

            //用参数名和参数值构造参数对象,@p这种属性如果不存在将会返回空字符串
            Parameter parameter = new Parameter(removeBlankChar(param.valueOf("@name")), processParamValue(param.getText(), dataType));
            parameter.setDataType(dataType);
            parameter.setFormType(formType);

            if (FormType.select.equals(formType) || FormType.radio.equals(formType)) {
                List<Node> options = param.selectNodes("option");
                List<Option> optionList = new ArrayList<Option>();
                for (Node node : options) {
                    Option option = new Option();
                    option.setLabel(node.valueOf("@label"));
                    option.setValue(node.valueOf("@value"));
                    if (StringUtils.isNotEmpty(node.valueOf("@selected")) && node.valueOf("@selected").equals("selected"))
                        option.setSelected(true);
                    optionList.add(option);
                }
                //select和radio用option列表构造
                parameter = new Parameter(removeBlankChar(param.valueOf("@name")), optionList);
            }

            parameter.setLabel(param.valueOf("@label"));
            parameter.setDescription(param.valueOf("@description"));
            parameters.add(parameter);
        }
        return parameters;
    }

    /**
     * 读取模块的配置，如果没配一些参数，默认是true
     *
     * @param xmlContent
     * @return
     * @throws ModuleConfigException
     */
    public static String readModuleConfig(String xmlContent) throws ModuleConfigException {
        Element root = getRootElement(xmlContent);
        Node cache = root.selectSingleNode("/module/config/" + RenderConstants.IS_CACHEABLE);
        Node edit = root.selectSingleNode("/module/config/" + RenderConstants.IS_EDIT);
        Node delete = root.selectSingleNode("/module/config/" + RenderConstants.IS_DELETE);
        StringBuilder sb = new StringBuilder();
        sb.append(RenderConstants.IS_CACHEABLE);
        sb.append("=");
        sb.append(cache != null ? cache.getText() : true);
        sb.append("\n");
        sb.append(RenderConstants.IS_EDIT);
        sb.append("=");
        sb.append(edit != null ? edit.getText() : true);
        sb.append("\n");
        sb.append(RenderConstants.IS_DELETE);
        sb.append("=");
        sb.append(delete != null ? delete.getText() : true);
        sb.append("\n");
        return sb.toString();
    }

    private static Element getRootElement(String xmlContent) throws ModuleConfigException {
        try {
            Document document = DocumentHelper.parseText(xmlContent);
            return document.getRootElement();
        } catch (DocumentException e) {
            logger.error("不能解析模块配置文件", e);
            throw new ModuleConfigException("不能解析模块配置文件,发现xml文档异常", e);
        } catch (Exception e) {
            logger.error("不能解析模块配置文件", e);
            throw new ModuleConfigException("不能解析模块配置文件,发现未知异常", e);
        }
    }

    private static String removeBlankChar(String input) {
        String result;
        result = StringUtils.trim(input);
        result = StringUtils.replace(result, "\n", "");
        return StringUtils.remove(result, " ");
    }

    /**
     * 按类型做处理
     *
     * @param input
     * @param dataType
     * @return
     */
    private static String processParamValue(String input, DataType dataType) {
        String result;
        result = StringUtils.trim(input);
        //result = StringUtils.replace(result, "\n", "");
        return result;
    }
}

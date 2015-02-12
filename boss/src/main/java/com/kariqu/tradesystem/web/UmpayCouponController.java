package com.kariqu.tradesystem.web;

import com.kariqu.common.DateUtils;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.service.CouponService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Timestamp;
import java.util.*;

/**
 * 联动优势现金券兑换
 * User: Asion
 * Date: 13-7-23
 * Time: 下午3:20
 */
@Controller
public class UmpayCouponController {

    private static final Log logger = LogFactory.getLog("umpayLog");


    @Autowired
    private JdbcTemplate jdbcTemplate;


    public static final String coupon_one = "6139805";
    public static final String coupon_two = "6139806";
    public static final String coupon_three = "6139807";

    //联动优势的参数key
    public static final String merId_key = "merId";
    public static final String goodsId_key = "goodsId";
    public static final String orderId_key = "orderId";
    public static final String amount_key = "amount";
    public static final String version_key = "version";


    private static Map<String, Long> productMap = new HashMap<String, Long>();


    //商品号的价格映射
    static {
        productMap.put(coupon_one, 3500l);
        productMap.put(coupon_two, 5000l);
        productMap.put(coupon_three, 10000l);
    }


    @Autowired
    private CouponService couponService;


    @RequestMapping(value = "/umpay/coupon", method = RequestMethod.POST)
    public void acceptCouponRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader br = request.getReader();
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        logger.info("联动优势请求:" + sb);

        String body = sb.toString();

        String[] pairList = body.split("&");

        final Map<String, String> paramMap = new HashMap<String, String>();


        for (String pair : pairList) {
            String[] kv = pair.split("=");
            if (kv.length != 2) {
                continue;
            }
            paramMap.put(kv[0], kv[1]);
        }

        StringBuilder data = new StringBuilder();
        data.append(paramMap.get(merId_key));
        data.append("|");
        data.append(paramMap.get(goodsId_key));
        data.append("|");
        data.append(paramMap.get(orderId_key));
        data.append("|");
        data.append(paramMap.get(amount_key));
        data.append("|");
        data.append(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT));
        data.append("|");

        String sign = body.substring(body.indexOf("&sign") + 6);
        boolean verify = SignEnc.verify(body.substring(0, body.indexOf("&sign")), sign);
        if (!verify) {
            logger.error("签名验证不能通过，可能有非法请求");
            flush(true, response, paramMap, data, "签名验证失败");
            return;
        }

        if (!productMap.containsKey(paramMap.get(goodsId_key))) {
            logger.error("发现商品号不存在:" + paramMap.get(goodsId_key));
            flush(true, response, paramMap, data, "商品号不存在");
            return;
        }


        try {

            int number = Integer.parseInt(paramMap.get(amount_key));
            if (number < 1) {
                logger.error("请求数量不合法:" + number);
                flush(true, response, paramMap, data, "请求数量不合法");
                return;
            }

            final StringBuilder retMsg = new StringBuilder();

            //判断是否有重复请求
            try {
                Map<String, Object> existRecord = jdbcTemplate.queryForMap("select orderId,coupon from umpayrequest where orderId=?", paramMap.get(orderId_key));
                Object coupon = existRecord.get("coupon");
                retMsg.append(coupon);
                logger.warn("发现重复请求，返回之前处理结果:" + coupon);
                flush(false, response, paramMap, data, retMsg.toString());
                return;
            } catch (EmptyResultDataAccessException e) {
                retMsg.append("现金券号:");

                // 一年的使用期限
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, 1);

                List<Coupon> coupons = couponService.generateCouponList(number, productMap.get(paramMap.get(goodsId_key)), 100,
                        Coupon.CouponType.Normal, new Date(), calendar.getTime());
                if (coupons.size() == 0) {
                    logger.error("平台没有生成现金券，请求张数:" + number);
                    throw new Exception("没有生成现金券");
                }
                for (int i = 0; i < coupons.size(); i++) {
                    if (i > 0) {
                        retMsg.append(",");
                    }
                    retMsg.append(coupons.get(i).getCode());
                }
                jdbcTemplate.update("insert into umpayrequest(orderId,coupon,goodsId,createDate) values(?,?,?,?)", paramMap.get(orderId_key), retMsg.toString(), paramMap.get(goodsId_key), new Timestamp(System.currentTimeMillis()));

                logger.warn("现金券生成，准备发往联动优势:" + retMsg);

                flush(false, response, paramMap, data, retMsg.toString());
            }

        } catch (Exception e) {
            logger.error("联动优势请求发生异常", e);
            flush(true, response, paramMap, data, "服务器发生错误");
        }

    }

    private void flush(boolean error, HttpServletResponse response, Map<String, String> paramMap, StringBuilder data, String retMsg) throws IOException {

        data.append(error ? "1111" : "0000");
        data.append("|");

        data.append(new BASE64Encoder().encode(retMsg.getBytes("GBK")));
        data.append("||");
        data.append(paramMap.get(version_key));

        String content = data + "|" + SignEnc.sign(data.toString());

        StringBuffer result = new StringBuffer();
        result.append("<META NAME=\"MobilePayPlatform\" CONTENT=\"" + content + "\">");
        String toFlush = result.toString();

        logger.info("发往联动优势:" + toFlush);

        //给eps返回
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(toFlush);
        out.flush();
        out.close();
    }


    static class SignEnc {


        public static final String CertPath = "/home/admin/umpay/testUmpay.cert.crt";
        public static final String KeyPath = "/home/admin/umpay/testMer.key.p8";

        public static String sign(String dataString) {
            FileInputStream fis = null;
            if (dataString == null) {
                throw new RuntimeException(
                        "the data string to be signed cannot be null");
            }

            byte[] kb = null;
            try {
                File f = new File(KeyPath);
                kb = new byte[(int) f.length()];
                fis = new FileInputStream(f);
                fis.read(kb);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("load the primary key failed");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception localException1) {
                        fis = null;
                    } finally {
                        fis = null;
                    }
                }
            }

            PKCS8EncodedKeySpec peks;
            KeyFactory kf;
            PrivateKey pk;
            try {
                peks = new PKCS8EncodedKeySpec(kb);
                kf = KeyFactory.getInstance("RSA");
                pk = kf.generatePrivate(peks);
            } catch (Exception e) {
                throw new RuntimeException("invalid primary key format");
            }

            Signature sig;
            byte[] sb;
            try {
                sig = Signature.getInstance("SHA1withRSA");
                sig.initSign(pk);
                sig.update(dataString.getBytes("gb2312"));
                sb = sig.sign();
            } catch (Exception e) {
                throw new RuntimeException("sign procedure failed");
            }

            String b64Str;
            try {
                BASE64Encoder base64 = new BASE64Encoder();
                b64Str = base64.encode(sb);
            } catch (Exception e) {
                throw new RuntimeException("base64 generation failed");
            }
            try {
                BufferedReader br = new BufferedReader(new StringReader(b64Str));
                String tmpStr = "";
                String tmpStr1 = "";
                while ((tmpStr = br.readLine()) != null)
                    tmpStr1 = tmpStr1 + tmpStr;

                b64Str = tmpStr1;
                return b64Str;
            } catch (Exception br) {
                logger.error(br);
                throw new RuntimeException("base64 generation failed");
            }
        }

        public static boolean verify(String dataString, String signString)
                throws RuntimeException {
            if (dataString == null) {
                throw new RuntimeException(
                        "the data string to be signed cannot be null");
            }

            FileInputStream fis = null;
            byte[] cb = null;
            try {
                File f = new File(CertPath);
                cb = new byte[(int) f.length()];
                fis = new FileInputStream(f);
                fis.read(cb);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("load the cert failed");
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (Exception localException1) {
                        fis = null;
                    } finally {
                        fis = null;
                    }

            }

            ByteArrayInputStream bais = new ByteArrayInputStream(cb);
            CertificateFactory cf;
            X509Certificate cert;
            try {
                cf = CertificateFactory.getInstance("X.509");
                cert = (X509Certificate) cf.generateCertificate(bais);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("load the cert failed");
            }
            try {
                BASE64Decoder base64 = new BASE64Decoder();
                byte[] signed = base64.decodeBuffer(signString);
                Signature sig = Signature.getInstance("SHA1withRSA");
                sig.initVerify(cert);
                sig.update(dataString.getBytes());
                return sig.verify(signed);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("verify procedure failed");
            }
        }
    }

}




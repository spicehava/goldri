package club.goldri.web.system.util;

import club.goldri.core.constant.Constant;
import club.goldri.core.util.StringUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created on 17/6/7.
 * 短信API产品的DEMO程序,工程中包含了一个SmsDemo类，直接通过
 * 执行main函数即可体验短信产品API功能(只需要将AK替换成开通了云通信-短信产品功能的AK即可)
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 *
 * 备注:Demo工程编码采用UTF-8
 * 国际短信发送请勿参照此DEMO
 */
public class SmsUtil {
    private static String product = "Dysmsapi"; //产品名称:云通信短信API产品,开发者无需替换
    private static String domain = "dysmsapi.aliyuncs.com"; //产品域名,开发者无需替换
    private static String accessKeyId = Constant.SMS_ACCESS_KEY_ID;
    private static String accessKeySecret = Constant.SMS_ACCESS_KEY_SECRET;

    /**
     *
     * @param phoneNumbers 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
     * @param signName 必填:短信签名-可在短信控制台中找到
     * @param templateCode 必填:短信模板-可在短信控制台中找到
     * @param templateParam 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为{\"name\":\"Tom\", \"code\":\"123\"},特别注意：参数值只能是数字和字母,并且模板中有参数时，该参数必须有对应的参数值
     * @param smsUpExtendCode 可选:上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
     * @param outId 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
     * @return
     * @throws ClientException
     */
    public static SendSmsResponse sendSms(String phoneNumbers, String signName, String templateCode, String templateParam,String smsUpExtendCode, String outId) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(phoneNumbers);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);

        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);

        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为{\"name\":\"Tom\", \"code\":\"123\"}
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        //特别注意：参数值只能是数字和字母
        if(StringUtil.isNotEmpty(templateParam)){
            request.setTemplateParam(templateParam);
        }

        //可选:上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        if(StringUtil.isNotEmpty(smsUpExtendCode)){
            request.setSmsUpExtendCode(smsUpExtendCode);
        }

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        if(StringUtil.isNotEmpty(outId)){
            request.setOutId(outId);
        }

        //hint 请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }

    /**
     * 查询短信发送详情
     * @param phoneNumber 必填:手机号码
     * @param bizId 可选:流水号
     * @param sendDate 必填:发送日期 支持30天内记录查询，格式yyyyMMdd
     * @param pageSize 可选:每页行项目数，可以为空，为空时默认为10条
     * @param currentPage 可选:从第几页开始获取数据，当为空时，默认为1
     * @return
     * @throws ClientException
     */
    public static QuerySendDetailsResponse querySendDetails(String phoneNumber,String bizId, String sendDate, Long pageSize, Long currentPage) throws ClientException {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填:手机号码
        request.setPhoneNumber(phoneNumber);
        //可选:流水号
        if(StringUtil.isNotEmpty(bizId)){
            request.setBizId(bizId);
        }
        //必填:发送日期 支持30天内记录查询，格式yyyyMMdd
        request.setSendDate(sendDate);
        //必填:每页行项目数，可以为空，为空时默认为10条
        request.setPageSize(pageSize == null ? 10L : pageSize);
        //必填:当前页码从1开始计数
        request.setCurrentPage(currentPage == null ? 1L : currentPage);

        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

        return querySendDetailsResponse;
    }

    //验证日期格式
    public static boolean isValidDate(String dateStr, String pattern) {
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
        try {
            format.setLenient(false);
            format.parse(dateStr);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 多线程方式调用发送消息，避免阻塞
     * @param phoneNumbers
     * @param signName
     * @param templateCode
     * @param templateParam
     * @param smsUpExtendCode
     * @param outId
     */
    public static void sendMsgThread(String phoneNumbers, String signName, String templateCode, String templateParam,String smsUpExtendCode, String outId){
        new SendMsgThead(phoneNumbers, signName, templateCode, templateParam, smsUpExtendCode, outId).start();
    }

    /**
     * 多线程方式处理短信发送
     */
    public static class SendMsgThead extends Thread {
        private String phoneNumbers;
        private String signName;
        private String templateCode;
        private String templateParam;
        private String smsUpExtendCode;
        private String outId;

		public SendMsgThead(String phoneNumbers, String signName, String templateCode, String templateParam,String smsUpExtendCode, String outId){
            this.phoneNumbers = phoneNumbers;
            this.signName = signName;
            this.templateCode = templateCode;
            this.templateParam = templateParam;
            this.smsUpExtendCode = smsUpExtendCode;
            this.outId = outId;
        }

        public SendMsgThead(String phoneNumbers, String signName, String templateCode, String templateParam){
            this.phoneNumbers = phoneNumbers;
            this.signName = signName;
            this.templateCode = templateCode;
            this.templateParam = templateParam;
        }

        @Override
        public void run() {
            try {
                sendSms(phoneNumbers, signName, templateCode, templateParam, smsUpExtendCode, outId);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws ClientException, InterruptedException {

        String phoneNumbers = "18831626126";
        String signName = "阿里云短信测试专用";
        String templateCode = "SMS_119190070";
        String templateParam = null;
        String smsUpExtendCode = null;
        String outId = null;
        //测试用例
        //1、模板中有参数，而未提供参数值 --> 模板中有参数时，templateParam必须有对应的参数值，否则会报错
        //发短信
        SendSmsResponse response = sendSms(phoneNumbers, signName, templateCode, templateParam, smsUpExtendCode, outId);
        System.out.println("短信接口返回的数据----------------");
        System.out.println("Code=" + response.getCode());
        System.out.println("Message=" + response.getMessage());
        System.out.println("RequestId=" + response.getRequestId());
        System.out.println("BizId=" + response.getBizId());

        Thread.sleep(3000L);

        String phoneNumber = "18831626126";
        String bizId = response.getBizId();
        String sendDate = "20171226";
        Long pageSize = null;
        Long currentPage = null;
        //查明细
        if(response.getCode() != null && response.getCode().equals("OK")) {
            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(phoneNumber, bizId, sendDate, pageSize, currentPage);
            System.out.println("短信明细查询接口返回数据----------------");
            System.out.println("Code=" + querySendDetailsResponse.getCode());
            System.out.println("Message=" + querySendDetailsResponse.getMessage());
            int i = 0;
            for(QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs())
            {
                System.out.println("SmsSendDetailDTO["+i+"]:");
                System.out.println("Content=" + smsSendDetailDTO.getContent());
                System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                System.out.println("OutId=" + smsSendDetailDTO.getOutId());
                System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
                System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
                System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
                System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
            }
            System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
            System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
        }

    }
}

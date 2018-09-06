package com.psyb.service.common.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.util.FileNameMapper;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class StringUtil {

	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 返回一个定长的随机字符串(只包含大小写字母、数字)
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	public static String getUUID4MD5() {
		return EncryptUtil.md5(UUID.randomUUID().toString());
	}
	
	public static String getPrefixFromVoip(String voipid) {
		if(voipid.contains("$"))
		{
			String[] vp = voipid.split("\\$");
			if(vp.length > 0)
			{
				return vp[0].trim();
			}
		}
		int len = voipid.length();
		if(len > 8)
		{
			return voipid.substring(0, len-8);
		}
		return voipid;
	}
	
	/**
	 * 替换SQL注入的单引号（'）
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceSingleQuote(String str) {
		if (str != null && !"".equals(str)) {
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (c == '\'') {
					result.append("");
				} else {
					result.append(str.charAt(i));
				}
			}
			return result.toString();
		} else {
			return "";
		}
	}

	/**
	 * 根据位数生成随机数
	 * 
	 * @param number
	 *            位数
	 * @return n 随机数
	 */
	public static String generateRandomNum(int number) {

		StringBuffer num = new StringBuffer();
		while (number > 0) {
			num.append(String.valueOf(new Random().nextInt(10)));// 获取大于等于0，小于10的整型随机数
			number--;
		}
		String n = num.toString();
		return n;
	}

	/**
	 * 将空串转为0
	 * 
	 * @param s
	 *            空串(null或者"")
	 * @return
	 */
	public static String NullToZero(String s) {
		if (s == null || s.trim().length() == 0) {
			s = "0";
		}
		return s;
	}

	/**
	 * 根据传参number，生成12位字符串，位数不够在number前补零
	 * 
	 * @param number
	 *            数字
	 * @return 12位字符串
	 */
	public static String generateTwelveString(BigDecimal number) {
		String s = "";
		if (number != null) {
			s = number.toString();
			if (s.length() == 1) {
				s = "00000000000" + s;
			} else if (s.length() == 2) {
				s = "0000000000" + s;
			} else if (s.length() == 3) {
				s = "000000000" + s;
			} else if (s.length() == 4) {
				s = "00000000" + s;
			} else if (s.length() == 5) {
				s = "0000000" + s;
			} else if (s.length() == 6) {
				s = "000000" + s;
			} else if (s.length() == 7) {
				s = "00000" + s;
			} else if (s.length() == 8) {
				s = "0000" + s;
			} else if (s.length() == 9) {
				s = "000" + s;
			} else if (s.length() == 10) {
				s = "00" + s;
			} else if (s.length() == 11) {
				s = "0" + s;
			} else {
				s = "" + s;
			}
		}
		return s;
	}

	/**
	 * 根据传参number，生成8位字符串，位数不够在number前补零
	 * 
	 * @param number
	 *            数字
	 * @return 8位字符串
	 */
	public static String generateEightString(BigDecimal number) {
		String s = "";
		if (number != null) {
			s = number.toString();
			if (s.length() == 1) {
				s = "0000000" + s;
			} else if (s.length() == 2) {
				s = "000000" + s;
			} else if (s.length() == 3) {
				s = "00000" + s;
			} else if (s.length() == 4) {
				s = "0000" + s;
			} else if (s.length() == 5) {
				s = "000" + s;
			} else if (s.length() == 6) {
				s = "00" + s;
			} else if (s.length() == 7) {
				s = "0" + s;
			} else {
				s = "" + s;
			}
		}
		return s;
	}

	/**
	 * 生成32位随机字符串
	 * 
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}

	/**
	 * 获取短信消息类型
	 * 
	 * @param msgType
	 *            消息类型
	 * @return
	 */
	public static String getMsgType(String msgType) {
		String type = null;
		if (msgType != null && msgType.trim().length() > 0) {
			if ("0".equals(msgType)) {// 普通短信
				type = "15";
			} else if ("1".equals(msgType)) {// 长短信
				type = "8";
			} else {
				type = "15";
			}
		} else {
			type = "15";
		}
		return type;
	}

	/**
	 * 获取短信类型
	 * 
	 * @param smsType
	 *            短信类型
	 * @return
	 */
	public static String getSmsType(String smsType) {
		String type = null;
		if (smsType != null && smsType.trim().length() > 0) {
			if ("0".equals(smsType)) {// 上行短信
				type = "0";
			} else if ("1".equals(smsType)) {// 手机接收状态报告
				type = "2";
			} else {
				type = "0";
			}
		} else {
			type = "0";
		}
		return type;
	}

	/**
	 * 生成订单Id
	 * 
	 * @param prefix
	 *            计费类型前缀
	 * @param sn
	 *            Rest服务器序号
	 * @return
	 */
//	public static String getOrderId(String prefix, String sn) {
//		String time = DateUtil.getTimeOfToday();// 当前日期
//		String randomNum = generateRandomNum(7);// 7位流水号
//		// RS（2位）+ REST服务器序号（1001，4位）+ 计费类型（SMS，LIC，3位）+
//		// 时间戳（yyyyMMddHHmmss,14位）+7位随机数=30位
//		String orderid = Constants.RESTSERVER + sn + prefix + time + randomNum;
//		return orderid;
//	}

	/**
	 * 计算字符长度
	 * 
	 * @param value
	 * @return
	 */
	public static int length(String value) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < value.length(); i++) {
			// 获取一个字符
			String temp = value.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				// 中文字符长度为2
				valueLength += 2;
			} else {
				// 其他字符长度为1
				valueLength += 1;
			}
		}
		return valueLength;
	}

	/**
	 * 从后往前截取字符串
	 * 
	 * @param src
	 *            被截取的字符串
	 * @param end
	 *            从后往前截取的位数
	 * @return
	 */
	public static String subEndStr(String src, int end) {
		StringBuffer des1 = new StringBuffer("");
		StringBuffer des = new StringBuffer("");
		char c = '0';
		if (src != null && src.trim().length() > 0) {
			int i = src.length() - 1;
			while (i >= 0) {
				c = src.charAt(i);
				des1.append(c);
				i--;
			}
			String s = des1.toString();
			String ss = s.substring(0, end);
			if (ss != null && ss.trim().length() > 0) {
				int j = ss.length() - 1;
				while (j >= 0) {
					c = ss.charAt(j);
					des.append(c);
					j--;
				}
			}
		}
		return des.toString();
	}

	/**
	 * 获取语音文件串
	 * 
	 * @param prefix
	 *            语音文件路径
	 * @param suffix
	 *            语音文件后缀
	 * @param split
	 *            语音文件分隔符
	 * @param voiceCode
	 *            语音验证码
	 * @return 语音验证码文件串
	 */
	public static String getVoiceCode(String prefix, String suffix, String split, String voiceCode) {
		StringBuffer vcode = new StringBuffer();
		String tips = "tishiyin";
		vcode.append(prefix).append(tips).append(suffix).append(split);
		if (voiceCode != null && voiceCode.trim().length() > 0) {
			System.out.println("before lower case VoiceCode = " + voiceCode);
			voiceCode = voiceCode.toLowerCase();
			System.out.println("after lower case VoiceCode = " + voiceCode);
			int len = voiceCode.length();
			for (int i = 0; i < len; i++) {
				char c = voiceCode.charAt(i);
				if (i == len - 1) {
					vcode.append(prefix).append(c).append(suffix);
				} else {
					vcode.append(prefix).append(c).append(suffix).append(split);
				}
			}
			System.out.println("VoiceCode = " + vcode.toString());
		}
		return vcode.toString();
	}

	public static String getFSIndex(String index) {
		String str = "";
		int length = index.length();
		switch (length) {
		case 1:
			str = "0000" + index;
			break;
		case 2:
			str = "000" + index;
			break;
		case 3:
			str = "00" + index;
			break;
		case 4:
			str = "0" + index;
			break;
		case 5:
			str = index;
		}
		return str;
	}

	/**
	 * json兼容 判断json是否以[开头 ]结尾
	 */
	public static String checkJson(String json) {
		return json.startsWith("[") && json.endsWith("]") ? json : json.startsWith("[") && !json.endsWith("]") ? json
				+ "]" : !json.startsWith("[") && json.endsWith("]") ? "[" + json : "[" + json + "]";
	}

	/**
	 * 解析from to
	 */
	public static String getFromOrTo(String str) {
		return str.substring(str.indexOf(":") + 1, str.lastIndexOf("@")).toLowerCase();
	}

	/**
	 * 判断数据长度是否超过有效长度
	 */
	public static boolean isTooLong(String[] args, int[] lens) {
		boolean flag = false;
		int argLen = args.length;
		if (args.length > lens.length) {
			argLen = lens.length;
		} else if (args.length < lens.length) {
			argLen = args.length;
		}
		for (int i = 0; i < argLen; i++) {
			if (args[i] != null) {
				if (length(args[i]) > lens[i]) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 去掉换行符
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 添加JSON换行符
	 * 
	 * @param str
	 * @return
	 */
	public static String addLineFeed(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断是否为浮点数，包括double和float
	 * 
	 * @param str
	 *            传入的字符串
	 * @return 是浮点数返回true,否则返回false
	 */
	public static boolean isDouble(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断list中字符串是否被指定的字串包含
	 * 
	 * @param str
	 * @param list
	 * @return
	 */
	public static boolean checkInStr(String str, List<String> list) {
		for (String l : list) {
			if (str.indexOf(l) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 字符串数组转字符串
	 * 
	 * @param sep
	 * @param list
	 * @return
	 */
	public static String StringListToString(List<String> list, String sep) {
		String str = null;
		if (list != null && list.size() > 0) {
			str = list.get(0);
			for (int i = 1; i < list.size(); i++) {
				String tmp = sep + list.get(i);
				str = str + tmp;
			}
		}
		return str;
	}

	/**
	 * 判断是否为数字或字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumericAndABC(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile("[A-Z,a-z,0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 判断是否为正确的电话号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isPhoneNum(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9,#,*,-]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * @param hangup
	 * @return
	 */
	public static String getHangupDesc(String type) {
		String desc = "未知错误";
		int hangup = (type != null && type.length() != 0 ? Integer.parseInt(type) : -999);
		switch (hangup) {
		case 1:
			desc = "结束通话";
			break;
		case 2:
			desc = "账户欠费或者设置的通话时间到";
			break;
		case -1:
			desc = "【异常】被叫没有振铃就收到了挂断消息!";
			break;
		case -2:
			desc = "【异常】呼叫超时没有接通被挂断!";
			break;
		case -3:
			desc = "【异常】回拨: 主叫接通了主叫挂断!";
			break;
		case -4:
			desc = "【异常】回拨: 主叫通道创建了被挂断!";
			break;
		case -5:
			desc = "【异常】被叫通道建立了被挂断!";
			break;
		case -6:
			desc = "【异常】系统鉴权失败!";
			break;
		case -7:
			desc = "【异常】第三方鉴权失败!";
			break;
		case -8:
			desc = "【异常】直拨: 被叫振铃了挂断!";
			break;
		case -9:
			desc = "【异常】回拨: 被叫振铃了挂断!";
			break;
		case -10:
			desc = "【异常】回拨: 主叫振铃了挂断!";
			break;
		}

		return desc;
	}

	public static void unzip(String zipFilepath, final String destDir, final List<String> unzipFileList)
			throws BuildException, RuntimeException {
		File f = new File(zipFilepath);
		if (!f.exists()) {
			throw new RuntimeException("zip file [" + zipFilepath + "] does not exist.");
		}

		Project proj = new Project();
		Expand expand = new Expand();
		expand.setProject(proj);
		expand.add(new FileNameMapper() {

			@Override
			public String[] mapFileName(String fileName) {
				if (unzipFileList != null) {
					unzipFileList.add(fileName);
				}
				return null;
			}

			@Override
			public void setFrom(String arg) {

			}

			@Override
			public void setTo(String arg) {

			}
			
		});
		expand.setTaskType("unzip");
		expand.setTaskName("unzip");
		expand.setEncoding("GBK");
		expand.setSrc(new File(zipFilepath));
		expand.setDest(new File(destDir));
		expand.execute();
	}

	public static long getFileSize(String path) {
		File f = new File(path);
		if (f.exists()) {
			return f.length();
		}
		return 0;
	}
	
	/**
	 * 判断strs数组是否包含list集合外的字符串
	 * @param list
	 * @param strs
	 * @return
	 */
	public static boolean checkInStr(List<String> list,String[] strs){
		for (String str : strs){
			if (!list.contains(str)){
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * 读取数据
	 * @param buffer 数据缓冲区
	 * @param channel
	 * @return count 数据大小
	 * @exception IOException
	 */
	public synchronized static int readBlock(ByteBuffer buffer, FileChannel channel) 
	{     
		int count = 0;
		try 
		{     
			buffer.clear();
		    count = channel.read(buffer);    
		    buffer.flip(); 
		    if (count <= 0)
		    {
		    	return 0;      
		    }     
		} catch (IOException e) {     
		}     
		return count;      
	}

	/**
	 * 对象转xml
	 * 
	 * @param obj
	 * @throws JAXBException 
	 */
	public static String Object2Xml(Object obj) throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(obj.getClass());
		Marshaller marshaller = context.createMarshaller();  
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(obj, writer);
        return writer.toString();
	}
	
	/**
	 * 格式化xml
	 * 
	 * @param document
	 * @return
	 */
	public static String formatXml(Document document) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setTrimText(false);
		format.setIndentSize(2);
		format.setNewlines(true);
		format.setPadText(true);
		StringWriter out = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(out, format);
		try {
			xmlWriter.write(document);
			xmlWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}
	
	/**
	 * 文件名后追加时间戳
	 * 
	 * @param fileName
	 * @return
	 */
	public static String appendFileName(String fileName) {
		String suffix = fileName.substring(fileName.indexOf("."));
		fileName = fileName.substring(0, fileName.indexOf("."));
		fileName = fileName + DateUtil.formatTimestamp(System.currentTimeMillis());
		return fileName + "." + suffix;
	}
	
	/**
	 * @Title: isBlank 
	 * @Description: 判断是否为空 
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isBlank(String str){
		return org.apache.commons.lang.StringUtils.isBlank(str);
	}
	
	/**
	 * @Title: isNotBlank 
	 * @Description: 判断是否为空
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isNotBlank(String str){
		return org.apache.commons.lang.StringUtils.isNotBlank(str);
	}
	
	/**
	 * @Title: getUserAcc 
	 * @Description: 根据appId userName获取useracc
	 * @param @param appId
	 * @param @param userName
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getUserAcc(String appId, String userName){
		StringBuilder builder = new StringBuilder();
		builder.append(appId);
		builder.append("#");
		builder.append(userName);
		return builder.toString();
	}
	
	/**
	 * @Title: getAppIdFormUserAcc 
	 * @Description: 根据userAcc获取appId
	 * @param @param userAcc
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getAppIdFormUserAcc(String userAcc){
		if (StringUtils.isEmpty(userAcc)){
			return "";
		}
		String[] strs = userAcc.split("#");
		if(strs != null && strs.length>1){
			return strs[0];
		}else{
			return "";
		}
	}
	
	/**
	 * @Title: getUserNameFormUserAcc 
	 * @Description: 根据userAcc获取userName
	 * @param @param userAcc
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getUserNameFormUserAcc(String userAcc){
		if (StringUtils.isEmpty(userAcc)){
			return "";
		}
		String[] strs = userAcc.split("#");
		if(strs != null && strs.length>1){
			return strs[1];
		}else{
			return userAcc;
		}
	}
}

package com.psyb.service.common;

import java.io.File;

/**
 * This Version is ${2015-03-26} 系统常量接口
 */
public interface Constants {

	
	public static final String AUTH_PATH = "/server/auth";
	public static final String IVR_PATH = "/ivr";
	public static final String INLET_PATH = "/inlet";
	public static final String SET_PATH = "/Calls";
	
	/**
	 * 计费类型 calltype
	 * */
	public static final int CT_DCALL = 0;
	public static final int CT_CALLBACK = 1;
	public static final int CT_P2P = 2;
	public static final int CT_IVRIN = 3;
	public static final int CT_IVROUT = 5;
	public static final int CT_NOTICE = 4;
	public static final int CT_P2P_V = 11;
	public static final int CT_VERIFY = 13;
	public static final int CT_LANDCALL = 14;
	
	/**
	 * 呼叫类型直拨0，回拨1 点对点 7
	 */
	public static final String TYPE_DCALL = "0";
	public static final String TYPE_CALLBACK = "1";  
	public static final String TYPE_P2P_M = "2";
	public static final String TYPE_P2P_V = "3";  
	public static final String TYPE_P2P = "7";
	
	/**
	 * 呼叫结果 callresult
	 * */
	
	public static final int CR_FAIL = 0;
	public static final int CR_SUCC = 1;
	public static final int CR_ACC_ERR = -1;
	
	/**
	 * 成功
	 */
	public static final String SUCC = "000000";
	public static final String ERR_NOTEMPID = "600001";  //外呼通知无模板
	public static final String ERR_UNDISNUM = "600002";  //外呼显号未配置
	public static final String ERR_ACCOUNT = "600003";  //账号错误
	
	
	/**
	 * 错误码描述
	 */
	public static final String SUCC_DESC = "success";
	public static final String SUCC_NOTEMPID_DESC = "No outbound notification configuration template";
	public static final String SUCC_UNDISNUM_DESC = "No transmission service number is configured";
	public static final String ERR_ACCOUNT_DESC = "Account Error";
	
	/**
	 * 外呼临时话单存储(默认是24小时，秒表示，属于不经常变化常量）
	 */
	public static final long CDR_DISABLED_TIME = 86400;
	
	//中间号
	public static final long XNUM_DISABLED_TIME = 86400;
	
	
	/**
	 * 内部服务名称
	 * 
	 */
	public static final String SERVER_REST = "RestServer";
	public static final String SERVER_CALLRESULT = "CallResult";
	
	//黑词 命令类型 0:新增黑词 1：修改黑词 2:删除黑词
	public static final String BALCK_WORDS_ADD="0";
	public static final String BALCK_WORDS_MODIFY="1";
	public static final String BALCK_WORDS_DELETE="2";
	//词类型 0:黑词 1:白词
	public static final String BALCK_WORDS_TYPE="0";
	public static final String WHITE_WORDS_TYPE="1";
	
	/**
	 * REST API版本号
	 */
	public static final String ApiVersion2015 = "2015-03-26";
	
	/**
	 * sig失效时间(默认是24小时，毫秒表示，属于不经常变化常量）
	 */
	public static final long SIG_DISABLED_TIME = 86400000;

	public static final String ERROR_FILE_NAME = "ErrorCode";
	public static final String GENERAL_FILE_NAME = "GeneralConfig";
	public static final String CHECKRIGHT_FILE_NAME = "UnCheckRight";
	public static final String UNCHECKRIGHT_NAME = "UnCheckRight";
	public static final String MDN_BLACK_LIST_NAME = "NumberBlackList";

	/**
	 *  Script目录
	 */
	public static final String SCRIPT_DIR_MATCH = File.separator + "script" + File.separator;

	/**
	 * 配置文件版本号目录
	 */
	public static final String VERSION_DIR = File.separator + "version";
	
	/**
	 * real ip
	 */
	public static final String X_REAL_IP = "x-real-ip";

	/**
	 * GeneralConfig KEY
	 */
	public static final String UPLOAD_FILE_PATH = "upload_file_path";
	public static final String SCALE_PIX = "scale_pix";
	public static final String MAX_UPLOAD_FILE = "max_upload_file";
	public static final String FILESERIAL = "fileserial";
	public static final String UPLOAD_LOG_FILE_PATH = "upload_log_file_path";
	public static final String UPLOAD_IP_SPEED_FILE_PATH = "upload_ip_speed_file_path";
	public static final String GENERATE_SERVER_ADDRS = "generate_server_addrs";
	public static final String GENERATE_VTM_UPLOAD_PATH = "vtm_upload_path";
	public static final String GENERATE_APACHE_DOWNLOAD_IP = "apache_download_ip";
	public static final String IM_QUEUE_NAME = "im_queue_name";
	public static final String MCM_QUEUE_NAME = "mcm_queue_name";
	public static final String LOCAL_IP = "local_ip";
	public static final String ENVIRONMENT_SETTING = "environment_setting";
	
	/**
	 * 应用key长度
	 */
	public static final int APP_LENGTH = 32;

	

	/**
	 * view.xml中定义的视图名称
	 */
	public static final String XML_VIEW_NAME = "cloudcom";

	/**
	 * view.xml中定义的视图名称
	 */
	public static final String JSON_VIEW_NAME = "jsonView";
	
	/**
	 * 读取不到数据次数
	 */
	public static final int NOT_READ_COUNT = 5;
	
	/**
	 * 缩略图 - 最大高（宽）缩放后的像素
	 */
	public static final String THUMBNAIL_SCALE_PIX = "120";
	
	/**
	 * 缩略图标识
	 */
	public static final String THUMBNAIL_SUFFIX = "_thum";
	
	/**
	 * IP测速文件后缀
	 */
	public static final String IPSPEED_SUFFIX = ".csv.gz";
	
	/**
	 * chunked编码
	 */
	public static final String TRANSFER_ENCODING = "chunked";
	
	/**
	 * chunked结束符
	 */
	public static final String CHUNKED_STOP = "0";
	
	/**
	 * 缓存大小
	 */
	public static final int BUFFERSIZE = 1024 * 8;
	
	/**
	 * 禁言状态
	 */
	public static final int FORBID = 2;
	
	/**
	 * 上传附件临时信息有效时间
	 */
	public static final int ATTACH_EXPIRATION = 172800;
	
	/**
	 * 缓存时间 单位毫秒
	 */
	public static final int EXPIRATION = 60000;
	
	/**
	 * 上传文件类型  2、语音 3、视频 4、图片
	 */
	public static final int FILE_TYPE_AUDIO = 2;
	public static final int FILE_TYPE_VIDEO = 3;
	public static final int FILE_TYPE_IMAGE = 4;
	
	/**
	 * 上传文件类型
	 */
	public static final int FILE_UPLOAD_TYPE = 28;
	
	/**
	 * 多渠道类型
	 */
	public static final int MCM_TYPE = 126;
	
	/**
	 * 用户多渠道文件上传
	 */
	public static final String ATTACH_MCM_USER = "2";
	
	/**
	 * 第三方多渠道文件上传
	 */
	public static final String ATTACH_MCM_OS = "3"; 
	
	/**
	 * 发送IM消息
	 */
	public static final int SEND_MESSAGE = 3;
	
	/**
	 * 发送多渠道消息
	 */
	public static final int SEND_MCM = 53;
	
	/**
	 * 版本号文件名称
	 */
	public static final String VERSION_NAME = File.separator + "version.txt";
	
	/**
	 * 服务器类型
	 */
	public static final int SERVER_CONNECTOR = 1;
	public static final int SERVER_LVS = 2;
	public static final int SERVER_FILESERVER = 3;
	
	/**
	 * 
	 */
	public static final String SESSIONID_KEY = "CSID";
	
	/**
	 * GeneralConfig 配置占位符
	 */
	public static final String GENERALCONFIG_PLACEHOLDER = "${fileserial}";
	
	/**
	 * vtm上传路径
	 */
	public static final String UPLOAD_VTM_PATH = "vtm";
	
	/**
	 * 群组ID前缀
	 */
	public static final String GROUPID_PREFIX = "g";
	
	/**
	 * USERACC 间隔符
	 */
	public static final String USERACC_MARK = "#";
	
	/**
	 * 视频图片后缀
	 */
	public static final String VIDEO_IMAGE_SUFFIX = "png";
	
	/**
	 * 视频图片宽度
	 */
	public static final int VIDEO_IMAGE_WIDTH = 320;
	
	/**
	 * 视频图片高度
	 */
	public static final int VIDEO_IMAGE_HEIGHT = 240;
	
	/**
	 * 视频预览图旋转角度
	 */
	public static final int IMAGE_ROTATE_ANGLE = 90;

	/**
	 * 结果集为空
	 */
	public static final int DB_EMPTY = -999;

	public static final int DB_COUNT_EMPTY = 0;

	public static final int FILE_NAME_SIZE = 320;
	
	/**
	 * 历史记录消息获取，目前只取1-10对应的消息类型
	 */
	public static final String MSG_TYPES_FILTER = " (msg_type = 1 OR msg_type = 2 OR msg_type = 3 OR msg_type = 4 OR msg_type = 5 OR msg_type = 6 OR msg_type = 7 OR msg_type = 9 OR msg_type = 10) ";
	
	/**
	 * 获取历史消息的时间限制，90天内
	 */
	public static final long MSG_HISTORY_MAX_TIME = 60l * 60l * 24l * 90l * 1000l;

	/**
	 * 客户端获取最近联系人列表条数限制
	 */
	public static final int RECENTLY_CONTACTS_MAX_LIMIT = 50;
	
	/**
	 * 客户端获取最近联系人列表默认时间7天
	 */
	public static final long RECENTLY_CONTACTS_DEFAULT_TIME = 60l * 60l * 24l * 7l * 1000l;
	
	/**
	 * 获取错误码数量限制
	 */
	public static final int ERROR_CODE_LIMIT = 100;
	
	/**
	 * user_register redis key
	 */
	public static final String KEY_USER_REGISTER = "ytx017|";
	
	/**
	 * user_group redis key
	 */
	public static final String KEY_USER_GROUP = "ytx024|";
	
	/**
	 * auth code redis key
	 */
	public static final String AUTH_CODE_KEY = "ytx041|";
	
}

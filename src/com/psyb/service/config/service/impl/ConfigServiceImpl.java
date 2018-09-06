package com.psyb.service.config.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.exception.CCPCassandraDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.util.BufferedRandomAccessFile;
import com.psyb.service.common.util.DateUtil;
import com.psyb.service.common.util.FileUtil;
import com.psyb.service.common.util.StringUtil;
import com.psyb.service.config.dao.ConfigDao;
import com.psyb.service.config.form.ConfigFileForm;
import com.psyb.service.config.form.UploadFileResp;
import com.psyb.service.config.model.ConfigFile;
import com.psyb.service.config.model.UploadAttr;
import com.psyb.service.config.service.ConfigService;

//@Service
public class ConfigServiceImpl implements ConfigService {

	private Logger logger = Logger.getLogger(ConfigServiceImpl.class);
	
	@Autowired
	private ConfigDao configDao;
	
	@Override
	public void downConfigFile(ConfigFileForm configFileForm, HttpServletRequest request, 
			HttpServletResponse response) throws CCPServiceException, CCPCassandraDaoException {
		long startTime = System.currentTimeMillis();
		String type = configFileForm.getType();
		String version = configFileForm.getVersion();
		
		if (type == null || "".equals(type)) {
			throw new CCPServiceException(ScriptManager.buildError("16056"));
		}
		if (version == null || "".equals(version)) {
			throw new CCPServiceException(ScriptManager.buildError("16057"));
		}
		if (!StringUtil.isNumeric(version)) {
			throw new CCPServiceException(ScriptManager.buildError("16059"));
		}
		
		// 查出最新版本号和文件路径
		ConfigFile configFile = configDao.CheckConfigFile(type, version);
		if (configFile == null) {
			throw new CCPServiceException(ScriptManager.buildError("16060"));
		}
		int lastVersion = configFile.getVersion();
		// 判断是否是最新版本
		if (Integer.parseInt(version) >= lastVersion) {
			throw new CCPServiceException(ScriptManager.buildError("16061"));
		}
		
		String filePath = configFile.getFilePath();
		logger.info("filePath:" + filePath);
		if (filePath == null || "".equals(filePath)) {
			throw new CCPServiceException(ScriptManager.buildError("16057"));
		}
		
		long sPos = 0;// 开始下载位置
		String range = request.getHeader("Range");
		if (range != null) {
			String[] rangs = range.split("=")[1].split("-");
			sPos = Long.parseLong(rangs[0]);
		}
		
		InputStream is = null;
		OutputStream out = null;
		int len = 0;// 读取字节数
		int downloadLen = 0;// 已下载字节数
		File file = new File(filePath);
		
		if (file.exists()) {
			try {
			    is = new FileInputStream(file);
			    is.skip(sPos);
			    out = response.getOutputStream();
				int BUFFER_SIZE = 1024 * 800;// 数据缓冲区大小800KB
			    byte[] buf = new byte[BUFFER_SIZE];
				while ((len = is.read(buf, 0, buf.length)) != -1) {
					out.write(buf, 0, len);
					downloadLen = downloadLen + len;
	            	logger.info("readBuff:" + len);
				}
				
				String contentRange = "bytes " + sPos + "-" + downloadLen + "/" + file.length();
				logger.info("Content-Range:" + contentRange);
				logger.info("Content-Length:" + downloadLen);
				// 服务端所返回的数据体的内容类型
				response.setContentType("application/octet-stream");
				response.setContentLength(downloadLen);
				response.setHeader("Content-Range", contentRange);
			} catch(IOException e) {
				logger.info(e);
				throw new CCPServiceException(ScriptManager.buildError("16055"));
			} finally {
				FileUtil.closeQuietly(is, out);
			}
		} else {
			logger.info("文件不存在");
			throw new CCPServiceException(ScriptManager.buildError("16058"));
		}
		long endTime = System.currentTimeMillis();
		logger.info("下载文件总共耗时：" + (endTime - startTime) + "ms");
	}
	
	@Override
	public UploadFileResp uploadFile(String token, HttpServletRequest request) 
			throws CCPServiceException, CCPCassandraDaoException {
		long startTime = System.currentTimeMillis();
		
		if (token == null || "".equals(token)) {
			throw new CCPServiceException(ScriptManager.buildError("16052"));
		}
		
		// 根据token查出文件名
		UploadAttr ua = configDao.CheckUploadToken(token);
		if (ua == null) {
			throw new CCPServiceException(ScriptManager.buildError("16054"));
		}
		
		int maxUploadSize = Integer.parseInt(ScriptManager.getScriptManager().getLocalConfig(Constants.MAX_UPLOAD_FILE));
		String filePath = ScriptManager.getScriptManager().getLocalConfig(Constants.UPLOAD_FILE_PATH);
		
		String appId = ua.getAppId();
		String currDate = DateUtil.getDateOfString(DateUtil.getDateOfTimestamp(), 11);
		
		String path = appId + File.separator + currDate;
		
		// 拼接目录
		filePath = filePath + File.separator + path;
		
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		String fileName = ua.getLocalFileName();
		String fullPath = filePath + File.separator + fileName;
		logger.info("fullPath:" + fullPath);

		long offset = 0;// 文件上传大小
		String transferEncod = request.getHeader("Transfer-Encoding");
		// 判断是chunked上传还是断点续传
		if (Constants.TRANSFER_ENCODING.equals(transferEncod)) {
			this.chunkedUploadFile(request, fullPath, path, maxUploadSize, token);
		} else {
//			offset = this.continueUploadFile(request, fullPath, path, maxUploadSize, token);
			String tempPath = filePath + "/temp";
			File tempFile = new File(tempPath);
			if (!tempFile.exists()) {
				tempFile.mkdirs();
			}
			tempPath = tempPath + File.separator + fileName;
			offset = this.uploadFileTest(request, fullPath, fullPath, tempPath, maxUploadSize, token);
		}
		
    	long endTime = System.currentTimeMillis();
    	logger.info("上传文件共计耗时：" + (endTime - startTime) + "ms");
    	return new UploadFileResp(Constants.SUCC, offset);
	}
	
	/**
	 * 断点续传
	 * @param request
	 * @param fullPath
	 * @param maxUploadSize
	 * @return
	 */
	public long continueUploadFile(HttpServletRequest request, String fullPath, String path, int maxUploadSize, String token)
			throws CCPServiceException {
		String range = request.getHeader("Range");
		String length = request.getHeader("Content-Length");
		
		// 上传开始位置
		long sPos = 0;
		// 上传文件大小
		long fileSize = 0;
		
		if (range != null && !"".equals(range)) {
			String[] rangs = range.split("=");
			if (rangs.length != 2) {
				throw new CCPServiceException(ScriptManager.buildError("16009"));
			}
			String[] lens = rangs[1].split("-");
			if (lens.length != 2) {
				throw new CCPServiceException(ScriptManager.buildError("16009"));
			}
	        sPos = Long.parseLong(lens[0]);
	        String[] size = lens[1].split("/");
	        if (size.length != 2) {
	        	throw new CCPServiceException(ScriptManager.buildError("16009"));
	        }
	        // 上传结束位置
	        long ePos = Long.parseLong(size[0]);
	        fileSize = Long.parseLong(size[1]);
	        if (fileSize > maxUploadSize) {
	        	throw new CCPServiceException(ScriptManager.buildError("16053"));
	        }
	        if (sPos > fileSize || ePos > fileSize) {
	        	logger.info("maxFileSize:" + maxUploadSize);
	        	throw new CCPServiceException(ScriptManager.buildError("16011"));
	        }
	        long uPos = ePos - sPos;
			if (uPos != Long.parseLong(length)) {
				throw new CCPServiceException(ScriptManager.buildError("16010"));
			}
		}
		
		// 已上传字节数
		long offset = sPos;
		File file = null;
		InputStream is = null;
    	BufferedRandomAccessFile out = null;
    	try {
    		file = new File(fullPath);
    		
    		// 判断续传文件是否存在
    		if (sPos != 0) {
    			if (!file.exists()) {
    				throw new CCPServiceException(ScriptManager.buildError("16053"));
    			}
    		}
    		
            is = request.getInputStream();
            out = new BufferedRandomAccessFile(file, "rw", 1024*8);
            
            //定位文件指针到sPos位置 
            out.seek(sPos);
            byte[] b = new byte[1024 * 8];
            // 已读取字节数
            int len = 0;
            
            while ((len=is.read(b, 0, b.length)) != -1) {
            	out.write(b, 0, len);
            	offset = offset + len;
            	logger.info("readBuff:" + len);
            	
    	        if (offset > maxUploadSize) {
    	        	logger.info("maxFileSize:" + maxUploadSize);
    	        	throw new CCPServiceException(ScriptManager.buildError("16053"));
    	        }
            }
            
            // 全部上传完成后生成缩略图
            if ((offset == fileSize) || range == null) {
            	//从配置文件中读取最大高（宽）缩放后的像素
         		String strScalePix = ScriptManager.getScriptManager().getLocalConfig(Constants.SCALE_PIX);
         		int scalePix = Integer.parseInt(strScalePix);
         		
    			if (file.exists()) {
    				String dstImgPath = fullPath + Constants.THUMBNAIL_SUFFIX;
//    				if(ImageUtil.isImage(file)) {
//    					if (ImageUtil.isScale(scalePix, file)) {
//            	    		// 生成缩略图
//            	    		ImageUtil.resize(scalePix, fullPath, dstImgPath);
//            			} else {
//            				logger.info("图片宽度和高度未超过标准像素，无需缩放..");
//            				logger.info("scalePix:" + scalePix);
//            				ImageUtil.copyFile(new File(fullPath), new File(dstImgPath));
//            			}
//    				}
    			}

    			// 向MQ发送消息
//            	sendMessag.sendAMQMessage(token, path);
            }
    	} catch(IOException e) {
    		logger.info(e);
//    		FileUtil.deleteFile(file, is, out);
    		throw new CCPServiceException(ScriptManager.buildError("16051"));
    	} finally {
//    		FileUtil.closeQuietly(is, out);
    		try {
    			is.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return offset;
	}
	
	/**
	 * chunked
	 * @param request
	 * @param fullPath
	 * @param maxUploadSize
	 * @return
	 * @throws CCPServiceException
	 */
	public void chunkedUploadFile(HttpServletRequest request, String fullPath, String path, int maxUploadSize, String token) 
			throws CCPServiceException {
		InputStream is = null;
		
	    int len = 0;// 已读取字节数
	    int readErrorCount = 1;// 读取错误数据次数
	    boolean isFinish = false;// 上传是否完成
	    StringBuffer strBuf = new StringBuffer(1000);
	    byte[] buf = new byte[1024 * 8];// 缓冲区大小8KB
		long offset = 0;// 已上传字节数
		
		FileOutputStream fos = null;
		File file = null;
		try {
			file = new File(fullPath);
			fos = new FileOutputStream(file);
			is = request.getInputStream();
			while (!isFinish) {
		        while ((len = is.read(buf, 0, buf.length)) != -1) {
		            fos.write(buf, 0, len);
		            offset = offset + len;
	            	logger.info("readBuff:" + len);
		            strBuf.append(new String(buf, 0, len, "UTF-8"));
		            
		            // 判断是否接收完成
		            isFinish = strBuf.toString().lastIndexOf(Constants.CHUNKED_STOP) != -1;
		            strBuf.delete(0, strBuf.length());
		            
		            if (offset > maxUploadSize) {
		               logger.info("maxFileSize:" + maxUploadSize);
		               logger.info("上传文件超过最大限制，删除文件" + fullPath);
		               FileUtil.deleteFile(file, is, fos);
		               throw new CCPServiceException(ScriptManager.buildError("16053"));
		            }
		        }
		        logger.info("上传文件大小:" + offset);
		        
		        if (len == -1 && !isFinish) {
		            logger.info("第" + readErrorCount + "次读取数据失败，等待1000ms后再次读取..");
		            readErrorCount++;
		            Thread.sleep(1000);// 等待1秒
		        }
		        if (readErrorCount > Constants.NOT_READ_COUNT) {
		           logger.info("超过" + Constants.NOT_READ_COUNT * 1000 + " ms读取数据失败，上传失败..");
		           FileUtil.deleteFile(file, is, fos);
		    	   throw new CCPServiceException(ScriptManager.buildError("16051"));
		        }
		    }
			// 向MQ发送消息
//			sendMessag.sendAMQMessage(token, path);
		} catch(Exception e) {
			logger.info(e);
			FileUtil.deleteFile(file, is, fos);
    		throw new CCPServiceException(ScriptManager.buildError("16051"));
		} finally {
			FileUtil.closeQuietly(is, fos);
		}
	}
	
	public long uploadFileTest(HttpServletRequest request, String fullPath, String path, String tempPath, int maxUploadSize, String token)
			throws CCPServiceException {
		String range = request.getHeader("Range");
		String length = request.getHeader("Content-Length");
		
		// 上传开始位置
		long sPos = 0;
		// 上传文件大小
		long fileSize = 0;
		
		if (range != null && !"".equals(range)) {
			String[] rangs = range.split("=");
			if (rangs.length != 2) {
				throw new CCPServiceException(ScriptManager.buildError("16009"));
			}
			String[] lens = rangs[1].split("-");
			if (lens.length != 2) {
				throw new CCPServiceException(ScriptManager.buildError("16009"));
			}
		    sPos = Long.parseLong(lens[0]);
		    String[] size = lens[1].split("/");
		    if (size.length != 2) {
		    	throw new CCPServiceException(ScriptManager.buildError("16009"));
		    }
		    // 上传结束位置
		    long ePos = Long.parseLong(size[0]);
		    fileSize = Long.parseLong(size[1]);
		    if (fileSize > maxUploadSize) {
		    	throw new CCPServiceException(ScriptManager.buildError("16053"));
		    }
		    if (sPos > fileSize || ePos > fileSize) {
		    	logger.info("maxFileSize:" + maxUploadSize);
		    	throw new CCPServiceException(ScriptManager.buildError("16011"));
		    }
		    long uPos = ePos - sPos;
			if (uPos != Long.parseLong(length)) {
				throw new CCPServiceException(ScriptManager.buildError("16010"));
			}
		}
		
		// 已上传字节数
		long offset = sPos;
		File tempFile = null;
		InputStream is = null;
		BufferedOutputStream out = null;
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel fc = null;
		try {
			tempFile = new File(tempPath);
			
			// 判断续传文件是否存在
//			if (sPos != 0) {
//				if (!file.exists()) {
//					throw new CCPServiceException(ScriptManager.buildError("16053"));
//				}
//			}
			
		    is = request.getInputStream();
		    out = new BufferedOutputStream(new FileOutputStream(tempFile));
		    
//		    fis = new FileInputStream();
		    fos = new FileOutputStream(tempFile);
		    
		    fc = fos.getChannel();
		    
		    byte[] b = new byte[1024 * 8];
		    // 已读取字节数
		    int len = 0;
		    
		    while ((len=is.read(b, 0, b.length)) != -1) {
		    	
//		    	out.write(b, 0, len);
		    	offset = offset + len;
		    	logger.info("readBuff:" + len);
		    	
		        if (offset > maxUploadSize) {
		        	logger.info("maxFileSize:" + maxUploadSize);
		        	throw new CCPServiceException(ScriptManager.buildError("16053"));
		        }
		    }
		    out.close();
		    if (sPos == 0) {
		    	logger.info("fullPath:" + fullPath);
		    	logger.info("tempPath:" + tempPath);
		    	tempFile.renameTo(new File(fullPath));
		    } else {
		    	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tempFile));
		    	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(fullPath), true));
		    	while ((len=bis.read(b, 0, b.length)) != -1) {
		    		bos.write(b, 0, len);
		    		bos.flush();
		    	}
		    	bis.close();
		    	bos.close();
		    }
		    
		    // 全部上传完成后生成缩略图
		    if ((offset == fileSize) || range == null) {
		    	//从配置文件中读取最大高（宽）缩放后的像素
		 		String strScalePix = ScriptManager.getScriptManager().getLocalConfig(Constants.SCALE_PIX);
		 		int scalePix = Integer.parseInt(strScalePix);
		 		
//				if (file.exists()) {
//					String dstImgPath = fullPath + Constants.THUMBNAIL_SUFFIX;
//					if(ImageUtil.isImage(file)) {
//						if (ImageUtil.isScale(scalePix, file)) {
//		    	    		// 生成缩略图
//		    	    		ImageUtil.resize(scalePix, fullPath, dstImgPath);
//		    			} else {
//		    				logger.info("图片宽度和高度未超过标准像素，无需缩放..");
//		    				logger.info("scalePix:" + scalePix);
//		    				ImageUtil.copyFile(new File(fullPath), new File(dstImgPath));
//		    			}
//					}
//				}
		
				// 向MQ发送消息
//		    	sendMessag.sendAMQMessage(token, path);
		    }
		} catch(IOException e) {
			logger.info(e);
		//	FileUtil.deleteFile(file, is, out);
			throw new CCPServiceException(ScriptManager.buildError("16051"));
		} finally {
		//	FileUtil.closeQuietly(is, out);
			try {
				is.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return offset;
	}
}

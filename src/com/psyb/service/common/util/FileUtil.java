package com.psyb.service.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ming.sample.util.Random;

import com.psyb.service.common.Constants;

public class FileUtil {

	public static final Logger logger = LogManager.getLogger(FileUtil.class);

	/**
	 * 关闭字节输出流
	 * 
	 * @param bos
	 */
	public static void closeQuietly(ByteArrayOutputStream bos) {
		try {
			if (bos != null) {
				bos.close();
			}
		} catch (IOException e) {

		}
	}

	/**
	 * 关闭输入流和文件输出流
	 * 
	 * @param is
	 * @param fos
	 */
	public static void closeQuietly(InputStream is, FileOutputStream fos) {
		try {
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {

		}
	}

	/**
	 * 关闭输入流和输出流
	 * 
	 * @param is
	 * @param outputStream
	 */
	public static void closeQuietly(InputStream is, OutputStream outputStream) {
		try {
			if (is != null) {
				is.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {

		}
	}

	/**
	 * 关闭输入流和缓冲输出流
	 * 
	 * @param is
	 * @param bos
	 */
	public static void closeQuietly(InputStream is, BufferedOutputStream bos) {
		try {
			if (bos != null) {
				bos.close();
			}
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {

		}
	}
	
	/**
	 * 关闭输入流和缓冲输出流
	 * 
	 * @param is
	 * @param bos
	 */
	public static void closeQuietly(InputStream is, BufferedOutputStream bos, FileOutputStream fos) {
		try {
			if (bos != null) {
				bos.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			
		}
	}

	/**
	 * 关闭缓冲输入流和缓冲输出流
	 * 
	 * @param bis
	 * @param bos
	 */
	public static void closeQuietly(BufferedInputStream bis,
			BufferedOutputStream bos) {
		try {
			if (bos != null) {
				bos.close();
			}
			if (bis != null) {
				bis.close();
			}
		} catch (IOException e) {

		}
	}

	/**
	 * 关闭输入流、文件输出流后删除文件
	 * 
	 * @param file
	 * @param fis
	 */
	public static void deleteFile(File file, InputStream is,
			FileOutputStream fos) {
		closeQuietly(is, fos);
		if ((file != null) && (file.isFile()) && (file.exists())) {
			boolean isDeleted = file.delete();
			if (isDeleted) {
				logger.info("删除文件：" + file.getName());
			}
		}
	}

	/**
	 * 关闭缓冲输入流和缓冲输出流后删除文件
	 * 
	 * @param file
	 * @param bis
	 * @param bos
	 */
	public static void deleteFile(File file, BufferedInputStream bis,
			BufferedOutputStream bos) {
		closeQuietly(bis, bos);
		if (file.exists()) {
			if (file.delete()) {
				logger.info("删除临时文件：" + file.getName());
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.delete()) {
				logger.info("删除文件:" + file.getName());
			}
		}
	}

	/**
	 * 关闭输入流和缓冲输出流后删除文件
	 * 
	 * @param file
	 * @param is
	 * @param bos
	 */
	public static void deleteFile(File file, InputStream is,
			BufferedOutputStream bos) {
		closeQuietly(is, bos);
		deleteFile(file);
	}
	
	public static void deleteFile(File file, InputStream is,
			BufferedOutputStream bos, FileOutputStream fos) {
		closeQuietly(is, bos, fos);
		deleteFile(file);
	}

	/**
	 * 复制或追加文件
	 * 
	 * @param srcFile
	 * @param newFile
	 * @param position
	 *            从当前位置开始寫入
	 * @param append
	 *            true追加文件
	 */
	public static void moveFile(File srcFile, File newFile, long position,
			boolean append) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(newFile, append);
			FileChannel fci = fis.getChannel();
			FileChannel fco = fos.getChannel();
			fco.transferFrom(fci, position, fci.size());
		} catch (Exception e) {
			deleteFile(newFile, fis, fos);
		} finally {
			closeQuietly(fis, fos);
		}
	}

	/**
	 * 获取文件后缀
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileNameSuffix(String fileName) {
		if (fileName == null || fileName.lastIndexOf(".") == -1) {
			return "";
		}
		int pos = fileName.lastIndexOf(".");
		return fileName.substring(pos);
	}

	/**
	 * 生成文件名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String generatorFileName(String fileName) {
		String fileNameSuffix = "";
		if (fileName.equals("")) {
			fileNameSuffix = Constants.IPSPEED_SUFFIX;
		} else {
			fileNameSuffix = FileUtil.getFileNameSuffix(fileName);
		}
		fileName = System.currentTimeMillis() + Random.nextNumString(6)
				+ fileNameSuffix;
		return fileName;
	}
	
	/**
	 * 读取文件内容
	 * 
	 * @param file
	 * @return
	 */
	public static String readFile(File file) {
		try {
			FileReader reader = new FileReader(file);
			BufferedReader buff = new BufferedReader(reader);
			return buff.readLine();
		} catch (IOException e) {
			logger.error(e);
		}
		return null;
	}
	
	/**
	 * 写文件
	 * 
	 * @param path
	 * @param value
	 */
	public static void writeFile(String path, String fileName, String value) {
		File file = new File(path);
		FileWriter writer = null;
		try {
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(path + fileName);
			writer = new FileWriter(file);
			writer.write(value);
			writer.flush();
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
}

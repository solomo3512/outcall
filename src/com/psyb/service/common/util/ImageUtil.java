package com.psyb.service.common.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageUtil {

	private static Logger logger = LogManager.getLogger(ImageUtil.class);

	/**
	 * 判断文件是否是图片
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isImage(String fileName) {
		String fileSuffix = getImageSuffix(fileName);
		// 是否JPG、PNG、GIF格式的图片
		return "jpg".equalsIgnoreCase(fileSuffix)
				|| "png".equalsIgnoreCase(fileSuffix)
				|| "gif".equalsIgnoreCase(fileSuffix);
	}

	/**
	 * 判断文件是否需要缩放 - JPG、PNG、GIF格式的图片且高（宽）度大于缩放后的最大高（宽）度像素
	 * 
	 * @param scalePix
	 *            最大宽（高）的缩放后的像素
	 * @param file
	 * @return true - 需要缩放，false - 不需要缩放
	 * @throws IOException
	 */
	public static boolean isScale(int scalePix, File file) throws IOException {
		boolean isScale = false;
		BufferedImage bi = ImageIO.read(file);
		int width = bi.getWidth();
		int height = bi.getHeight();
		// 是否需要缩放
		if (width > scalePix || height > scalePix) {
			isScale = true;// 需要缩放
		}
		return isScale;
	}

	/**
	 * 缩放图片，可以对JPG、PNG、GIF三种图片等比缩放
	 *
	 * @param scalePix
	 *            最大宽（高）的缩放后的像素
	 * @param srcImgPath
	 *            原图路径
	 * @param dstImgPath
	 *            缩放图路径
	 */
	public static void resize(int scalePix, String srcImgPath, String dstImgPath) {
		try {
			String imgSuffix = getImageSuffix(srcImgPath);
			File dstImgFile = new File(dstImgPath); // 缩略图文件
			BufferedImage srcImgBuf = getImage(srcImgPath);
			int width = srcImgBuf.getWidth();// 原图宽度
			int height = srcImgBuf.getHeight();// 原图高度
			// 是否需要缩放
			if (width > scalePix || height > scalePix) {
				int scaleWidth = scalePix;// 缩略图宽度
				int scaleHeight = (int) (height * scaleWidth / width);// 缩略图高度，以宽度为基准，等比例缩放图片
				if (scaleHeight > scalePix) {
					scaleHeight = scalePix;
					scaleWidth = (int) (width * scaleHeight / height);// 以高度为基准，等比例缩放图片
				}
				double sx = (double) scaleWidth / width;
				double sy = (double) scaleHeight / height;
				BufferedImage dstImgBuf = null;
				int type = srcImgBuf.getType();
				if (type == BufferedImage.TYPE_CUSTOM) {
					ColorModel cm = srcImgBuf.getColorModel();
					WritableRaster raster = cm.createCompatibleWritableRaster(
							scaleWidth, scaleHeight);
					boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
					dstImgBuf = new BufferedImage(cm, raster,
							isAlphaPremultiplied, null);
				} else {
					dstImgBuf = new BufferedImage(scaleWidth, scaleHeight, type);
					Graphics2D g = dstImgBuf.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_RENDERING,
							RenderingHints.VALUE_RENDER_QUALITY);
					g.drawRenderedImage(srcImgBuf, AffineTransform
							.getScaleInstance(sx, sy));
					g.dispose();
				}
				String imgType = "jpeg";
				if ("png".equalsIgnoreCase(imgSuffix)) {
					imgType = "png";
				}
				ImageIO.write(dstImgBuf, imgType, dstImgFile);
			}
		} catch (IOException e) {
			logger.info(e);
		}
	}

	/**
	 * 缩放图片，可以对JPG、PNG、GIF三种图片等比缩放
	 *
	 * @param scalePix
	 *            最大宽（高）的缩放后的像素
	 * @param srcImgPath
	 *            原图路径
	 * @param dstImgPath
	 *            缩放图路径
	 */
	/*public static void resizeNew2(int scalePix, String srcImgPath, String dstImgPath) {
		try {
			File file = new File(srcImgPath);
			String suffix = srcImgPath.substring(srcImgPath.lastIndexOf(".") + 1);
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(suffix);
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);
			//BufferedImage srcImgBuf = ImageIO.read(new File(srcImgPath));
			//int width = srcImgBuf.getWidth();// 原图宽度
			//int height = srcImgBuf.getHeight();// 原图高度
			int width = reader.getWidth(0);
			int height = reader.getHeight(0);
			// 是否需要缩放
			if (width > scalePix || height > scalePix) {
				int scaleWidth = scalePix;// 缩略图宽度
				int scaleHeight = (height * scaleWidth / width);// 缩略图高度，以宽度为基准，等比例缩放图片
				if (scaleHeight > scalePix) {
					scaleHeight = scalePix;
					scaleWidth = (width * scaleHeight / height);// 以高度为基准，等比例缩放图片
				}
				String lastDstImgPath = dstImgPath + "." + getImageSuffix(srcImgPath);

				Thumbnails.of(srcImgPath).size(scaleWidth,scaleHeight).toFile(lastDstImgPath);
				File thumFile = new File(lastDstImgPath);
				thumFile.renameTo(new File(dstImgPath));
			} else {
				logger.info("Image width and height does not exceed standard of pixels without scaling..");
				FileUtil.moveFile(new File(srcImgPath), new File(dstImgPath), 0, false);
			}
		} catch (IOException e) {
			logger.info(e);
		}
	}*/
	
	/**
	 * 缩放图片，可以对JPG、PNG、GIF三种图片等比缩放
	 *
	 * @param scalePix
	 *            最大宽（高）的缩放后的像素
	 * @param srcImgPath
	 *            原图路径
	 * @param dstImgPath
	 *            缩放图路径
	 */
	public static void resizeNew2(int scalePix, String srcImgPath, String dstImgPath) {
		try {
			BufferedImage srcImgBuf = getImage(srcImgPath);
			int width = srcImgBuf.getWidth();// 原图宽度
			int height = srcImgBuf.getHeight();// 原图高度
			// 是否需要缩放
			if (width > scalePix || height > scalePix) {
				int scaleWidth = scalePix;// 缩略图宽度
				int scaleHeight = (height * scaleWidth / width);// 缩略图高度，以宽度为基准，等比例缩放图片
				if (scaleHeight > scalePix) {
					scaleHeight = scalePix;
					scaleWidth = (width * scaleHeight / height);// 以高度为基准，等比例缩放图片
				}
				BufferedImage _image = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_INT_RGB);
				_image.getGraphics().drawImage(srcImgBuf, 0, 0, scaleWidth, scaleHeight, null); // 绘制缩小后的图
				FileOutputStream newimageout = new FileOutputStream(dstImgPath); // 输出到文件流
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimageout);
				encoder.encode(_image); // 近JPEG编码
				newimageout.close();
			} else {
				logger.info("Image width and height does not exceed standard of pixels without scaling..");
				FileUtil.moveFile(new File(srcImgPath), new File(dstImgPath), 0, false);
			}
		} catch (IOException e) {
			logger.info(e);
		}
	}

	private static BufferedImage getImage(String srcImgPath) {
		Image image = Toolkit.getDefaultToolkit().getImage(srcImgPath);
		if (image instanceof BufferedImage) {
			return (BufferedImage)image;
		}
		image = new ImageIcon(image).getImage();
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			logger.error(e.getMessage());
		}
		if (bimage == null) {
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		Graphics g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}


	/**
	 * 获取图片后缀名
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static String getImageSuffix(String fileName) {
		String suffix = "jpg";
		if (fileName != null && fileName.trim().length() > 0) {
			char dot = '.';
			int beginIndex = fileName.lastIndexOf(dot) + 1;
			suffix = fileName.substring(beginIndex);
		}
		return suffix;
	}

	public static void generateThumb(int suffix, String srcImageName,
			String dstImageFileName) {
		FileOutputStream fileOutputStream = null;
		JPEGImageEncoder encoder = null;
		BufferedImage tagImage = null;
		Image srcImage = null;
		try {
			File srcImageFile = new File(srcImageName);
			srcImage = ImageIO.read(srcImageFile);
			int srcWidth = srcImage.getWidth(null);// 原图片宽度
			int srcHeight = srcImage.getHeight(null);// 原图片高度
			int dstMaxSize = 120;// 目标缩略图的最大宽度/高度，宽度与高度将按比例缩写
			int dstWidth = srcWidth;// 缩略图宽度
			int dstHeight = srcHeight;// 缩略图高度
			float scale = 0;
			// 计算缩略图的宽和高
			if (srcWidth > dstMaxSize) {
				dstWidth = dstMaxSize;
				scale = (float) srcWidth / (float) dstMaxSize;
				dstHeight = Math.round((float) srcHeight / scale);
			}
			srcHeight = dstHeight;
			if (srcHeight > dstMaxSize) {
				dstHeight = dstMaxSize;
				scale = (float) srcHeight / (float) dstMaxSize;
				dstWidth = Math.round((float) dstWidth / scale);
			}
			// 生成缩略图
			tagImage = new BufferedImage(dstWidth, dstHeight,
					BufferedImage.TYPE_INT_RGB);
			tagImage.getGraphics().drawImage(srcImage, 0, 0, dstWidth,
					dstHeight, null);
			fileOutputStream = new FileOutputStream(dstImageFileName);
			encoder = JPEGCodec.createJPEGEncoder(fileOutputStream);
			encoder.encode(tagImage);
			fileOutputStream.close();
			fileOutputStream = null;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e) {
				}
				fileOutputStream = null;
			}
			encoder = null;
			tagImage = null;
			srcImage = null;
			System.gc();
		}
	}
}

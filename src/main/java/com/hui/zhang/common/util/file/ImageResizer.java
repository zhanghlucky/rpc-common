package com.hui.zhang.common.util.file;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class ImageResizer {
	/***
	 * @param srcImgPath
	 *            原图片路径
	 * @param distImgPath
	 *            转换大小后图片路径
	 * @param width
	 *            转换后图片宽度
	 * @param height
	 *            转换后图片高度
	 */
	public static ImgVO resizeImage(String srcImgPath, String distImgPath, int width, int height)throws IOException {
		
		//boolean proportion = true;
		File fromFile = new File(srcImgPath);
		BufferedImage fromBi = ImageIO.read(fromFile);
		int newWidth=width;
		int newHeight=height;
		if(width!=0&&height!=0){
			newWidth=width;
			newHeight=height;
			
		}
		if(width==0&&height>0){
			newHeight=height;
			double rate =  (double) height/((double) fromBi.getHeight(null));
			newWidth = (int) (((double) fromBi.getWidth(null))*rate);
		}
		if(width>0&&height==0){
			newWidth=width;
			double rate =  (double) width/((double) fromBi.getWidth(null));
			newHeight = (int) (((double) fromBi.getHeight(null))*rate);
		}

		BufferedImage toBi = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = toBi.createGraphics();
		toBi = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = toBi.createGraphics();
		Image from = fromBi.getScaledInstance(newWidth, newHeight, fromBi.SCALE_AREA_AVERAGING);
		g2d.drawImage(from, 0, 0, null);
		g2d.dispose();
		File toFile = new File(distImgPath);
		ImageIO.write(toBi, "png", toFile);

		/**
		 * File srcFile = new File(srcImgPath); Image srcImg =
		 * ImageIO.read(srcFile);
		 * 
		 * BufferedImage buffImg = null; buffImg = new BufferedImage(width,
		 * height, BufferedImage.TYPE_INT_RGB);
		 * buffImg.getGraphics().drawImage(srcImg.getScaledInstance(width,
		 * height, Image.SCALE_SMOOTH), 0, 0, null);
		 * 
		 * File file = new File(distImgPath); ImageIO.write(buffImg, suffix,
		 * file);
		 **/
		ImgVO imgVO = new ImgVO();
		imgVO.setHeight(newHeight);
		imgVO.setWidth(newWidth);
		imgVO.setSize(toFile.length());
		return imgVO;
	}
	
	public static ImgVO resizeImageToPngDpi(String srcImgPath, String distImgPath, int width, int height,int dpi) throws IOException {

		// boolean proportion = true;
		File fromFile = new File(srcImgPath);
		BufferedImage fromBi = ImageIO.read(fromFile);
		int newWidth = width;
		int newHeight = height;
		if (width != 0 && height != 0) {
			newWidth = width;
			newHeight = height;

		}
		if (width == 0 && height > 0) {
			newHeight = height;
			double rate = (double) height / ((double) fromBi.getHeight(null));
			newWidth = (int) (((double) fromBi.getWidth(null)) * rate);
		}
		if (width > 0 && height == 0) {
			newWidth = width;
			double rate = (double) width / ((double) fromBi.getWidth(null));
			newHeight = (int) (((double) fromBi.getHeight(null)) * rate);
		}

		BufferedImage toBi = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = toBi.createGraphics();
		toBi = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = toBi.createGraphics();
		Image from = fromBi.getScaledInstance(newWidth, newHeight, fromBi.SCALE_AREA_AVERAGING);
		g2d.drawImage(from, 0, 0, null);
		g2d.dispose();
		File toFile = new File(distImgPath);
		ImageIO.write(toBi, "png", toFile);
		
		toPngDpi(distImgPath,dpi);//改变dpi
		
		ImgVO imgVO = new ImgVO();
		imgVO.setHeight(newHeight);
		imgVO.setWidth(newWidth);
		imgVO.setSize(toFile.length());
		return imgVO;
	}
	
	public static void toPngDpi(String fpath,int dpi) throws IOException {
		File infile = new File(fpath);
		File outfile = new File(fpath);
		String suffix = fpath.substring(fpath.lastIndexOf(".") + 1, fpath.length());

		ImageReader reader = ImageIO.getImageReadersByFormatName(suffix).next();
		reader.setInput(new FileImageInputStream(infile), true, false);
		IIOMetadata data = reader.getImageMetadata(0);
		BufferedImage gridImage = reader.read(0);

		// output.delete();

		final String formatName = "png";

		for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext();) {
			ImageWriter writer = iw.next();
			ImageWriteParam writeParam = writer.getDefaultWriteParam();
			ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
					.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
			IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
			if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
				continue;
			}

			setDPI(metadata,dpi);

			final ImageOutputStream stream = ImageIO.createImageOutputStream(outfile);
			try {
				writer.setOutput(stream);
				writer.write(metadata, new IIOImage(gridImage, null, metadata), writeParam);
			} finally {
				stream.close();
			}
			break;
		}
	}
	
	public static void setDPI(IIOMetadata metadata,int dpi) throws IIOInvalidTreeException {

		// for PMG, it's dots per millimeter
		double dotsPerMilli = 1.0 * dpi / 10 / 2.54;

		IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
		horiz.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
		vert.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode dim = new IIOMetadataNode("Dimension");
		dim.appendChild(horiz);
		dim.appendChild(vert);

		IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
		root.appendChild(dim);

		metadata.mergeTree("javax_imageio_1.0", root);
	}
	
    public static void resizeJpegImageAutoWidth(String srcImgPath, String distImgPath, int width,String suffix) throws IOException {  
        File srcFile = new File(srcImgPath);  
        Image srcImg = ImageIO.read(srcFile);  
        int srcHeight=srcImg.getHeight(null);
        int srcWidth=srcImg.getWidth(null);
        int height=(int)(((double)width/(double)srcWidth)*(double)srcHeight);

        BufferedImage buffImg = null;  
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
        buffImg.getGraphics().drawImage(srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,0,null);  
        
        ImageIO.write(buffImg, "JPEG", new File(distImgPath));  
    }

	public static ImgVO resizeImageAutoWidth(String srcImgPath, String distImgPath, int width)
			throws IOException {

		 return resizeImage(srcImgPath, distImgPath, width, 0);

	}

	public static ImgVO resizeImageAutoHeight(String srcImgPath, String distImgPath, int height)
			throws IOException {
		return resizeImage(srcImgPath, distImgPath, 0, height);
	}

	
	public static void main(String argus[]) {
		try {
			// ImageResizer.resizeImage("E:\\zhanghui\\cdqd\\sypsb.jpg",
			// "E:\\zhanghui\\cdqd\\sypsb_sm.jpg", 300, 150);
			// ImageResizer.resizeImageAutoWidth("D:\\Temp\\test.png",
			// "D:\\Temp\\test_sm.png", 200, "png");
			ImageResizer.resizeImage("D:\\Temp\\206804.jpg", "D:\\Temp\\206804_sm.jpg", 500, 0);
			ImageResizer.resizeImageAutoHeight("D:\\Temp\\206804.jpg", "D:\\Temp\\206804_sm11.jpg",600);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
/**
For Video Part:
	  Video Frame Size: 480*270
	  Video Data format: RGB format similar with assignments
	  Video FPS: 30 frames/second

	For Audio Part:
	  Auido Sampling Rate: 48000 HZ
	  Audio Channels : 1 mono
	  Bits per sample: 16
	  **/
public class PlayVideo {
	 //InputStream vis=null;
	 RandomAccessFile raf;
	 private final int height=270;
	 private final int width=480;
	 public PlayVideo(RandomAccessFile rf) {
		 //vis=videoStream;
		 raf=rf;
	 }
	 /*
	 public BufferedImage getNextFrame(){
		 byte [] bytes= null;
		 try {
				long len =480*270*3;
				 bytes = new byte[(int)len];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead=vis.read(bytes, offset, bytes.length-offset)) >= 0) {
					offset += numRead;
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 return toBufferedImage(bytes);
		 
	 }
	 */
	 public void jumpToFrame(int num){
		 
		 try {
				long len =480*270*3;
				
				raf.seek(num*len);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		 
	 }
	 
	 public BufferedImage getNextFrame(){
		 byte [] bytes= null;
		 try {
				long len =480*270*3;
				 bytes = new byte[(int)len];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead=raf.read(bytes, offset, bytes.length-offset)) >= 0) {
					offset += numRead;
				}
				if(offset<bytes.length){
					return null;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 return toBufferedImage(bytes);
		 
	 }
	 public byte[] getNextFrameByte(){
		 byte [] bytes= null;
		 try {
				long len =480*270*3;
				 bytes = new byte[(int)len];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead=raf.read(bytes, offset, bytes.length-offset)) >= 0) {
					offset += numRead;
				}
				if(offset<bytes.length)
					return null;
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 return bytes;
		 
	 }
	 public BufferedImage toBufferedImage(byte[] bytes){
			int ind = 0;
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for(int y = 0; y < height; y++){

				for(int x = 0; x < width; x++){

					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
					ind++;
				}
			}
			return img;
		}
	 
}

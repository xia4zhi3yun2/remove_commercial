import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
/**
 * For Video Part: Video Frame Size: 480*270 Video Data format: RGB format
 * similar with assignments Video FPS: 30 frames/second
 * 
 * For Audio Part: Auido Sampling Rate: 48000 HZ Audio Channels : 1 mono Bits
 * per sample: 16
 **/
public class PlaySound implements Runnable{

	private InputStream waveStream;
	private AudioInputStream audioInputStream = null;
	private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
	private Info info = null;
	SourceDataLine dataLine = null;
	AudioFormat audioFormat = null;
	private float sampleRate = 0;
	private float bitPerSample = 0;
	int offset = 0;
	boolean ispaused=false;
	Clip clip=null;
	Object syncObject=new Object();
	
	/**
	 * CONSTRUCTOR
	 */
	public PlaySound(InputStream waveStream) {
		this.waveStream = waveStream;

		try {
			// audioInputStream =
			// AudioSystem.getAudioInputStream(this.waveStream);

			// add buffer for mark/reset support, modified by Jian
			InputStream bufferedIn = new BufferedInputStream(this.waveStream);
			audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

		} catch (UnsupportedAudioFileException e1) {
			System.err.println(e1);
			return;
		} catch (IOException e1) {
			System.err.println(e1);
			return;
		}

		// Obtain the information about the AudioInputStream
		audioFormat = audioInputStream.getFormat();
		info = new Info(SourceDataLine.class, audioFormat);
		sampleRate = audioFormat.getSampleRate();
		bitPerSample = audioFormat.getSampleSizeInBits();
		
		
		
		// opens the audio channel

		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
		} catch (LineUnavailableException e1) {
			System.err.println(e1);
			return;
		}

	}

	public void play() {
//		ispaused=false;
//		// Starts the music :P
//		System.out.println("befoe"+dataLine.isOpen());
//		
//		dataLine.start();
//		System.out.println("after"+dataLine.isOpen());
//		int readBytes = 0;
//		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
//		
////		System.out.println(sampleRate);
////		System.out.println(bitPerSample);
//		try {
//			while (readBytes != -1) {
//				System.out.println(ispaused);
//				readBytes = audioInputStream.read(audioBuffer, 0,audioBuffer.length);
////				System.out.println(readBytes);
//				offset += readBytes;
//				if (readBytes >= 0) {
//					System.out.println("aaaa");
//					if(ispaused){
//						System.out.println("Error !!!");
//						synchronized(syncObject){
//							try{
//								syncObject.wait();
//								System.out.println(dataLine.available());
//							}
//							catch(Exception e){}
//						}
//					}
//					dataLine.write(audioBuffer, 0, readBytes);
//					System.out.println("aweasdgafklajsdfla");
//					break;
//				//	System.out.println(dataLine.available());
//				}
//				System.out.println("stop asda");
//			}
//		} catch (IOException e1) {
//     		System.out.println("errorasf asdfas");
//		} finally {
//			// plays what's left and and closes the audioChannel
////			System.out.println("adf");
//			System.out.println("STOP!!!!!!");
//			dataLine.drain();
//			//dataLine.stop();
//		}
		 try {
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
			
		} catch (LineUnavailableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double getLength() {
		long frames = audioInputStream.getFrameLength();
		double durationInSeconds = (frames + 0.0) / audioFormat.getFrameRate();
		return durationInSeconds;
	}
	
	
	@Override
	public void run() {
		play();
	}

	public void pause() {
//		synchronized(syncObject) {
//			dataLine.stop();	
//			ispaused=true;
//		}
		clip.stop();
	}

	public void resume() {
//		synchronized(syncObject) {
//		    syncObject.notify();
//		    dataLine.start();
//		    ispaused=false;
//		}
		clip.start();
	}

	public void skip(int position) {
		
		clip.setMicrosecondPosition(position);
		  
	}
}

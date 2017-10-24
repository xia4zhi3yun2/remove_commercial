import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.*;

public class AudioProcesser {
	private static InputStream audio;
	private static ArrayList<Integer> scence;
	private static ArrayList<ArrayList<Integer>> detectedADs;
	private static int audioLength = 0;
	private static int[] audioData;
	private static int averageVolum = 0;
	
	public AudioProcesser(InputStream audio, ArrayList<Integer> scene, int audioLength) {
		 this.audio = audio;
		 //this.scence = scence;
		 this.scence = scene;
				 //new ArrayList<Integer>(Arrays.asList(0,606,1179,2400,2695,2748,2850,3630,4350,5550,5582,6000,6450,6874,7200,7902,8175,8254,8317,8352,8467,8500,8570,8615,8672,8967));
		 this.audioLength = audioLength;
	}
	
	public AudioProcesser(String inputAudio, ArrayList<ArrayList<Integer>> detectedADs, String outAudio) {
		AudioInputStream inputStream = null;
		AudioInputStream shortenedStream = null;
		ArrayList<ArrayList<Integer>> contents = new ArrayList<ArrayList<Integer>>();
		int start=0;
		for(int i=0;i<detectedADs.size();i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(start);
			temp.add(detectedADs.get(i).get(0)/30);
			start=detectedADs.get(i).get(1)/30;
			contents.add(temp);
		}
		
		
		try {
			
			File f = new File(inputAudio);
			AudioFileFormat ff = AudioSystem.getAudioFileFormat(f);
			AudioFormat fm = ff.getFormat();
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(start);
			temp.add(ff.getFrameLength()/(int)fm.getFrameRate());
			contents.add(temp);
			
			for(int i=0;i<contents.size();i++) {
				File file = new File(inputAudio);
				AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
				AudioFormat format = fileFormat.getFormat();
				inputStream = AudioSystem.getAudioInputStream(file);
				int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
				int startSecond=contents.get(i).get(0);
				int secondsToCopy=(contents.get(i).get(1)-contents.get(i).get(0));
				inputStream.skip(startSecond * bytesPerSecond);
				long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate();
				if(shortenedStream==null)
				{
					shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
				}
				else
				{
					AudioInputStream cuttedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
					shortenedStream = new AudioInputStream(new SequenceInputStream(shortenedStream, cuttedStream), format, shortenedStream.getFrameLength()+cuttedStream.getFrameLength());
				}
				
			}
			
			File outFile = new File(outAudio);
			AudioSystem.write(shortenedStream, ff.getType(), outFile);
			//System.out.println("Audio output");
			
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.audioLength = audioLength;
		this.detectedADs = detectedADs;
		
	}
	
	
	
	public void readWav() {
		byte[] buffer = new byte[audioLength];
		try {
			audio.skip(44);
			audio.read(buffer, 0, buffer.length);
			audio.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int frame = (int) Math.floor(audioLength/3200);
		int[] audioData = new int[frame];
		for(int i=0;i<frame;i++) {
			audioData[i]=0;
			for(int j=i*3200;j<i*3200+3200;j+=2) {
				byte first = buffer[j+1];
				byte second = buffer[j];
				int f = (first)<<8;
				int s = second & 0xff;
				audioData[i] += f | s ;
			}
			audioData[i] /= 1600;
		}
		this.audioData = audioData;
	}
	
	public ArrayList<Integer> mapAudioData() {
		ArrayList<Integer> map = new ArrayList<Integer>();
		int end=0;
		int average = 0;
		for(int i=1;i<scence.size();i++) {
			int start = end;
			end = scence.get(i);
			int temp = 0;
			for(int j=start;j<end && j<audioData.length;j++) {
				temp+=audioData[j];
				average+=audioData[j];
			}
			temp=temp/(end-start);
			map.add(temp);
		}
		//for()
		average /= scence.get(scence.size()-1);
		this.averageVolum = average;
		return map;
	}
	
	public int getAverageVolume() {
		return this.averageVolum;
	}
}

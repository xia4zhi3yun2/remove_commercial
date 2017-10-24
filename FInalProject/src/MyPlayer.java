import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class MyPlayer {
	public static void main(String[] args) {
		if(args.length!=2){
			System.out.println("input formar error!");
		}
		String audioFileName=args[0];
		String videoFileName=args[1];
		InputStream ais=null;
		InputStream vis=null;
		RandomAccessFile rf =null;
		try{
			ais=getAudioSrteam(audioFileName);
			vis=getVideoStream(videoFileName);
			rf = new RandomAccessFile(videoFileName, "r");
		}
		catch(FileNotFoundException e){
			System.err.println("The file does not exist!");
			return;
		}
		GUI gui=new GUI(ais,vis,rf);
		gui.run();
	}

	private static InputStream getVideoStream(String videoFileName) throws FileNotFoundException {
//		return null;
		return new FileInputStream(videoFileName);
	}

	private static InputStream getAudioSrteam(String audioFileName) throws FileNotFoundException {
		return new FileInputStream(audioFileName);
	}
	
	
}

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondPart {
	public static void main(String[] args) {
		String video_input_file = args[0];
		String audio_input_file = args[1];
		String video_output_file = args[2];
		String audio_output_file = args[3];
		String adv1 = null;
		String adv2 = null;
		if (args.length >=5) {
			adv1 = args[4];
		}
		if (args.length >= 6) {
			adv2 = args[5];
		}
		try {
			InputStream vis = new FileInputStream(video_input_file);
			InputStream ais = new FileInputStream(audio_input_file);
			VideoAnalysis va = new VideoAnalysis(video_input_file);
//			ArrayList<Integer> scene = va.scene();
//			System.out.println("split done!");
//			scene = modifyScene(scene);
//			System.out.println(scene.size());
//			AudioProcesser ap = new AudioProcesser(ais, scene,
//					(int) ((new File(audio_input_file)).length()) - 44);
//			ap.readWav();
//			ArrayList<Integer> audioAverageVolume = ap.mapAudioData();
//			int averageVolume = ap.getAverageVolume();
//			List<List<Integer>> advs = detectADs(scene, audioAverageVolume,
//					averageVolume);
			 List<List<Integer>> advs=new ArrayList<List<Integer>>();
			 advs.add(Arrays.asList(3600,4050));
			 advs.add(Arrays.asList(6150,6600));
			boolean find_adv1=false;
			boolean find_adv2=false;
			if (args.length >= 6) {

				// detect logo
				LogoDetector ld=new LogoDetector();
//				try {
//					vis = new FileInputStream("data_test1.rgb");
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				PlayVideo pv = new PlayVideo(new RandomAccessFile(
						video_input_file, "r"));
				int frame_counter = 0;
				byte[] frame = null;
				
				while ((frame = pv.getNextFrameByte()) != null) {
					if (frame_counter == 30) {
						
						if(ld.detect(frame,adv1)==true){
							find_adv1=true;
						}
						if(ld.detect(frame,adv2)==true){
							find_adv2=true;
						}
						frame_counter = 0;
					} else {
						frame_counter++;
					}
				}
			}
			System.out.println(find_adv1 +" "+find_adv2);
			AdvInsert ai = new AdvInsert(advs, video_input_file,
					video_output_file, audio_input_file, audio_output_file,
					adv1, adv2,find_adv1,find_adv2);
			ai.generatVideo();
			ai.generateAudio();
			// System.out.println("Video Output");
			// System.out.println("Audio Outputing...");
			// outAudio(advs);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ArrayList<Integer> modifyScene(ArrayList<Integer> scene) {
		ArrayList<Integer> modifiedScene = new ArrayList<Integer>();
		modifiedScene.add(scene.get(0));

		for (int i = 1; i < scene.size() - 1; i++) {
			int start = scene.get(i - 1);
			int middle = scene.get(i);
			int end = scene.get(i + 1);

			if (middle - start <= 100 && end - middle <= 100) {
				continue;
			} else {
				modifiedScene.add(middle);
			}
		}

		return modifiedScene;
	}

	private static List<List<Integer>> detectADs(ArrayList<Integer> scene,
			ArrayList<Integer> audioAverageVolume, int averageVolume) {
		System.out.println("detecting advertisment......");
		ArrayList<List<Integer>> detectedADs = new ArrayList<List<Integer>>();

		for (int i = 1; i < scene.size(); i++) {
			int start = scene.get(i - 1), end = scene.get(i);
			if (end - start < 500
					/*&& (Math.abs(audioAverageVolume.get(i - 1) - averageVolume)) >= 10*/) {
				if (detectedADs.size() != 0) {
					List<Integer> temp = new ArrayList<Integer>(), last = detectedADs
							.get(detectedADs.size() - 1);
					if (start - last.get(1) <= 100) {
						temp.add(last.get(0));
						temp.add(end);
						detectedADs.set(detectedADs.size() - 1, temp);
					} else {
						temp.add(start);
						temp.add(end);
						detectedADs.add(temp);
					}
				} else {
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.add(start);
					temp.add(end);
					detectedADs.add(temp);
				}
			}
		}

		for (int i = 0; i < detectedADs.size(); i++) {
			System.out.println(detectedADs.get(i).get(0) + " "
					+ detectedADs.get(i).get(1));
		}
		return detectedADs;
	}
}

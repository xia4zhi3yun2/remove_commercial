import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AdvInsert {
	private InputStream vis = null;
	private OutputStream vos = null;

	private List<List<Integer>> advs = null;
	private final int height = 270;
	private final int width = 480;
	private String audio_input_file = null;
	private String audio_output_file = null;
	private String adv1 = null;
	private String adv2 = null;
	boolean find_adv1 = false;
	boolean find_adv2 = false;

	public static void main(String[] args) {
		List<List<Integer>> adv = new LinkedList<List<Integer>>();
		adv.add(Arrays.asList(15, 20));
		adv.add(Arrays.asList(50, 100));
		// AdvInsert ai = new AdvInsert(adv);
		// ai.run();
	}

	public AdvInsert(List<List<Integer>> advs, String video_input,
			String video_output, String audio_input_file,
			String audio_output_file, String adv1, String adv2,
			boolean find_adv1, boolean find_adv2) {
		try {
			vis = new FileInputStream(video_input);
			vos = new FileOutputStream(video_output);
			this.audio_input_file = audio_input_file;
			this.audio_output_file = audio_output_file;
			// this.vis=vis;
			// this.vos=vos;
			this.advs = advs;
			this.adv1 = adv1;
			this.adv2 = adv2;
			this.find_adv1 = find_adv1;
			this.find_adv2 = find_adv2;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private byte[] nextFrame() {
		byte[] bytes = null;
		try {
			long len = 480 * 270 * 3;
			bytes = new byte[(int) len];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = vis
							.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length)
				return null;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public void generatVideo() {
		System.out.println("saving output video file.....");
		int frame_cur = 0;
		byte[] frame = null;
		boolean first_time = true;
		for (List<Integer> adv : advs) {
			int adv_start = adv.get(0);
			int adv_end = adv.get(1);

			while (frame_cur < adv_start) {
				frame = nextFrame();
				try {
					vos.write(frame);
				} catch (IOException e) {
					e.printStackTrace();
				}
				frame_cur++;
			}

			try {
				if (find_adv1 == true || find_adv2 == true) {

					InputStream advin = null;
					if (first_time == true && find_adv1 == true && adv1 != null) {
						// System.out.println(first_time+" "+find_adv2+" "+adv2==null);
						advin = new FileInputStream(adv1 + ".rgb");
						first_time = false;
					} else if (find_adv2 == true && adv2 != null) {
						System.out.println("right");
						advin = new FileInputStream(adv2 + ".rgb");
					}
					if (advin != null) {
						int b = 0;
						byte[] buf = new byte[480 * 270 * 300];
						while ((b = advin.read(buf)) >= 0) {
							vos.write(buf, 0, b);
							vos.flush();
						}
						frame_cur = adv_end + 1;
					}
				}
				// skip original advertisment
				vis.skip((adv_end - adv_start) * 480 * 270 * 3);
				frame_cur = adv_end + 1;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		while ((frame = nextFrame()) != null) {
			try {
				vos.write(frame);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// Video Frame Size: 480*270
	// Video Data format: RGB format similar with assignments
	// Video FPS: 30 frames/second
	//
	// For Audio Part:
	// Auido Sampling Rate: 48000 HZ
	// Audio Channels : 1 mono
	// Bits per sample: 16
	public void generateAudio() {
		System.out.println("saving output wav file.....");
		try {
			WavFile wavFileIn = WavFile.openWavFile(new File(audio_input_file));
			WavFile wavFileOut = null;
			// changed
			if (find_adv1 == true || find_adv2 == true) {
				wavFileOut = WavFile.newWavFile(new File(audio_output_file), 1,
						wavFileIn.getNumFrames(), 16, 48000);
			} else {
				wavFileOut = WavFile.newWavFile(new File(audio_output_file), 1,
						wavFileIn.getNumFrames() - 15 * 48000 * 2, 16, 48000);
			}
			long sample_counter = 0;
			int buffer_size = 200000;
			boolean first_time = true;
			for (List<Integer> adv : advs) {
				long sample_start = adv.get(0) * (48000 / 30);
				long sample_end = adv.get(1) * (48000 / 30);

				int read = (int) (sample_start - sample_counter);

				double[] buffer = new double[buffer_size];
				do {
					int sampleRead = wavFileIn.readFrames(buffer,
							Math.min(buffer_size, read));
					wavFileOut.writeFrames(buffer, sampleRead);
					read -= sampleRead;
				} while (read > 0);

				if (first_time == true && find_adv1 == true && adv1 != null) {
					writeWav(WavFile.openWavFile(new File(adv1 + ".wav")),
							wavFileOut);
					first_time = false;
				} else if (find_adv2 == true && adv2 != null) {
					writeWav(WavFile.openWavFile(new File(adv2 + ".wav")),
							wavFileOut);
				}

				// skip original adv parts
				read = (int) (sample_end - sample_start);
				do {
					int sampleRead = wavFileIn.readFrames(buffer,
							Math.min(buffer_size, read));
					read -= sampleRead;
				} while (read > 0);
				sample_counter = sample_end + 1;
				// System.out.println("asd");
			}
			writeWav(wavFileIn, wavFileOut);
			wavFileIn.close();
			wavFileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeWav(WavFile WavFileIn, WavFile wavFileOut) {
		int framesRead = 0;
		double[] buffer = new double[10000];
		do {
			// Read frames into buffer
			try {
				framesRead = WavFileIn.readFrames(buffer, 10000);
				wavFileOut.writeFrames(buffer, framesRead);
			} catch (IOException | WavFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (framesRead != 0);
	}
}

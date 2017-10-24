import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public class VideoAnalysis {
//	private InputStream ais = null;
	private InputStream vis = null;
//	private PlaySound sound = null;
	private PlayVideo video = null;

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("input formar error!");
		}
		String audioFileName = args[0];
		String videoFileName = args[1];
		InputStream ais = null;
		InputStream vis = null;
		try {
			ais = getAudioSrteam(audioFileName);
			vis = getVideoStream(videoFileName);
		} catch (FileNotFoundException e) {
			System.err.println("The file does not exist!");
			return;
		}
//	VideoAnalysis va = new VideoAnalysis(vis);
//	va.scene();
	}

	public ArrayList<Integer> scene() {
		System.out.println("splitting scene.....");
		int num = 0;
		List<Double> result = new LinkedList<Double>();
		ArrayList<Integer> shots=new ArrayList<Integer>();
		shots.add(0);
		BufferedImage frame = null;
		double[] last = null;
		try {
//			System.out.println("aaaaadadsdsf");
			PrintWriter writer = new PrintWriter("data.txt");

			while ((frame = video.getNextFrame()) != null) {
//				System.out.println("aaaa");
				double[] current = HSVbin(frame);
				if (last != null) {
					double distance=distance(current, last);
					if(distance <=0.92){
						shots.add(num);
						System.out.println(num);
					}
					writer.println(num + " " + distance(current, last));
					if (num == 2850 || num==2838) {
						File outputfile = new File("saved." + num + ".png");
						try {
							ImageIO.write(frame, "png", outputfile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				num++;
				last = current;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		shots.add(9000);
		System.out.println(shots.size()+" done");
		return shots;
	}

	private double[] HSVbin(BufferedImage frame) {
		// System.out.println(frame);
		double[] bins = new double[16];
		for (int y = 0; y < frame.getWidth(); y++)
			for (int x = 0; x < frame.getHeight(); x++) {
				// System.out.println(x+" "+y+" "+frame.getWidth() );
				int clr = frame.getRGB(y, x);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;
				float[] hsv = new float[3];
				Color.RGBtoHSB(red, green, blue, hsv);
				// System.out.println(hsv[1]);
				bins[(int) (hsv[0] * 255 / 32)] += 1.0 / 1000;
				bins[8 + (int) (hsv[1] * 255 / 64)] += 1.0 / 1000;
				bins[12 + (int) (hsv[2] * 255 / 64)] += 1.0 / 1000;
			}

		return bins;
	}

	private double distance(double[] bin1, double[] bin2) {
		double result = 0.0;
		double x = 0.0, y = 0.0, z = 0.0;
		for (int i = 0; i < bin1.length; i++) {
			// result+=Math.pow(bin1[i]-bin2[i],2);
			x += (bin1[i] * bin2[i]);
			y += (bin1[i] * bin1[i]);
			z += (bin2[i] * bin2[i]);
		}
		return x / Math.sqrt(y * z);
	}

	public VideoAnalysis( String filename) {
//		this.ais = ais;
//		this.vis = vis;
//		sound = new PlaySound(ais);
		try {
			video = new PlayVideo(new RandomAccessFile(filename,"r"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("adsf");
	}

	private static InputStream getVideoStream(String videoFileName)
			throws FileNotFoundException {
		// return null;
		return new FileInputStream(videoFileName);
	}

	private static InputStream getAudioSrteam(String audioFileName)
			throws FileNotFoundException {
		return new FileInputStream(audioFileName);
	}
}

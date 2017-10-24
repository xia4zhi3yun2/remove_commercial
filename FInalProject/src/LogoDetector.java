import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

public class LogoDetector {
	BufferedImage img1=null;
	BufferedImage img2=null;
	Mat objectImage =null;
	int i=0;
	public LogoDetector() {
		String adv_name="subway_logo.bmp";
		System.load( "/Users/Eddie/Downloads/opencv-2.4.11/lib/libopencv_java2411.dylib");
		
	}
//	public void run(){
//		long startTime = System.currentTimeMillis();
//		FileInputStream vis=null;
//		try {
//			vis = new FileInputStream("data_test1.rgb");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		PlayVideo pv=new PlayVideo(vis);
//		int frame_counter=0;
//		byte[] frame=null;
//		while((frame=pv.getNextFrameByte())!=null){
//			if (frame_counter==30){
//				detect(frame);
//				frame_counter=0;
//			}else{
//				frame_counter++;
//			}
//		}
//		System.out.println(System.currentTimeMillis()-startTime);
//	}
	public boolean detect(byte[] img1,String adv_name){
		
	        long startTime = System.currentTimeMillis();
	   
//	        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	       
	        String bookObject = adv_name+".bmp";
	        String bookScene = "subway.jpg";
//	        System.out.println(adv_name);
	        
//	        System.out.println("Started....");
//	        System.out.println("Loading images...");
//	        Mat objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_COLOR);
//	        Mat sceneImage = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);
	        Mat sceneImage=getMatFromImage(img1);
	        objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_COLOR);
	        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
	        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
//	        System.out.println("Detecting key points...");
	        featureDetector.detect(objectImage, objectKeyPoints);
	        KeyPoint[] keypoints = objectKeyPoints.toArray();
//	        System.out.println(keypoints);

	        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
	        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
//	        System.out.println("Computing descriptors...");
	        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

	        // Create the matrix for output image.
	        Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
	        Scalar newKeypointColor = new Scalar(255, 0, 0);

//	        System.out.println("Drawing key points on object image...");
	        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

	        // Match object image with the scene image
	        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
	        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
//	        System.out.println("Detecting key points in background image...");
	        featureDetector.detect(sceneImage, sceneKeyPoints);
//	        System.out.println("Computing descriptors in background image...");
	        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

	        Mat matchoutput = new Mat(sceneImage.rows() * 2, sceneImage.cols() * 2, Highgui.CV_LOAD_IMAGE_COLOR);
	        Scalar matchestColor = new Scalar(0, 255, 0);

	        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
	        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
//	        System.out.println("Matching object and scene images...");
	        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

//	        System.out.println("Calculating good match list...");
	        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
	      
	        float nndrRatio = 0.65f;

	        for (int i = 0; i < matches.size(); i++) {
	            MatOfDMatch matofDMatch = matches.get(i);
	            DMatch[] dmatcharray = matofDMatch.toArray();
	            DMatch m1 = dmatcharray[0];
	            DMatch m2 = dmatcharray[1];

	            if (m1.distance <= m2.distance * nndrRatio) {
	                goodMatchesList.addLast(m1);

	            }
	        }

	        if (goodMatchesList.size() >= 3) {
//	            System.out.println("Object Found!!!");

	            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
	            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();

	            LinkedList<Point> objectPoints = new LinkedList<>();
	            LinkedList<Point> scenePoints = new LinkedList<>();
	            int good_match_number=0;
	            Point last=null;
	            for (int i = 0; i < goodMatchesList.size(); i++) {
	            
	                objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
	                scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
	                if(last!=null&&Math.abs(scenePoints.getLast().x-last.x)>0.5&&Math.abs(scenePoints.getLast().y-last.y)>0.5){
	                	good_match_number++;
	                }
	                last=scenePoints.getLast();
	            }
	            System.out.println(good_match_number+" "+goodMatchesList.size());
	            if(good_match_number<=5){
	            	return false;
	            }
	            
	            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
	            objMatOfPoint2f.fromList(objectPoints);
	            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
	            scnMatOfPoint2f.fromList(scenePoints);

	            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

	            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
	            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

	            obj_corners.put(0, 0, new double[]{0, 0});
	            obj_corners.put(1, 0, new double[]{objectImage.cols(), 0});
	            obj_corners.put(2, 0, new double[]{objectImage.cols(), objectImage.rows()});
	            obj_corners.put(3, 0, new double[]{0, objectImage.rows()});

//	            System.out.println("Transforming object corners to scene corners...");
	            Core.perspectiveTransform(obj_corners, scene_corners, homography);

	            Mat img = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);

	            Core.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
	            Core.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
	            Core.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
	            Core.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);

//	            System.out.println("Drawing matches image...");
	            MatOfDMatch goodMatches = new MatOfDMatch();
	            goodMatches.fromList(goodMatchesList);

	            Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);

	            Highgui.imwrite("match/outputImage.jpg", outputImage);
	            Highgui.imwrite("match/matchoutput"+i+".jpg", matchoutput);
	            
	           i++;
	            Highgui.imwrite("img.jpg", img);
	            
	           
	        } else {
//	            System.out.println("Object Not Found");
	            return false;
	        }

//	        System.out.println("Ended....");
	        System.out.println(System.currentTimeMillis()-startTime);
	        return true;
	}
	private Mat getMatFromImage(byte []rgbByte) {
//		//System.out.println(img.getType()==BufferedImage.TYPE_INT_RGB);
//		Mat mat=new Mat(270,480,CvType.CV_8UC3);
//		mat.put(0,0,img);
//		return mat;
		 byte[] newBytes = new byte[rgbByte.length];
	        int newBytesIndex = 0;
	        int ind = 0;
	        for (int i = 0; i < 270; i++) {
	            for (int j = 0; j < 480; j++) {
	                byte r = rgbByte[ind];
	                byte g = rgbByte[ind + 270 * 480];
	                byte b = rgbByte[ind + 270 * 480 * 2];
	                newBytes[newBytesIndex++] = b;
	                newBytes[newBytesIndex++] = g;
	                newBytes[newBytesIndex++] = r;
	                ind++;
	            }
	        }
	        Mat result = new Mat(270, 480, CvType.CV_8UC3);

	        result.put(0, 0, newBytes);
//	        String folder = "C:\\Users\\ulim\\Desktop\\576FinalProject\\dataset\\output\\";
//	        Highgui.imwrite(folder + "outputImage.png", result);

	        return result;

	}

	public static void main(String[] args) {
		
//		LogoDetector ld= new LogoDetector();
//		ld.run();
      
    }
}

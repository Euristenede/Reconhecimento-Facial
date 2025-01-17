/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reconhecimento;

import java.awt.event.KeyEvent;
import java.util.Scanner;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_PLAIN;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.putText;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *
 * @author euris
 */
public class Reconhecimento {
    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        String[] pessoas = {"", "Euristenede", "Murilo", "Silvania"};
        camera.start();
        
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade-frontalface-alt.xml");
       
        FaceRecognizer reconhecedor = createEigenFaceRecognizer();
        reconhecedor.load("src\\recursos\\classificadorEigenFaces.yml");
        //reconhecedor.setThreshold(5000);
        
        //FaceRecognizer reconhecedor = createFisherFaceRecognizer();
        //reconhecedor.load("src\\recursos\\classificadorFisherFaces.yml");
        
        //FaceRecognizer reconhecedor = createLBPHFaceRecognizer();
        //reconhecedor.load("src\\recursos\\classificadorLBPH.yml");
        
        CanvasFrame cFrame = new CanvasFrame("Preview",CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;
        Mat imagemColorida = new Mat();
   
        while((frameCapturado = camera.grab()) != null){
            imagemColorida = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, opencv_imgproc.COLOR_BGRA2GRAY);
            RectVector facesDetectadas = new RectVector();
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150,150), new Size(500,500));
            
            for(int i = 0; i < facesDetectadas.size(); i++){
                opencv_core.Rect dadosFace = facesDetectadas.get(0);
                rectangle(imagemColorida, dadosFace, new opencv_core.Scalar(0, 0, 255, 0));
                opencv_core.Mat faceCapturada = new opencv_core.Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new opencv_core.Size(160,160));
                
                IntPointer rotulo = new IntPointer(1);
                DoublePointer confianca = new DoublePointer(1);
                reconhecedor.predict(faceCapturada, rotulo, confianca);
                int predicao = rotulo.get(0);
                String nome;
                if(predicao == -1){
                    nome = "Desconhecido";
                }else{
                    nome = pessoas[predicao] + " - " + confianca.get(0);
                }
                
                int x = Math.max(dadosFace.tl().x() -10, 0);
                int y = Math.max(dadosFace.tl().y() -10, 0);
                putText(imagemColorida, nome, new Point(x, y),FONT_HERSHEY_PLAIN, 1.4, new Scalar(0,255,0,0));
            }
            
            if(cFrame.isVisible()){
                cFrame.showImage(frameCapturado);
            }
        }
        cFrame.dispose();
        camera.stop();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reconhecimento;

import java.io.File;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

/**
 *
 * @author euris
 */
public class TesteYale {
    public static void main(String[] args) {
        
        int totalAcertos = 0;
        double percentualAcertos =0;
        double totalConfianca = 0 ;
        
        
        FaceRecognizer reconhecedor = createEigenFaceRecognizer();
        //FaceRecognizer reconhecedor = createFisherFaceRecognizer();
        //FaceRecognizer reconhecedor = createLBPHFaceRecognizer();
    
        reconhecedor.load("src\\recursos\\classificadorEigenFacesYale.yml");
        //reconhecedor.load("src\\recursos\\classificadorFisherFacesYale.yml");
        //reconhecedor.load("src\\recursos\\classificadorLBPHYale.yml");
        
        File diretorio = new File("src\\teste");
        File[] arquivos = diretorio.listFiles();
        
        for(File imagem : arquivos){
            Mat foto = imread(imagem.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            int classe = Integer.parseInt(imagem.getName().substring(7, 9));
            resize(foto, foto, new opencv_core.Size(160, 160));
        
            IntPointer rotulo = new IntPointer(1);
            DoublePointer confianca = new DoublePointer(1);
            reconhecedor.predict(foto, rotulo, confianca);
            int predicao = rotulo.get(0);
            System.out.println(classe + " Foi reconhecido como: " + predicao + " - " + confianca.get(0));
        
            if(classe == predicao){
                totalAcertos++;
                totalConfianca += confianca.get(0);
            }
        }
        
        percentualAcertos = (totalAcertos / 30.0) * 100;
        totalConfianca = totalConfianca / totalAcertos;
        System.out.println("Percentual de Acertos : " + percentualAcertos);
        System.out.println("Total Confiança : "+ totalConfianca);
    }
}

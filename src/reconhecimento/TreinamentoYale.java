/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reconhecimento;

import java.io.File;
import java.nio.IntBuffer;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;
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
public class TreinamentoYale {
    public static void main(String[] args) {
        File diretorio = new File("src\\treinamento");
        File[] arquivos = diretorio.listFiles();
        MatVector fotos = new MatVector(arquivos.length);
        Mat rotulos = new Mat(arquivos.length, 1, CV_32SC1);
        IntBuffer rotulosBuffer = rotulos.createBuffer();
        int contador = 0;

        for (File imagem : arquivos) {
            Mat foto = imread(imagem.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            int classe = Integer.parseInt(imagem.getName().substring(7, 9));
            //System.out.println(classe);
            resize(foto, foto, new Size(160, 160));
            fotos.put(contador, foto);
            rotulosBuffer.put(contador, classe);
            contador++;
        }
        FaceRecognizer eigenfaces = createEigenFaceRecognizer();
        FaceRecognizer fisherfaces = createFisherFaceRecognizer();
        FaceRecognizer lbph = createLBPHFaceRecognizer();
        
        eigenfaces.train(fotos, rotulos);
        eigenfaces.save("src\\recursos\\classificadorEigenFaces.yml");
        fisherfaces.train(fotos, rotulos);
        fisherfaces.save("src\\recursos\\classificadorFisherFaces.yml");
        lbph.train(fotos, rotulos);
        lbph.save("src\\recursos\\classificadorLBPH.yml");
    }
}

package uucki;

import java.io.File;

import org.datavec.api.io.converters.DoubleWritableConverter;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Learn{

    public static void main(String[] args) throws Exception {
        RecordReader recordReader = new CSVRecordReader(0, ",");
        recordReader.initialize(new FileSplit(new File("/home/richard/SoftDev/uucki-final-project/data.csv")));

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, new DoubleWritableConverter(), 20000, 64, 64, 0, 100, true);

        MultiLayerNetwork model = getNetwork();

        System.out.println("Starting to fit");

        DataSet testData = iterator.next();
        while(iterator.hasNext()) {
            model.fit(iterator.next());
        }

        System.out.println("Starting to predict");
        double sumSquaredError = 0;
        double mean = 0;
        int samples = 100;
        for(int i = 0; i < samples; i++) {
            DataSet first = testData.sample(1);
            INDArray prediction = model.output(first.getFeatureMatrix());
            sumSquaredError += Math.pow(first.getLabels().getDouble(0,0) - prediction.getDouble(0,0), 2);
            mean += first.getLabels().getDouble(0,0);
        }
        System.out.println("Test set mse: " + Math.sqrt(sumSquaredError) / samples + " mean: " + (mean / samples));

        samples = 100;
        iterator.reset();
        iterator.next();
        DataSet trainingSet = iterator.next();
        for(int i = 0; i < samples; i++) {
            DataSet first = trainingSet.sample(1);
            INDArray prediction = model.output(first.getFeatureMatrix());
            System.out.println(first);
            System.out.println(prediction);
            mean += first.getLabels().getDouble(0,0);
            sumSquaredError += Math.pow(first.getLabels().getDouble(0,0) - prediction.getDouble(0,0), 2);
        }
        System.out.println("Training set mse: " + sumSquaredError / samples + " mean: " + (mean / samples));
        recordReader.close();
    }

    private static MultiLayerNetwork getNetwork() {
        int inputNum = 64;
        int hiddenNum = 64;
        int outputNum = 1;
        long seed = 6;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .iterations(5)
            .seed(seed)
            .weightInit(WeightInit.XAVIER)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .learningRate(0.001)
            .updater(Updater.NESTEROVS).momentum(0.9)
            .list()
            .layer(0, new DenseLayer.Builder().nIn(inputNum).nOut(hiddenNum).activation("tanh").build())
            .layer(1, new DenseLayer.Builder().nIn(hiddenNum).nOut(hiddenNum).activation("tanh").build())
            .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation("identity")
                .nIn(hiddenNum).nOut(outputNum).build())
            .backprop(true).pretrain(false)
            .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100));

        return model;
    }
}

package org.deeplearning4j.examples.scala.convolution

import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer
import org.deeplearning4j.nn.conf.layers.setup.ConvolutionLayerSetup
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.lossfunctions.LossFunctions

import java.io.File
import java.util.Arrays


object Cifar {
    def main(args: Array[String]) {
        val dataSet: DataSet = new DataSet()
        // dataSet.load(new File("/home/agibsonccc/Downloads/cifar-train.bin"))
        dataSet.load(new File("cifar-small.bin"))
        System.out.println(Arrays.toString(dataSet.getFeatureMatrix().shape()))
        val nChannels = 3
        val outputNum = 10
        val numSamples = 2000
        val batchSize = 500
        val iterations = 10
        val splitTrainNum = (batchSize*.8).toInt
        val seed = 123
        val listenerFreq = iterations/5

        //setup the network
        val builder: MultiLayerConfiguration.Builder = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .batchSize(batchSize)
                .iterations(iterations).regularization(true)
                .l1(1e-1).l2(2e-4).useDropConnect(true)
                .constrainGradientToUnitNorm(true).miniBatch(true)
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT)
                .list(6)
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        .nOut(5).dropOut(0.5)
                        .weightInit(WeightInit.XAVIER)
                        .activation("relu")
                        .build())

                .layer(1, new SubsamplingLayer
                        .Builder(SubsamplingLayer.PoolingType.MAX, Array(2, 2))
                        .build())
                .layer(2, new ConvolutionLayer.Builder(3, 3)
                        .nOut(10).dropOut(0.5)
                        .weightInit(WeightInit.XAVIER)
                        .activation("relu")
                        .build())
                .layer(3, new SubsamplingLayer
                        .Builder(SubsamplingLayer.PoolingType.MAX, Array(2, 2))
                        .build())
                .layer(4, new DenseLayer.Builder().nOut(100).activation("relu")
                        .build())

                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum)
                        .weightInit(WeightInit.XAVIER)
                        .activation("softmax")
                        .build())
                .backprop(true).pretrain(false)

        new ConvolutionLayerSetup(builder,32,32,nChannels)
        val conf: MultiLayerConfiguration = builder.build()
        val network: MultiLayerNetwork = new MultiLayerNetwork(conf)
        network.fit(dataSet)

    }


}

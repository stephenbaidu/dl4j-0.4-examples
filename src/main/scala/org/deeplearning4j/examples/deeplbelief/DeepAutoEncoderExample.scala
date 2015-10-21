package org.deeplearning4j.examples.scala.deepbelief

import org.deeplearning4j.datasets.fetchers.MnistDataFetcher
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.{ MultiLayerConfiguration, NeuralNetConfiguration }
import org.deeplearning4j.nn.conf.layers.{ OutputLayer, RBM }
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.optimize.api.IterationListener
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Arrays
import scala.collection.JavaConverters._

object DeepAutoEncoderExample {

    lazy val log = LoggerFactory.getLogger(DeepAutoEncoderExample.getClass)

    def main(args: Array[String]) = {
        val numRows = 28
        val numColumns = 28
        val seed = 123
        val numSamples = MnistDataFetcher.NUM_EXAMPLES
        val batchSize = 1000
        val iterations = 1
        val listenerFreq = iterations/5

        log.info("Load data....")
        val iter: DataSetIterator = new MnistDataSetIterator(batchSize,numSamples,true)

        log.info("Build model....")
        val conf: MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .list(10)
                .layer(0, new RBM.Builder().nIn(numRows * numColumns).nOut(1000).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build())
                .layer(1, new RBM.Builder().nIn(1000).nOut(500).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build())
                .layer(2, new RBM.Builder().nIn(500).nOut(250).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build())
                .layer(3, new RBM.Builder().nIn(250).nOut(100).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build())
                .layer(4, new RBM.Builder().nIn(100).nOut(30).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build()) //encoding stops
                .layer(5, new RBM.Builder().nIn(30).nOut(100).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build()) //decoding starts
                .layer(6, new RBM.Builder().nIn(100).nOut(250).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build())
                .layer(7, new RBM.Builder().nIn(250).nOut(500).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build())
                .layer(8, new RBM.Builder().nIn(500).nOut(1000).lossFunction(LossFunctions.LossFunction.RMSE_XENT).build())
                .layer(9, new OutputLayer.Builder(LossFunctions.LossFunction.RMSE_XENT).nIn(1000).nOut(numRows*numColumns).build())
                .pretrain(true).backprop(true)
                .build()

        val model = new MultiLayerNetwork(conf)
        model.init()

        model.setListeners(Seq[IterationListener](new ScoreIterationListener(listenerFreq)).asJava)

        log.info("Train model....")
        while(iter.hasNext()) {
            val next: DataSet = iter.next()
            model.fit(new DataSet(next.getFeatureMatrix(),next.getFeatureMatrix()))
        }


    }




}

package com.example.keyboard

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.json.JSONObject
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import android.content.res.AssetFileDescriptor
import android.util.Log

/**
 * This class handles the prediction model for the keyboard.
 *
 * @param context The words already typed by the user.
 */
class PredictionModelHandler(private val context: Context) {
    private var interpreter: Interpreter? = null
    private var wordIndex: Map<String, Int>? = null
    private var indexWord: Map<Int, String>? = null
    // Maximum length of the sequence as per the findings from the Hrusso Aka dataset
    private var maxLen: Int = 54

    init {
        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            wordIndex = loadWordIndex(context)
            indexWord = wordIndex?.map { it.value to it.key }?.toMap()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Loads the tflite file containing the next-word prediction model
     */
    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd("prediction_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    /**
     * Loads the word index from the JSON file.
     * The JSON file contains the mapping of words to integers obtained during model training.
     *
     * @param context The context of the application.
     * @return The word index as a mutable map.
     */
    private fun loadWordIndex(context: Context): Map<String, Int> {
        val wordIndexJson: String = context.assets.open("word_index.json").use { inputStream ->
            inputStream.bufferedReader().use { it.readText() }
        }

        // Convert JSON into a map
        val jsonObject = JSONObject(wordIndexJson)
        val wordIndex = mutableMapOf<String, Int>()

        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            wordIndex[key] = jsonObject.getInt(key)
        }

        return wordIndex
    }


    /**
     * Tokenizes the text and converts it into a sequence of integers.
     * All special characters are removed, the text is converted to lowercase and split into tokens.
     *
     * @param text The text to be tokenized.
     * @return The tokenized text.
     */
    private fun tokenizeText(text: String): List<Int> {
        val cleanedText = text.lowercase().replace("[^a-z0-9 ]".toRegex(), "")
        val tokens = cleanedText.split(" ")
        val tokenizedText = mutableListOf<Int>()

        for (token in tokens) {
            val index = wordIndex?.get(token)
            if (index != null) {
                tokenizedText.add(index)
            } else {
                tokenizedText.add(0)
            }
        }

        return tokenizedText
    }


    /**
     * Pads the sequence with zeros to make it of the same length as the maximum length.
     *
     * @param sequence The sequence to be padded.
     * @return The padded sequence.
     */
    private fun padSequence(sequence: List<Int>): List<Int> {
        val paddedSequence = mutableListOf<Int>()
        val padding = maxLen - sequence.size

        for (i in 0 until padding) {
            paddedSequence.add(0)
        }

        paddedSequence.addAll(sequence)

        return paddedSequence
    }


    /**
     * Predicts the next word based on the text provided.
     *
     * @param text The text based on which the prediction is to be made.
     * @return The top 3 predictions.
     */
    fun predict(text: String): List<Pair<String, Float>> {
        val tokenizedText = tokenizeText(text)
        val paddedSequence = padSequence(tokenizedText)

        val input = Array(1) { FloatArray(maxLen) }
        for (i in 0 until maxLen) {
            input[0][i] = paddedSequence[i].toFloat()
        }

        val output = Array(1) { FloatArray(13598) }
        val startTime = System.nanoTime()
        interpreter?.run(input, output)
        val endTime = System.nanoTime()
        val elapsedTime = endTime - startTime

        Log.d("PredictionModelHandler", "Time taken: $elapsedTime ns")

        val probabilities = output[0]

        val topPredictions = probabilities
            .mapIndexed { index, value -> Pair(index, value) }
            .sortedByDescending { it.second }

        return topPredictions
            .filter { it.first != 0 }
            .take(3)
            .map { Pair(indexWord?.get(it.first) ?: "Unknown", it.second) }
    }
}
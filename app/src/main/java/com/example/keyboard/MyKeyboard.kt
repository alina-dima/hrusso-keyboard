package com.example.keyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import com.example.keyboard.databinding.KeyboardLayoutBinding
import com.example.keyboard.databinding.Symbols1LayoutBinding
import com.example.keyboard.databinding.Symbols2LayoutBinding
import com.example.keyboard.utils.ButtonIds.buttonIds
import com.example.keyboard.utils.ButtonIds.buttonSym1Ids
import com.example.keyboard.utils.ButtonIds.buttonSym2Ids
import com.example.keyboard.utils.ButtonIds.diacriticButtonIds
import com.example.keyboard.utils.KeyboardUtils.combineDiacritic
import com.example.keyboard.utils.KeyboardUtils.updateCapsState
import java.util.Locale

import android.util.Log
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView

class MyKeyboard : InputMethodService() {
    private var capsCount = 0
    private var diacritic = ""
    private lateinit var predictionModelHandler: PredictionModelHandler
    private lateinit var prediction1: TextView
    private lateinit var prediction2: TextView
    private lateinit var prediction3: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        val keyboardBinding = KeyboardLayoutBinding.inflate(layoutInflater)
        val symbols1LayoutBinding = Symbols1LayoutBinding.inflate(layoutInflater)
        val symbols2LayoutBinding = Symbols2LayoutBinding.inflate(layoutInflater)

        // Initialize the predictions
        predictionModelHandler = PredictionModelHandler(this)
        val predictions = predictionModelHandler.predict("")
        prediction1 = keyboardBinding.root.findViewById(R.id.prediction1)
        prediction2 = keyboardBinding.root.findViewById(R.id.prediction2)
        prediction3 = keyboardBinding.root.findViewById(R.id.prediction3)
        prediction1.text = predictions[0].first
        prediction2.text = predictions[1].first
        prediction3.text = predictions[2].first

        setListenersPredictions()
        setListenersMain(keyboardBinding, symbols1LayoutBinding)
        setListenersDiacritic(keyboardBinding)
        setListenersSym1(symbols1LayoutBinding, keyboardBinding, symbols2LayoutBinding)
        setListenersSym2(symbols2LayoutBinding, keyboardBinding, symbols1LayoutBinding)

        return keyboardBinding.root
    }


    /**
     * This function sets the listeners for the prediction buttons.
     * It listens for clicks on the predictions and appends it to the input connection.
     */
    private fun setListenersPredictions() {
        prediction1.setOnClickListener {
            currentInputConnection?.commitText(prediction1.text, 1)
        }
        prediction2.setOnClickListener {
            currentInputConnection?.commitText(prediction2.text, 1)
        }
        prediction3.setOnClickListener {
            currentInputConnection?.commitText(prediction3.text, 1)
        }
    }


    /**
     * This function sets the listeners for the main keyboard buttons.
     * It implements the functionality for handling letters, capitalisation, space, backspace, enter
     * and switching to the symbols keyboard layout.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListenersMain(keyboardBinding: KeyboardLayoutBinding,
                                 symbols1LayoutBinding: Symbols1LayoutBinding) {

        for (buttonId in buttonIds) {
            val button = keyboardBinding.root.findViewById<Button>(buttonId)
            button.setOnClickListener {
                val inputConnection = currentInputConnection
                val text = if (capsCount > 0) button.text.toString()
                    .uppercase(Locale.ROOT) else button.text.toString().lowercase()

                // Check if a diacritic has been pressed before and combine it with current letter
                if (diacritic != "") {
                    val newText = combineDiacritic(text, diacritic)
                    inputConnection?.commitText(newText, 1)
                    diacritic = ""
                } else {
                    inputConnection?.commitText(text, 1)
                }

                // Reset the caps count and update view if the user chose to capitalize one char
                if (capsCount == 1) {
                    capsCount = updateCapsState(buttonIds, keyboardBinding, 0)
                }
            }
        }

        keyboardBinding.btnSpace.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE))

            // Get text before the cursor
            val precedingText = inputConnection?.getTextBeforeCursor(100, 0).toString()

            // Get predictions
            val predictions = predictionModelHandler.predict(precedingText)

            // Update prediction TextViews
            prediction1.text = predictions[0].first
            prediction2.text = predictions[1].first
            prediction3.text = predictions[2].first

            return@setOnClickListener
        }

        keyboardBinding.btnBackSpace.setOnTouchListener { _, event ->
            val inputConnection = currentInputConnection
            when (event.action) {
                // Detects initial touch and deletes a character when let go
                MotionEvent.ACTION_DOWN -> {
                    inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                }
            }
            return@setOnTouchListener false
        }

        keyboardBinding.btnEnter.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            return@setOnClickListener
        }

        keyboardBinding.btnSym.setOnClickListener {
            setInputView(symbols1LayoutBinding.root)
        }

        keyboardBinding.btnCaps.setOnClickListener {
            it as ImageButton
            capsCount++
            Log.d("Caps", capsCount.toString())

            capsCount = updateCapsState(buttonIds, keyboardBinding, capsCount)
        }
    }


    /**
     * This function sets the listeners for the diacritic buttons.
     * It listens for clicks on the diacritics and combines them with the previous letter.
     */
    private fun setListenersDiacritic(keyboardBinding: KeyboardLayoutBinding) {
        for (diacriticButtonId in diacriticButtonIds) {
            val diacriticButton = keyboardBinding.root.findViewById<Button>(diacriticButtonId)
            diacriticButton.setOnClickListener {
                val text = diacriticButton.text.toString()
                diacritic = text
                val prevLetter = currentInputConnection?.getTextBeforeCursor(1, 0)
                if (prevLetter != " " && prevLetter != "") {
                    val combined = combineDiacritic(prevLetter.toString(), text)
                    currentInputConnection?.deleteSurroundingText(1, 0)
                    currentInputConnection?.commitText(combined, 1)
                    diacritic = ""
                }
            }
        }
    }


    /**
     * This function sets the listeners for the symbols1 layout buttons.
     * It handles the functionality of typing symbols, space, backspace, enter, switching to the
     * main layout and switching to the symbols2 layout.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListenersSym1(symbols1LayoutBinding: Symbols1LayoutBinding,
                                keyboardBinding: KeyboardLayoutBinding,
                                symbols2LayoutBinding: Symbols2LayoutBinding) {

        for (buttonId in buttonSym1Ids) {
            val button = symbols1LayoutBinding.root.findViewById<Button>(buttonId)
            button.setOnClickListener {
                val inputConnection = currentInputConnection
                val text = button.text.toString()
                inputConnection?.commitText(text, 1)
            }
        }

        symbols1LayoutBinding.btnSpace.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE))
        }

        symbols1LayoutBinding.btnBackSpace.setOnTouchListener { _, event ->
            val inputConnection = currentInputConnection
            when (event.action) {
                // Detects initial touch and deletes a character when let go
                MotionEvent.ACTION_DOWN -> {
                    inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                }
            }
            return@setOnTouchListener false
        }

        symbols1LayoutBinding.btnEnter.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))

            return@setOnClickListener
        }

        symbols1LayoutBinding.btnSym.setOnClickListener {
            capsCount = updateCapsState(buttonIds, keyboardBinding, 0)
            setInputView(keyboardBinding.root)
        }

        symbols1LayoutBinding.btnSymPage.setOnClickListener {
            setInputView(symbols2LayoutBinding.root)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    /**
     * This function sets the listeners for the symbols2 layout buttons.
     * It handles the functionality of typing symbols, space, backspace, enter, switching to the
     * main layout and switching to the symbols1 layout.
     */
    private fun setListenersSym2(symbols2LayoutBinding: Symbols2LayoutBinding,
                                 keyboardBinding: KeyboardLayoutBinding,
                                 symbols1LayoutBinding: Symbols1LayoutBinding) {

        for (buttonId in buttonSym2Ids) {
            val button = symbols2LayoutBinding.root.findViewById<Button>(buttonId)
            button.setOnClickListener {
                val inputConnection = currentInputConnection
                val text = button.text.toString()
                inputConnection?.commitText(text, 1)
            }
        }

        symbols2LayoutBinding.btnSpace.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE))
        }

        symbols2LayoutBinding.btnBackSpace.setOnTouchListener { _, event ->
            val inputConnection = currentInputConnection
            when (event.action) {
                // Detects initial touch and deletes a character when let go
                MotionEvent.ACTION_DOWN -> {
                    inputConnection?.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DEL
                        )
                    )
                }
            }

            return@setOnTouchListener false
        }

        symbols2LayoutBinding.btnEnter.setOnClickListener {
            val inputConnection = currentInputConnection
            inputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            return@setOnClickListener
        }

        symbols2LayoutBinding.btnSym.setOnClickListener {
            capsCount = updateCapsState(buttonIds, keyboardBinding, 0)
            setInputView(keyboardBinding.root)
        }

        symbols2LayoutBinding.btnSymPage.setOnClickListener {
            setInputView(symbols1LayoutBinding.root)
        }
    }
}
package com.example.keyboard.utils

import android.widget.Button
import android.widget.ImageButton
import com.example.keyboard.R
import com.example.keyboard.databinding.KeyboardLayoutBinding

object KeyboardUtils {
    /**
     * This function updates the state of the caps button and the text on the keyboard buttons.
     * The function returns the updated caps count.
     */
    fun updateCapsState(buttonIds: Array<Int>,
                        keyboardBinding: KeyboardLayoutBinding,
                        capsCount: Int) : Int {
        val capsButton = keyboardBinding.root.findViewById<ImageButton>(R.id.btnCaps)

        var newCapsCount = capsCount

        when (newCapsCount) {
            1 -> {
                capsButton.setImageResource(R.drawable.ic_caps2)
            }
            2 -> {
                capsButton.setImageResource(R.drawable.ic_caps3)
            }
            else -> {
                capsButton.setImageResource(R.drawable.ic_caps1)
                newCapsCount = 0
            }
        }

        for (buttonId in buttonIds) {
            val button = keyboardBinding.root.findViewById<Button>(buttonId)
            val text =
                if (newCapsCount > 0) button.text.toString().uppercase() else button.text.toString()
                    .lowercase()
            button.text = text
        }

        return newCapsCount
    }

    /**
     * This function combines the diacritic with the text.
     * The combined character is then returned.
     */
    fun combineDiacritic(text: String, diacritic: String): String {
        return when (diacritic) {
            "◌́" -> {
                when (text) {
                    "a" -> "á"
                    "e" -> "é"
                    "i" -> "í"
                    "o" -> "ó"
                    "u" -> "ú"
                    "ü" -> "ǘ"
                    else -> text
                }
            }
            "◌̀" -> {
                when (text) {
                    "a" -> "à"
                    "e" -> "è"
                    "i" -> "ì"
                    "o" -> "ò"
                    "u" -> "ù"
                    "ü" -> "ǜ"
                    else -> text
                }
            }
            "◌̈" -> {
                when (text) {
                    "a" -> "ä"
                    "e" -> "ë"
                    "i" -> "ï"
                    "o" -> "ö"
                    "u" -> "ü"
                    "ú" -> "ǘ"
                    "ù" -> "ǜ"
                    else -> text
                }
            }
            "◌̃" -> {
                when (text) {
                    "a" -> "ã"
                    "n" -> "ñ"
                    "o" -> "õ"
                    else -> text
                }
            }
            "◌̂" -> {
                when (text) {
                    "s" -> "ŝ"
                    "g" -> "ĝ"
                    else -> text
                }
            }
            else -> text
        }
    }
}
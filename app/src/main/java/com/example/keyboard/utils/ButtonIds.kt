package com.example.keyboard.utils

import com.example.keyboard.R

object ButtonIds {
    // List of the diacritic button IDs
    val diacriticButtonIds = arrayOf(
        R.id.btnAcute,
        R.id.btnGrave,
        R.id.btnUmlaut,
        R.id.btnTilde,
        R.id.btnCircumflex
    )

    // List of button IDs in the main layout
    var buttonIds = arrayOf(
        // First row
        R.id.btnQ, R.id.btnW, R.id.btnE, R.id.btnR, R.id.btnT,
        R.id.btnY, R.id.btnU, R.id.btnI, R.id.btnO, R.id.btnP,

        // Second row
        R.id.btnA, R.id.btnS, R.id.btnD, R.id.btnF, R.id.btnG,
        R.id.btnH, R.id.btnJ, R.id.btnK, R.id.btnL,

        // Third row
        R.id.btnZ, R.id.btnX, R.id.btnC, R.id.btnV, R.id.btnB,
        R.id.btnN, R.id.btnM,

        // Fourth row
        R.id.btnDot, R.id.btnComma
    )

    // List of button IDs in the symbols1 layout
    val buttonSym1Ids = arrayOf(
        // First row
        R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
        R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0,

        // Second row
        R.id.btnPlus, R.id.btnTimes, R.id.btnDivide, R.id.btnEqual, R.id.btnSlash,
        R.id.btnUnderscore, R.id.btnLess, R.id.btnMore, R.id.btnLBracket, R.id.btnRBracket,

        // Third row
        R.id.btnExcl, R.id.btnAt, R.id.btnHash, R.id.btnDollar, R.id.btnPercent,
        R.id.btnCaret, R.id.btnAmpersand, R.id.btnAsterisk, R.id.btnLParen, R.id.btnRParen,

        // Fourth row
        R.id.btnMinus, R.id.btnApotrophe, R.id.btnQuotation, R.id.btnColon, R.id.btnSemiColon,
        R.id.btnCommaSymbol, R.id.btnQuestion,

        // Fifth row
        R.id.btnComma, R.id.btnDot
    )

    // List of button IDs in the symbols2 layout
    val buttonSym2Ids = arrayOf(
        // First row
        R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
        R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0,

        // Second row
        R.id.btnQuote, R.id.btnTilde, R.id.btnBackslash, R.id.btnBar, R.id.btnLBrace,
        R.id.btnRBrace, R.id.btnEuro, R.id.btnPound, R.id.btnYen, R.id.btnRupee,

        // Third row
        R.id.btnSEC, R.id.btnSFC, R.id.btnBEC, R.id.btnBFC, R.id.btnBES,
        R.id.btnBFS, R.id.btnSpade, R.id.btnHeart, R.id.btnDiamond, R.id.btnClub,

        // Fourth row
        R.id.btnStar, R.id.btnSFS, R.id.btnCurrency, R.id.btnLGuillemet, R.id.btnRGuillemet,
        R.id.btnUDExcl, R.id.btnUDQuest,

        // Fifth row
        R.id.btnDot, R.id.btnComma
    )
}
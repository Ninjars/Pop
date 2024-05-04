package jez.jetpackpop.ui

import jez.jetpackpop.R
import jez.jetpackpop.features.app.domain.GameChapter

fun GameChapter.toTitleRes() =
    when (this) {
        GameChapter.SIMPLE_SINGLE -> R.string.main_menu_chap_1
        GameChapter.SIMPLE_DECOY -> R.string.main_menu_chap_2
        GameChapter.SPLITTER -> R.string.main_menu_chap_3
    }
package jez.jetpackpop.ui

import jez.jetpackpop.R
import jez.jetpackpop.features.app.domain.GameChapter

fun GameChapter.toTitleRes() =
    when (this) {
        GameChapter.SIMPLE_SINGLE -> R.string.chapter_title_simple_single
        GameChapter.SIMPLE_DECOY -> R.string.chapter_title_simple_decoy
        GameChapter.SPLITTER -> R.string.chapter_title_splitter
        GameChapter.SIMPLE_SINGLE_HARD -> R.string.chapter_title_simple_single_hard
        GameChapter.SIMPLE_DECOY_HARD -> R.string.chapter_title_simple_decoy_hard
        GameChapter.SPLITTER_HARD -> R.string.chapter_title_splitter_hard
    }
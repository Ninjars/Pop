package jez.jetpackpop.data

import jez.jetpackpop.model.GameChapter

data class HighScores(val chapterScores: Map<GameChapter, Int>)

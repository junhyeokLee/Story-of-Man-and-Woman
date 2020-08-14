package com.dev_sheep.story_of_man_and_woman.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.dev_sheep.story_of_man_and_woman.R

class PokemonColorUtil(var context: Context) {

    @ColorInt
    fun getPokemonColor(typeOfPokemon: String?): Int {

        val color = when (typeOfPokemon) {
            "남" -> R.color.blue
            "여" -> R.color.hot_pink
            "10대" -> R.color.lightBlue
            "20대" -> R.color.lightYellow
            "30대" -> R.color.lightPurple
            "40대" -> R.color.lightBrown
            "50대" -> R.color.black
            else -> R.color.lightBlue
        }
        return convertColor(color)
    }

    @ColorInt
    fun convertColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }
}

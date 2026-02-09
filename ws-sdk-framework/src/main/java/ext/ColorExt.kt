package ext

import android.graphics.Color

object ColorExt {

    fun changeAlpha(color: Int, fraction: Float): Int {
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        val alpha = (Color.alpha(color) * fraction).toInt();
        return Color.argb(alpha, red, green, blue)
    }
}

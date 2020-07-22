package kdl.api.util

import java.awt.Color

fun colorOf(rgb: Int): Color = Color(rgb)

fun colorOf(rgb: Int, alpha: Float): Color = Color(
    rgb ushr 16 and 0xFF,
    rgb ushr 8 and 0xFF,
    rgb and 0xFF,
    (alpha * 255).toInt()
)

val Color.r: Int get() = red
val Color.g: Int get() = green
val Color.b: Int get() = blue
val Color.a: Int get() = alpha

val Color.rf: Float get() = red / 255f
val Color.gf: Float get() = green / 255f
val Color.bf: Float get() = blue / 255f
val Color.af: Float get() = alpha / 255f

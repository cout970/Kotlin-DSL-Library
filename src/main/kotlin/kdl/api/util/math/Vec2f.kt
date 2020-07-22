package kdl.api.util.math

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Suppress("NOTHING_TO_INLINE")
inline fun vec2Of(x: Number, y: Number = x) = Vec2f(x.toFloat(), y.toFloat())

operator fun Vec2f.plus(other: Vec2f) = Vec2f(this.x + other.x, this.y + other.y)
operator fun Vec2f.minus(other: Vec2f) = Vec2f(this.x - other.x, this.y - other.y)
operator fun Vec2f.times(other: Vec2f) = Vec2f(this.x * other.x, this.y * other.y)
operator fun Vec2f.div(other: Vec2f) = Vec2f(this.x / other.x, this.y / other.y)

operator fun Vec2f.plus(other: Float) = Vec2f(this.x + other, this.y + other)
operator fun Vec2f.minus(other: Float) = Vec2f(this.x - other, this.y - other)
operator fun Vec2f.times(other: Float) = Vec2f(this.x * other, this.y * other)
operator fun Vec2f.div(other: Float) = Vec2f(this.x / other, this.y / other)

operator fun Vec2f.plus(other: Double) = Vec2f(this.x + other.toFloat(), this.y + other.toFloat())
operator fun Vec2f.minus(other: Double) = Vec2f(this.x - other.toFloat(), this.y - other.toFloat())
operator fun Vec2f.times(other: Double) = Vec2f(this.x * other.toFloat(), this.y * other.toFloat())
operator fun Vec2f.div(other: Double) = Vec2f(this.x / other.toFloat(), this.y / other.toFloat())

operator fun Vec2f.plus(other: Int) = Vec2f(this.x + other.toFloat(), this.y + other.toFloat())
operator fun Vec2f.minus(other: Int) = Vec2f(this.x - other.toFloat(), this.y - other.toFloat())
operator fun Vec2f.times(other: Int) = Vec2f(this.x * other.toFloat(), this.y * other.toFloat())
operator fun Vec2f.div(other: Int) = Vec2f(this.x / other.toFloat(), this.y / other.toFloat())

operator fun Vec2f.plus(other: Long) = Vec2f(this.x + other.toFloat(), this.y + other.toFloat())
operator fun Vec2f.minus(other: Long) = Vec2f(this.x - other.toFloat(), this.y - other.toFloat())
operator fun Vec2f.times(other: Long) = Vec2f(this.x * other.toFloat(), this.y * other.toFloat())
operator fun Vec2f.div(other: Long) = Vec2f(this.x / other.toFloat(), this.y / other.toFloat())

fun Vec2f.toVec3d(z: Double = 0.0) = Vec3d(x.toDouble(), y.toDouble(), z)
fun Vec2f.toVec3i(z: Int = 0) = Vec3i(x.toInt(), y.toInt(), z)

fun Vec2f.withX(x: Float) = Vec2f(x, y)
fun Vec2f.withY(y: Float) = Vec2f(x, y)

val Vec2f.xy: Vec2f get() = Vec2f(x, y)
val Vec2f.yx: Vec2f get() = Vec2f(y, x)

inline val Vec2f.xi: Int get() = x.toInt()
inline val Vec2f.yi: Int get() = y.toInt()

inline val Vec2f.xd: Double get() = x.toDouble()
inline val Vec2f.yd: Double get() = y.toDouble()

inline val Vec2f.xf: Float get() = x
inline val Vec2f.yf: Float get() = y

val Vec2f.isNaN: Boolean get() = x.isNaN() || y.isNaN()

fun Vec2f.rotateRad(angle: Float): Vec2f {
    val sn = sin(angle)
    val cs = cos(angle)

    return Vec2f(x * cs - y * sn, x * sn + y * cs)
}

fun Vec2f.rotateDeg(angle: Float): Vec2f = rotateRad(angle / 180f * PI.toFloat())

fun Vec2f.toRad(): Vec2f = Vec2f(x / 180f * PI.toFloat(), y / 180f * PI.toFloat())
fun Vec2f.toDeg(): Vec2f = Vec2f(x * PI.toFloat() / 180f, y * PI.toFloat() / 180f)


package kdl.api.gui

import net.minecraft.util.math.Vec2f

sealed class Pos {

    @Suppress("NOTHING_TO_INLINE")
    companion object {
        inline fun fixed(x: Number, y: Number = x) = FixedPos(Vec2f(x.toFloat(), y.toFloat()))
        inline fun centerRelative(x: Number, y: Number = x) = CenterRelPos(Vec2f(x.toFloat(), y.toFloat()))
        inline fun parentRelative(x: Number, y: Number = x) = ParentRelPos(Vec2f(x.toFloat(), y.toFloat()))
    }
}

data class FixedPos(val vec: Vec2f) : Pos() {
    override fun toString(): String = "FixedPos(${vec.x}, ${vec.y})"
}

data class CenterRelPos(val vec: Vec2f) : Pos() {
    override fun toString(): String = "CenterRelPos(${vec.x}, ${vec.y})"
}

data class ParentRelPos(val vec: Vec2f) : Pos() {
    override fun toString(): String = "ParentRelPos(${vec.x}, ${vec.y})"
}

sealed class Size {
    @Suppress("NOTHING_TO_INLINE")
    companion object {
        inline fun fixed(x: Number, y: Number = x) = FixedSize(Vec2f(x.toFloat(), y.toFloat()))
        inline fun screenRelative(x: Number, y: Number = x) = ScreenRelSize(Vec2f(x.toFloat(), y.toFloat()))
        inline fun parentRelative(x: Number, y: Number = x) = ParentRelSize(Vec2f(x.toFloat(), y.toFloat()))
    }
}

data class FixedSize(val vec: Vec2f) : Size() {
    override fun toString(): String = "FixedSize(${vec.x}, ${vec.y})"
}

data class ScreenRelSize(val vec: Vec2f) : Size() {
    override fun toString(): String = "ScreenRelSize(${vec.x}, ${vec.y})"
}

data class ParentRelSize(val vec: Vec2f) : Size() {
    override fun toString(): String = "ParentRelSize(${vec.x}, ${vec.y})"
}

/**
 * Horizontal Alignment
 */
enum class HAlignment {
    Left, Middle, Right
}

/**
 * Vertical Alignment
 */
enum class VAlignment {
    Top, Middle, Bottom
}
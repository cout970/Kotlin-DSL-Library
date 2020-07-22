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

data class FixedPos(val vec: Vec2f) : Pos()

data class CenterRelPos(val vec: Vec2f) : Pos()

data class ParentRelPos(val vec: Vec2f) : Pos()

sealed class Size {
    @Suppress("NOTHING_TO_INLINE")
    companion object {
        inline fun fixed(x: Number, y: Number = x) = FixedSize(Vec2f(x.toFloat(), y.toFloat()))
        inline fun screenRelative(x: Number, y: Number = x) = ScreenRelSize(Vec2f(x.toFloat(), y.toFloat()))
        inline fun parentRelative(x: Number, y: Number = x) = ParentRelSize(Vec2f(x.toFloat(), y.toFloat()))
    }
}

data class FixedSize(val vec: Vec2f) : Size()

data class ScreenRelSize(val vec: Vec2f) : Size()

data class ParentRelSize(val vec: Vec2f) : Size()

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
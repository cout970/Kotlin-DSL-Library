package kdl.api.gui

import kdl.api.util.id
import kdl.api.util.math.vec2Of
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import java.awt.Color

interface GuiRenderer {

    val matrices: MatrixStack

    fun draw(shape: Renderable)

    fun getSize(size: Size): Vec2f

    fun getPos(pos: Pos): Vec2f
}

sealed class Renderable {
    /* Rectangular shape */
    data class Rect(
        var pos: Pos,
        var size: Size,
        var mode: DrawMode
    ) : Renderable()

    /* Rectangular shape with bevel on the corners */
    data class RoundRect(
        var pos: Pos,
        var size: Size,
        var radius: Int,
        var mode: DrawMode
    ) : Renderable()

    /* Circular shape */
    data class Circle(
        var pos: Pos,
        var radius: Float,
        var mode: DrawMode
    ) : Renderable()

    /* ItemStack */
    data class Stack(
        var pos: Pos,
        var stack: ItemStack
    ) : Renderable()

    /* A line of text */
    data class Text(
        var pos: Pos,
        var string: String = "",
        var color: Color,
        var shade: Boolean = false,
        var align: HAlignment = HAlignment.Left,
        var hover: Boolean = false
    ) : Renderable()

    /* Ordered list of items to render together */
    data class Group(
        val items: MutableList<Renderable>
    ) : Renderable()
}


class DrawMode {

    enum class Option {
        Textured, Sprite, Gradient, Solid, Stroke
    }

    var option: Option = Option.Solid

    // Textured
    var texture: Identifier = id("minecraft", "misingno")
    var uvPos: Vec2f = Vec2f.ZERO
    var uvSize: Vec2f = vec2Of(1)

    // Sprite
    var sprite: Sprite? = null

    // Gradient
    var start: Color = Color.WHITE
    var end: Color = Color.WHITE

    // Solid/Stroke
    var color: Color = Color.WHITE

    // Stroke
    var thickness: Int = 1

    constructor(texture: Identifier, uvPos: Vec2f, uvSize: Vec2f) {
        option = Option.Textured
        this.texture = texture
        this.uvPos = uvPos
        this.uvSize = uvSize
    }

    constructor(sprite: Sprite) {
        option = Option.Sprite
        this.sprite = sprite
    }

    constructor(color: Color) {
        option = Option.Solid
        this.color = color
    }

    constructor(start: Color, end: Color) {
        option = Option.Gradient
        this.start = start
        this.end = end
    }

    constructor(color: Color, thickness: Int) {
        option = Option.Stroke
        this.color = color
        this.thickness = thickness
    }
}
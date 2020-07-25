package kdl.api.gui.widgets

import kdl.KDL_ID
import kdl.api.gui.DrawMode
import kdl.api.gui.Pos
import kdl.api.gui.Renderable
import kdl.api.gui.Size
import kdl.api.util.id
import kdl.api.util.math.div
import kdl.api.util.math.vec2Of
import kdl.api.util.math.withX
import kdl.api.util.math.withY
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f

class ProgressBarWidgetBuilder {
    var label: String = "ProgressBar"
    var pos: Pos = Pos.centerRelative(-50, -50)
    var size: Size = Size.fixed(100, 100)
    var texture: Identifier = id("minecraft", "missigno")
    var textureSize: Int = 256
    var textureUV: Vec2f = Vec2f.ZERO
    var textureUVSize: Vec2f = vec2Of(textureSize)
    var progressGetter: () -> Float = { 1f }
}

fun WidgetHolder.horizontalProgressBar(config: ProgressBarWidgetBuilder.() -> Unit) {
    val builder = ProgressBarWidgetBuilder().also(config)

    widget<Renderable.Rect> {
        widgetType = id(KDL_ID, "horizontal_progress_bar")
        label = builder.label
        onCreate = {
            Renderable.Rect(
                builder.pos, builder.size, DrawMode(
                    texture = builder.texture,
                    uvPos = builder.textureUV / builder.textureSize,
                    uvSize = builder.textureUVSize / builder.textureSize
                )
            )
        }
        onRender = { state, renderer ->
            val size = renderer.getSize(builder.size)
            val progress: Float = (builder.progressGetter() * size.x).toInt() / size.x

            val u = progress * builder.textureUVSize.x / builder.textureSize
            val sizeX = size.x * progress

            state.size = Size.fixed(sizeX, size.y)
            state.mode.uvSize = state.mode.uvSize.withX(u)
            renderer.draw(state)
        }
    }
}

fun WidgetHolder.verticalProgressBar(config: ProgressBarWidgetBuilder.() -> Unit) {
    val builder = ProgressBarWidgetBuilder().also(config)

    widget<Renderable.Rect> {
        widgetType = id(KDL_ID, "vertical_progress_bar")
        label = builder.label
        onCreate = {
            Renderable.Rect(
                builder.pos, builder.size, DrawMode(
                    texture = builder.texture,
                    uvPos = builder.textureUV / builder.textureSize,
                    uvSize = builder.textureUVSize / builder.textureSize
                )
            )
        }
        onRender = { state, renderer ->
            val pos = renderer.getPos(builder.pos)
            val size = renderer.getSize(builder.size)

            val basePosY = builder.textureUV.y / builder.textureSize
            val baseSizeY = builder.textureUVSize.y / builder.textureSize

            val progress: Float = (builder.progressGetter() * size.y).toInt() / size.y

            val posY = pos.y + size.y * (1f - progress)
            val sizeY = size.y * progress

            val uvPosY = basePosY + (1 - progress) * baseSizeY
            val uvSizeY = progress * baseSizeY

            state.pos = Pos.fixed(pos.x, posY)
            state.size = Size.fixed(size.x, sizeY)

            state.mode.uvPos = state.mode.uvPos.withY(uvPosY)
            state.mode.uvSize = state.mode.uvSize.withY(uvSizeY)
            renderer.draw(state)
        }
    }
}
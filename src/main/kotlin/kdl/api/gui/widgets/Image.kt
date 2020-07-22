package kdl.api.gui.widgets

import kdl.KDL_ID
import kdl.api.gui.DrawMode
import kdl.api.gui.Pos
import kdl.api.gui.Renderable
import kdl.api.gui.Size
import kdl.api.util.id
import kdl.api.util.math.div
import kdl.api.util.math.vec2Of
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f

class ImageWidgetBuilder {
    var label: String = "Image"
    var pos: Pos = Pos.centerRelative(-50, -50)
    var size: Size = Size.fixed(100, 100)
    var texture: Identifier = id("minecraft", "missigno")
    var textureSize: Int = 256
    var textureUV: Vec2f = Vec2f.ZERO
    var textureUVSize: Vec2f = vec2Of(textureSize)
}

fun WidgetBuilder<*>.image(config: ImageWidgetBuilder.() -> Unit) {
    val builder = ImageWidgetBuilder().also(config)

    widget<Renderable> {
        widgetType = id(KDL_ID, "image")
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
        onRender = { state, renderer -> renderer.draw(state) }
    }
}
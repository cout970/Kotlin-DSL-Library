package kdl.api.gui.widgets

import kdl.KDL_ID
import kdl.api.gui.Pos
import kdl.api.gui.Renderable
import kdl.api.util.Color
import kdl.api.util.colorOf
import kdl.api.util.id
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

class TextLabelBuilder {
    var text: Text = LiteralText("TextLabel")
    var pos: Pos = Pos.parentRelative(0, 0)
    var color: Color = colorOf(0x404040)
}

fun WidgetHolder.textLabel(config: TextLabelBuilder.() -> Unit) {
    val builder = TextLabelBuilder().apply(config)

    widget<Renderable.Text> {
        widgetType = id(KDL_ID, "text_label")
        label = builder.text.asString()
        onCreate = {
            Renderable.Text(
                pos = builder.pos,
                string = builder.text.string,
                color = builder.color
            )
        }
        onRender = { state, renderer ->
            renderer.draw(state)
        }
    }
}
package kdl.api.gui.widgets

import kdl.KDL_ID
import kdl.api.gui.Pos
import kdl.api.gui.Size
import kdl.api.util.id

class ContainerWidgetBuilder : WidgetHolder {
    var label: String = "Container"
    var pos: Pos = Pos.centerRelative(-88, -83)
    var size: Size = Size.fixed(176, 166)
    override val children = mutableListOf<WidgetBuilder<*>>()
}

class ContainerWidgetState(
    override var pos: Pos,
    override var size: Size
) : WidgetRelativePos, WidgetRelativeSize

/**
 * A container of widgets, allows child widgets to use relative positions and relative sizes
 */
fun WidgetHolder.container(config: ContainerWidgetBuilder.() -> Unit) {
    val builder = ContainerWidgetBuilder().also(config)

    widget<ContainerWidgetState> {
        widgetType = id(KDL_ID, "container")
        label = builder.label
        children += builder.children

        onCreate = { ContainerWidgetState(builder.pos, builder.size) }
    }
}
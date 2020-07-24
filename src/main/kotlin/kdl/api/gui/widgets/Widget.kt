package kdl.api.gui.widgets

import kdl.api.KDL
import kdl.api.gui.GuiRenderer
import kdl.api.gui.Pos
import kdl.api.gui.ScreenCtx
import kdl.api.gui.Size
import net.minecraft.util.Identifier
import java.util.*

// Context for the widget
data class WidgetCtx(
    val screenCtx: ScreenCtx,
    val root: Widget<Unit>
)

interface WidgetDefinition<State> {
    // Type of this widget
    val type: Identifier

    // Children widgets
    val children: List<WidgetDefinition<*>>

    // Label to identify widgets while debugging
    val label: String

    // Called at widget creation, context is not yet available,
    // returns the default state of the widget
    val onCreate: (() -> State)?

    // Called every tick to render the widget on the screen
    val onRender: (WidgetCtx.(State, GuiRenderer) -> Unit)?
}

@KDL
interface WidgetHolder {
    val children: MutableList<WidgetBuilder<*>>

    fun <T> widget(func: WidgetBuilder<T>.() -> Unit) {
        val dsl = WidgetBuilder<T>().apply(func)
        if (dsl.widgetType == null) {
            error("Widget defined without id")
        }

        children += dsl
    }
}

@KDL
open class WidgetBuilder<State> : WidgetDefinition<State>, WidgetHolder {
    // Every Widget must have a type
    var widgetType: Identifier? = null
    override val type: Identifier get() = widgetType!!

    override var label: String = "Widget"
    override val children = mutableListOf<WidgetBuilder<*>>()

    override var onCreate: (() -> State)? = null
    override var onRender: (WidgetCtx.(State, renderer: GuiRenderer) -> Unit)? = null
}

interface Widget<State> {
    // Unique id of the widget
    val uuid: UUID

    // The internal state of the widget, if the widget has no state, Unit will be used instead
    val state: State

    // Readonly information about the widget
    val def: WidgetDefinition<State>

    // Children widgets
    val children: Map<UUID, Widget<*>>
}

// Allow children to use relative positions
interface WidgetRelativePos {
    val pos: Pos
}

// Allow children to use relative sizes
interface WidgetRelativeSize {
    val size: Size
}
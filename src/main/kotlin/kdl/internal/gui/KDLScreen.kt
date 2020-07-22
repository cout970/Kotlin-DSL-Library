package kdl.internal.gui

import kdl.KDL_ID
import kdl.api.gui.GuiManager
import kdl.api.gui.ScreenBuilder
import kdl.api.gui.widgets.Widget
import kdl.api.gui.widgets.WidgetBuilder
import kdl.api.gui.widgets.WidgetCtx
import kdl.api.gui.widgets.WidgetDefinition
import kdl.api.util.id
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.util.*
import kotlin.collections.LinkedHashMap

class KDLScreen(val config: ScreenBuilder, val handler: KDLScreenHandler, playerInv: PlayerInventory, title: Text) :
    Screen(title),
    ScreenHandlerProvider<KDLScreenHandler> {

    val ctx: WidgetCtx get() = WidgetCtx(this, handler, root as Widget<Unit>)
    val renderer = KDLGuiRenderer(this)
    val root: WidgetInstance

    init {
        config.onInit?.invoke(this, playerInv, title)

        val builder = WidgetBuilder<Unit>()
        builder.widgetType = id(KDL_ID, "root")
        builder.label = "Root widget"
        config.widgets?.invoke(builder, this)

        root = create(builder)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        renderer.matrices = matrices
        root.onRender(ctx, renderer)
    }

    fun create(builder: WidgetBuilder<*>): WidgetInstance {
        val children = LinkedHashMap<UUID, WidgetInstance>()

        for (child in builder.children) {
            val instance = create(child)
            children[instance.uuid] = instance
        }

        @Suppress("UNCHECKED_CAST")
        return WidgetInstance(
            UUID.randomUUID(),
            builder.onCreate?.invoke() ?: Unit,
            builder as WidgetDefinition<Any>,
            children
        )
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        // F3 reload the screen config
        if (keyCode == GLFW.GLFW_KEY_F6) {
            GuiManager.reloadGuiConfigs()
            onClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun onClose() {
        config.onClose?.invoke(this)
        super.onClose()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun getScreenHandler(): KDLScreenHandler = handler
}

class WidgetInstance(
    override val uuid: UUID,
    override var state: Any = Unit,
    override val def: WidgetDefinition<Any>,
    override val children: LinkedHashMap<UUID, WidgetInstance>
) : Widget<Any> {

    fun onRender(ctx: WidgetCtx, renderer: KDLGuiRenderer) {
        def.onRender?.invoke(ctx, state, renderer)
        for ((_, widget) in children) {
            widget.onRender(ctx, renderer)
        }
    }
}
package kdl.internal.gui

import kdl.KDL_ID
import kdl.api.gui.GuiManager
import kdl.api.gui.ScreenBuilder
import kdl.api.gui.widgets.*
import kdl.api.util.id
import kdl.api.util.math.plus
import kdl.api.util.math.times
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW
import java.util.*
import kotlin.collections.LinkedHashMap

class KDLScreen(val config: ScreenBuilder, handler: KDLScreenHandler, playerInv: PlayerInventory, title: Text) :
    HandledScreen<KDLScreenHandler>(handler, playerInv, title) {

    val ctx: WidgetCtx get() = WidgetCtx(this, handler, root as Widget<Unit>)
    val renderer = KDLGuiRenderer(this)
    val root: WidgetInstance

    val containerWidth: Int get() = super.backgroundWidth
    val containerHeight: Int get() = super.backgroundHeight

    init {
        config.onInit?.invoke(this, playerInv, title)

        val builder = WidgetBuilder<Unit>()
        builder.widgetType = id(KDL_ID, "root")
        builder.label = "Root widget"
        config.widgets.execute { it(builder, this) }

        root = create(builder)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        renderer.matrices = matrices
        renderer.parentPos = Vec2f(0f, 0f)
        renderer.parentSize = Vec2f(containerWidth.toFloat(), containerHeight.toFloat())
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
        // F6 reload the screen config
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

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) = Unit
}

class WidgetInstance(
    override val uuid: UUID,
    override var state: Any = Unit,
    override val def: WidgetDefinition<Any>,
    override val children: LinkedHashMap<UUID, WidgetInstance>
) : Widget<Any> {

    fun onRender(ctx: WidgetCtx, renderer: KDLGuiRenderer) {
        val state = this.state
        val parentPosSave = renderer.parentPos
        val parentSizeSave = renderer.parentSize

        def.onRender?.invoke(ctx, state, renderer)

        if (state is WidgetRelativePos) {
            renderer.parentPos += renderer.getPos(state.pos)
        }
        if (state is WidgetRelativeSize) {
            renderer.parentSize *= renderer.getSize(state.size)
        }

        for ((_, widget) in children) {
            widget.onRender(ctx, renderer)
        }

        renderer.parentPos = parentPosSave
        renderer.parentSize = parentSizeSave
    }
}
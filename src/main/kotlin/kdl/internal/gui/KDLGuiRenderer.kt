package kdl.internal.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import kdl.api.gui.*
import kdl.api.util.a
import kdl.api.util.b
import kdl.api.util.g
import kdl.api.util.math.plus
import kdl.api.util.math.times
import kdl.api.util.math.xi
import kdl.api.util.math.yi
import kdl.api.util.r
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import org.lwjgl.opengl.GL11
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class KDLGuiRenderer(val screen: KDLScreen) :
    GuiRenderer {
    override val textureManager: TextureManager = MinecraftClient.getInstance().textureManager
    override val textRenderer: TextRenderer = MinecraftClient.getInstance().textRenderer
    override var matrices = MatrixStack()
    var parentPos: Vec2f = Vec2f.ZERO
    var parentSize: Vec2f = Vec2f.SOUTH_EAST_UNIT

    override fun draw(shape: Renderable) {
        when (shape) {
            is Renderable.Rect -> drawRect(shape)
            is Renderable.RoundRect -> drawRoundRect(shape)
            is Renderable.Circle -> drawCircle(shape)
            is Renderable.Stack -> {
                val posVec = getPos(shape.pos)

                MinecraftClient.getInstance()
                    .itemRenderer
                    .renderGuiItemIcon(shape.stack, posVec.xi, posVec.yi)
            }
            is Renderable.Text -> {
                val posVec = getPos(shape.pos)

                MinecraftClient.getInstance()
                    .textRenderer
                    .draw(matrices, shape.string, posVec.x, posVec.y, shape.color.rgba)
            }
            is Renderable.Group -> {
                shape.items.forEach { draw(it) }
            }
            is Renderable.Custom -> {
                shape.func(screen.widgetCtx, this)
            }
        }
    }

    fun drawRect(shape: Renderable.Rect) {
        val posVec = getPos(shape.pos)
        val sizeVec = getSize(shape.size)

        val x0 = posVec.x
        val x1 = posVec.x + sizeVec.x
        val y0 = posVec.y
        val y1 = posVec.y + sizeVec.y
        val z = 0.0f

        val vertices = floatArrayOf(
            x0, y1, z, 0f, 1f,
            x1, y1, z, 1f, 1f,
            x1, y0, z, 1f, 0f,
            x0, y0, z, 0f, 0f
        )

        drawWithMode(vertices, GL11.GL_QUADS, shape.mode)
    }

    fun drawRoundRect(shape: Renderable.RoundRect) {
        val posVec = getPos(shape.pos)
        val sizeVec = getSize(shape.size)

        val x0 = posVec.x
        val x1 = posVec.x + sizeVec.x
        val y0 = posVec.y
        val y1 = posVec.y + sizeVec.y
        val z = 0.0f

        val vertices = floatArrayOf(
            x0, y1, z, 0f, 1f,
            x1, y1, z, 1f, 1f,
            x1, y0, z, 1f, 0f,
            x0, y0, z, 0f, 0f
        )

        drawWithMode(vertices, GL11.GL_QUADS, shape.mode)
    }

    fun drawCircle(shape: Renderable.Circle) {
        val posVec = getPos(shape.pos)
        val quality = max(20, (shape.radius * 0.5).toInt())
        val z = 0.0f

        val vertices = FloatArray(quality * 5) { 0f }

        vertices[0] = posVec.x
        vertices[1] = posVec.y
        vertices[2] = z
        vertices[3] = 0f
        vertices[4] = 0f

        repeat(quality - 1) { index ->
            val c = (index + 1) * 5
            val angle = (PI * 2 * index / quality).toFloat()
            val sn = sin(angle)
            val cs = cos(angle)

            vertices[c] = posVec.x + shape.radius * sn
            vertices[c + 1] = posVec.y + shape.radius * cs
            vertices[c + 2] = z
            vertices[c + 3] = sn * 0.5f + 1f
            vertices[c + 4] = cs * 0.5f + 1f
        }

        drawWithMode(vertices, GL11.GL_TRIANGLE_FAN, shape.mode)
    }

    fun drawWithMode(vert: FloatArray, glMode: Int, mode: DrawMode) {
        val matrix = matrices.peek().model
        val bufferBuilder = Tessellator.getInstance().buffer
        val vertexComponents = 5
        val vertexCount = vert.size / vertexComponents
        // Current
        var c = 0

        when (mode.option) {
            DrawMode.Option.Textured -> {
                textureManager.bindTexture(mode.texture)
                bufferBuilder.begin(
                    glMode,
                    VertexFormats.POSITION_TEXTURE
                )

                repeat(vertexCount) {
                    bufferBuilder
                        .vertex(matrix, vert[c], vert[c + 1], vert[c + 2])
                        .texture(mode.uvPos.x + vert[c + 3] * mode.uvSize.x, mode.uvPos.y + vert[c + 4] * mode.uvSize.y)
                        .next()
                    c += vertexComponents
                }
            }
            DrawMode.Option.Sprite -> {
                val sprite = mode.sprite!!
                textureManager.bindTexture(sprite.atlas.id)
                bufferBuilder.begin(
                    glMode,
                    VertexFormats.POSITION_TEXTURE
                )

                repeat(vertexCount) {
                    bufferBuilder
                        .vertex(matrix, vert[c], vert[c + 1], vert[c + 2])
                        .texture(sprite.getFrameU(vert[c + 3] * 16.0), sprite.getFrameV(vert[c + 4] * 16.0))
                        .next()
                    c += vertexComponents
                }
            }
            DrawMode.Option.Solid -> {
                bufferBuilder.begin(glMode, VertexFormats.POSITION_COLOR)
                val color = mode.color

                repeat(vertexCount) {
                    bufferBuilder
                        .vertex(matrix, vert[c], vert[c + 1], vert[c + 2])
                        .color(color.r, color.g, color.b, color.a)
                        .next()
                    c += vertexComponents
                }
            }
            DrawMode.Option.Gradient -> {
                bufferBuilder.begin(glMode, VertexFormats.POSITION_COLOR)

                repeat(vertexCount) {
                    // TODO use UV to interpolate between colors
                    val color = mode.start
                    bufferBuilder
                        .vertex(matrix, vert[c], vert[c + 1], vert[c + 2])
                        .color(color.r, color.g, color.b, color.a)
                        .next()
                    c += vertexComponents
                }
            }
            DrawMode.Option.Stroke -> {
                bufferBuilder.begin(glMode, VertexFormats.POSITION_COLOR)
                val color = mode.color

                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)
                GlStateManager.polygonMode(
                    GL11.GL_FRONT_AND_BACK,
                    GL11.GL_LINE
                )
                GlStateManager.lineWidth(mode.thickness.toFloat())

                repeat(vertexCount) {
                    bufferBuilder
                        .vertex(matrix, vert[c], vert[c + 1], vert[c + 2])
                        .color(color.r, color.g, color.b, color.a)
                        .next()
                    c += vertexComponents
                }

                bufferBuilder.end()
                RenderSystem.enableAlphaTest()
                BufferRenderer.draw(bufferBuilder)
                GL11.glPopAttrib()
                return
            }
        }
        bufferBuilder.end()
        RenderSystem.enableAlphaTest()
        BufferRenderer.draw(bufferBuilder)
    }

    override fun getPos(pos: Pos): Vec2f = when (pos) {
        is FixedPos -> pos.vec
        is CenterRelPos -> pos.vec + Vec2f(
            (screen.containerWidth / 2).toFloat(),
            (screen.containerHeight / 2).toFloat()
        )
        is ParentRelPos -> (pos.vec + parentPos)
    }

    override fun getSize(size: Size): Vec2f = when (size) {
        is FixedSize -> size.vec
        is ScreenRelSize -> size.vec * Vec2f(
            screen.width.toFloat(),
            screen.height.toFloat()
        )
        is ParentRelSize -> size.vec * parentPos
    }
}
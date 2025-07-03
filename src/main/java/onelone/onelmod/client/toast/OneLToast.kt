package onelone.onelmod.client.toast

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.toast.AdvancementToast
import net.minecraft.client.toast.Toast
import net.minecraft.client.toast.ToastManager
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import onelone.onelmod.client.OneLClient

class OneLToast(private val icon: ItemStack, private val title: Text, private val text: Text) : Toast {
    private val texture: Identifier = Identifier.ofVanilla("toast/advancement")
    private val defaultDurationMS: Int = 5000

    private var toastVisibility: Toast.Visibility = Toast.Visibility.SHOW

    companion object {
        fun show(icon: Item, title: Text, text: Text) {
            show(ItemStack(icon), title, text)
        }

        fun show(icon: ItemStack, title: Text, text: Text) {
            OneLClient.mc.toastManager.add(OneLToast(icon, title, text))
        }
    }
    override fun update(manager: ToastManager?, time: Long) {
        toastVisibility = if (time.toDouble() >= defaultDurationMS * OneLClient.mc.toastManager.notificationDisplayTimeMultiplier) Toast.Visibility.HIDE else Toast.Visibility.SHOW
    }

    override fun draw(context: DrawContext, textRenderer: TextRenderer, startTime: Long) {
        context.drawGuiTexture(RenderLayer::getGuiTextured, texture, 0, 0, this.width, this.height)

        val textLines = textRenderer.wrapLines(text, 125)
        val toastColor = 0xffff00

        if (textLines.size == 1) {
            context.drawText(
                textRenderer,
                title,
                30,
                7,
                toastColor or Colors.BLACK,
                false
            )
            context.drawText(textRenderer, textLines[0] as OrderedText, 30, 18, -1, false)
        } else {
            val titleDuration = 1500
            val f = 300.0f

            if (startTime < titleDuration) {
                val defaultColor = MathHelper.floor(
                    MathHelper.clamp(
                        (titleDuration - startTime).toFloat() / f,
                        0.0f,
                        1.0f
                    ) * 255.0f
                ) shl 24 or 0x04000000

                context.drawText(
                    textRenderer,
                    title,
                    30,
                    11,
                    toastColor or defaultColor,
                    false
                )
            } else {
                val defaultColor = MathHelper.floor(
                    MathHelper.clamp(
                        (startTime - titleDuration).toFloat() / f,
                        0.0f,
                        1.0f
                    ) * 252.0f
                ) shl 24 or 0x04000000
                var textY = this.height / 2 - textLines.size * 9 / 2

                for (orderedText in textLines) {
                    context.drawText(textRenderer, orderedText, 30, textY, 0xffffff or defaultColor, false)
                    textY += 9
                }
            }
        }

        context.drawItemWithoutEntity(icon, 8, 8)
    }

    override fun getVisibility(): Toast.Visibility {
        return toastVisibility
    }
}

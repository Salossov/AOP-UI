package aop.ui.client;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Keeps the complete caption and clips only its viewport, never its contents. */
final class ScrollingText {
    private final TextScrollState motion = new TextScrollState();
    private Component caption = Component.empty();
    private int viewport = -1;
    private float textScale;

    void draw(GuiGraphics g, Font font, Component text, int left, int top, int width, int height,
            int color, boolean fast, boolean centered, float scale) {
        if (width <= 0 || height <= 0) return;
        if (!text.equals(caption) || viewport != width || textScale != scale) {
            caption = text.copy(); viewport = width; textScale = scale; motion.reset();
        }
        double textWidth = font.width(text) * (double) scale;
        double offset = motion.advance(Util.getMillis(), Math.max(0, textWidth - width), fast);
        double start = textWidth <= width && centered ? (width - textWidth) / 2 : -offset;
        g.enableScissor(left, top, left + width, top + height);
        g.pose().pushPose();
        g.pose().translate(left + start, top + (height - (font.lineHeight - 1) * scale) / 2, 0);
        g.pose().scale(scale, scale, 1);
        g.drawString(font, text, 0, 0, color, false);
        g.pose().popPose();
        g.disableScissor();
    }
}

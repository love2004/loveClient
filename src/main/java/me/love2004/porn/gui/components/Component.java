package me.love2004.porn.gui.components;

import me.love2004.porn.loveClient;
import me.love2004.porn.Feature;
import me.love2004.porn.gui.PhobosGui;
import me.love2004.porn.gui.components.items.Item;
import me.love2004.porn.gui.components.items.buttons.Button;
import me.love2004.porn.modules.client.ClickGui;
import me.love2004.porn.modules.client.Colors;
import me.love2004.porn.modules.client.HUD;
import me.love2004.porn.util.ColorUtil;
import me.love2004.porn.util.MathUtil;
import me.love2004.porn.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Component
        extends Feature {
    private final ArrayList<Item> items = new ArrayList<>();
    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    private boolean hidden = false;

    public Component(String name, int x, int y, boolean open) {
        super(name);
        this.x = x;
        this.y = y;
        width = 88;
        height = 18;
        this.open = open;
        setupItems();
    }

    public void setupItems() {
    }

    private void drag(int mouseX, int mouseY) {
        if (!drag) {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drag(mouseX, mouseY);
        float totalItemHeight = open ? getTotalItemHeight() - 2.0f : 0.0f;
        int color = -7829368;
        if (ClickGui.getInstance().devSettings.getValue()) {
            color = ClickGui.getInstance().colorSync.getValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toARGB(ClickGui.getInstance().topRed.getValue(), ClickGui.getInstance().topGreen.getValue(), ClickGui.getInstance().topBlue.getValue(), ClickGui.getInstance().topAlpha.getValue());
        }
        if (ClickGui.getInstance().rainbow.getValue() && ClickGui.getInstance().colorSync.getValue() && Colors.INSTANCE.rainbow.getValue()) {
            RenderUtil.drawGradientRect((float) x, (float) y - 1.5f, (float) width, (float) (height - 4), HUD.getInstance().colorMap.get(MathUtil.clamp(y, 0, renderer.scaledHeight)), HUD.getInstance().colorMap.get(MathUtil.clamp(y + height - 4, 0, renderer.scaledHeight)));
        } else {
            RenderUtil.drawRect(x, (float) y - 1.5f, x + width, y + height - 6, color);
        }
        if (open) {
            RenderUtil.drawRect(x, (float) y + 12.5f, x + width, (float) (y + height) + totalItemHeight, 0x77000000);
            if (ClickGui.getInstance().outline.getValue()) {
                if (ClickGui.getInstance().rainbow.getValue()) {
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.shadeModel(7425);
                    GL11.glBegin(1);
                    Color currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp(y, 0, renderer.scaledHeight)));
                    GL11.glColor4f((float) currentColor.getRed() / 255.0f, (float) currentColor.getGreen() / 255.0f, (float) currentColor.getBlue() / 255.0f, (float) currentColor.getAlpha() / 255.0f);
                    GL11.glVertex3f((float) (x + width), (float) y - 1.5f, 0.0f);
                    GL11.glVertex3f((float) x, (float) y - 1.5f, 0.0f);
                    GL11.glVertex3f((float) x, (float) y - 1.5f, 0.0f);
                    float currentHeight = (float) getHeight() - 1.5f;
                    for (Item item : getItems()) {
                        currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp((int) ((float) y + (currentHeight += (float) item.getHeight() + 1.5f)), 0, renderer.scaledHeight)));
                        GL11.glColor4f((float) currentColor.getRed() / 255.0f, (float) currentColor.getGreen() / 255.0f, (float) currentColor.getBlue() / 255.0f, (float) currentColor.getAlpha() / 255.0f);
                        GL11.glVertex3f((float) x, (float) y + currentHeight, 0.0f);
                        GL11.glVertex3f((float) x, (float) y + currentHeight, 0.0f);
                    }
                    currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp((int) ((float) (y + height) + totalItemHeight), 0, renderer.scaledHeight)));
                    GL11.glColor4f((float) currentColor.getRed() / 255.0f, (float) currentColor.getGreen() / 255.0f, (float) currentColor.getBlue() / 255.0f, (float) currentColor.getAlpha() / 255.0f);
                    GL11.glVertex3f((float) (x + width), (float) (y + height) + totalItemHeight, 0.0f);
                    GL11.glVertex3f((float) (x + width), (float) (y + height) + totalItemHeight, 0.0f);
                    for (Item item : getItems()) {
                        currentColor = new Color(HUD.getInstance().colorMap.get(MathUtil.clamp((int) ((float) y + (currentHeight -= (float) item.getHeight() + 1.5f)), 0, renderer.scaledHeight)));
                        GL11.glColor4f((float) currentColor.getRed() / 255.0f, (float) currentColor.getGreen() / 255.0f, (float) currentColor.getBlue() / 255.0f, (float) currentColor.getAlpha() / 255.0f);
                        GL11.glVertex3f((float) (x + width), (float) y + currentHeight, 0.0f);
                        GL11.glVertex3f((float) (x + width), (float) y + currentHeight, 0.0f);
                    }
                    GL11.glVertex3f((float) (x + width), (float) y, 0.0f);
                    GL11.glEnd();
                    GlStateManager.shadeModel(7424);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                } else {
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.shadeModel(7425);
                    GL11.glBegin(2);
                    Color outlineColor = ClickGui.getInstance().colorSync.getValue() ? new Color(Colors.INSTANCE.getCurrentColorHex()) : new Color(loveClient.colorManager.getColorAsIntFullAlpha());
                    GL11.glColor4f((float) outlineColor.getRed(), (float) outlineColor.getGreen(), (float) outlineColor.getBlue(), (float) outlineColor.getAlpha());
                    GL11.glVertex3f((float) x, (float) y - 1.5f, 0.0f);
                    GL11.glVertex3f((float) (x + width), (float) y - 1.5f, 0.0f);
                    GL11.glVertex3f((float) (x + width), (float) (y + height) + totalItemHeight, 0.0f);
                    GL11.glVertex3f((float) x, (float) (y + height) + totalItemHeight, 0.0f);
                    GL11.glEnd();
                    GlStateManager.shadeModel(7424);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                }
            }
        }
        loveClient.textManager.drawStringWithShadow(getName(), (float) x + 3.0f, (float) y - 4.0f - (float) PhobosGui.getClickGui().getTextOffset(), -1);
        if (open) {
            float y = (float) (getY() + getHeight()) - 3.0f;
            for (Item item : getItems()) {
                if (item.isHidden()) continue;
                item.setLocation((float) x + 2.0f, y);
                item.setWidth(getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += (float) item.getHeight() + 1.5f;
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            x2 = x - mouseX;
            y2 = y - mouseY;
            PhobosGui.getClickGui().getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
            });
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            open = !open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            return;
        }
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            drag = false;
        }
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button) {
        items.add(button);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isOpen() {
        return open;
    }

    public final ArrayList<Item> getItems() {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0f;
        for (Item item : getItems()) {
            height += (float) item.getHeight() + 1.5f;
        }
        return height;
    }
}


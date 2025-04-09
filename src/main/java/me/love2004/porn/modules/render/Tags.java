package me.love2004.porn.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.love2004.porn.event.events.Render3DEvent;
import me.love2004.porn.command.Command;
import me.love2004.porn.modules.Module;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.loveClient;
import me.love2004.porn.manager.TotemPopManager;
import me.love2004.porn.util.ColorUtil;
import me.love2004.porn.util.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static net.minecraft.client.Minecraft.getMinecraft;

public class Tags extends Module {

    public Tags(){
        super("Nametags","Nametags",Category.RENDER,true,false,false);
        setInstance();
    }

    private static Tags INSTANCE = new Tags();
    private final ICamera camera = new Frustum();
    boolean shownItem;
    private final RenderUtil.RenderUtils renderUtil = new RenderUtil.RenderUtils();

    private final Setting<Boolean> outline = register(new Setting<>("Outline", Boolean.TRUE));
    private final Setting<Boolean> Rainbow = register(new Setting<>("Outline Rainbow", Boolean.FALSE));
    private final Setting<Integer> Oared = register(new Setting<>("Outline Red", 0, 0, 255));
    private final Setting<Integer> Green = register(new Setting<>("Outline Green", 0, 0, 255));
    private final Setting<Integer> Blue = register(new Setting<>("Outline Blue", 0, 0, 255));
    private final Setting<Integer> Alpha = register(new Setting<>("Outline Alpha", 150, 0, 255));
    private final Setting<Float> Width = register(new Setting<>("Outline Width", 1.5f, 0.0f, 3.0f));
    private final Setting<Boolean> reversed = register(new Setting<>("Reversed",Boolean.FALSE));
    private final Setting<Boolean> reversedHand = register(new Setting<>("Reversed Hand", Boolean.FALSE));
    private final Setting<Boolean> cf = register(new Setting<>("Custom Font", Boolean.FALSE));
    private final Setting<Boolean> max = register(new Setting<>("Show Max Enchants", Boolean.TRUE));
    private final Setting<Boolean> maxText = register(new Setting<>("Show Max Text", Boolean.TRUE));
    private final Setting<Boolean> health = register(new Setting<>("Health", Boolean.TRUE));
    private final Setting<Boolean> gameMode = register(new Setting<>("GameMode", Boolean.TRUE));
    private final Setting<Boolean> ping = register(new Setting<>("Ping", Boolean.TRUE));
    private final Setting<Boolean> pingColor = register(new Setting<>("Ping Color", Boolean.TRUE));
    private final Setting<Boolean> armor = register(new Setting<>("Armor", Boolean.TRUE));
    private final Setting<Boolean> durability = register(new Setting<>("Durability", Boolean.TRUE));
    private final Setting<Boolean> item = register(new Setting<>("Item Name", Boolean.TRUE));
    private final Setting<Boolean> invisible = register(new Setting<>("Invisibles", Boolean.FALSE));
    private final Setting<Boolean> pops = register(new Setting<>("Pop Count", Boolean.TRUE));
    private final Setting<Float> scale = register(new Setting<>("Scale", 0.1f, 0.1f, 1f));
    private final Setting<Float> height = register(new Setting<>("Height", 2.5f, 0.5f, 5.0f));

    public static Tags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Tags();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null) {
            return;
        }
        double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) *  event.getPartialTicks();
        double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) *  event.getPartialTicks();
        double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) *  event.getPartialTicks();
        camera.setPosition(d3, d4, d5);
        ArrayList<EntityPlayer> players = new ArrayList<>(mc.world.playerEntities);
        players.sort(Comparator.comparing(entityPlayer -> mc.player.getDistance((Entity) entityPlayer)).reversed());
        for (EntityPlayer entityPlayer2 : players) {
            NetworkPlayerInfo npi = mc.player.connection.getPlayerInfo(entityPlayer2.getGameProfile().getId());
            if (!camera.isBoundingBoxInFrustum(entityPlayer2.getEntityBoundingBox()) || entityPlayer2 == mc.getRenderViewEntity() || !entityPlayer2.isEntityAlive())
                continue;
            double pX = entityPlayer2.lastTickPosX + (entityPlayer2.posX - entityPlayer2.lastTickPosX) *  mc.timer.renderPartialTicks - mc.renderManager.renderPosX;
            double pY = entityPlayer2.lastTickPosY + (entityPlayer2.posY - entityPlayer2.lastTickPosY) *  mc.timer.renderPartialTicks - mc.renderManager.renderPosY;
            double pZ = entityPlayer2.lastTickPosZ + (entityPlayer2.posZ - entityPlayer2.lastTickPosZ) *  mc.timer.renderPartialTicks - mc.renderManager.renderPosZ;
            if (getShortName(npi.getGameType().getName()).equals("SP") && !invisible.getValue() || entityPlayer2.getName().startsWith("Body #"))
                continue;
            renderNametag(entityPlayer2, pX, pY, pZ);
        }

    }

    public void renderNametag(EntityPlayer player, double x, double y, double z) {
        shownItem = false;
        GlStateManager.pushMatrix();
        FontRenderer var13 = getMinecraft().fontRenderer;
        NetworkPlayerInfo npi = mc.player.connection.getPlayerInfo(player.getGameProfile().getId());
        String name = (player.isSneaking() ? Command.SECTIONSIGN() + "9" : Command.SECTIONSIGN() + "r") + getName(player) + (gameMode.getValue() ? " [" + getShortName(npi.getGameType().getName()) + "]" : "") + (ping.getValue() ? " " + (pingColor.getValue() ? Command.SECTIONSIGN() + getPing(npi.getResponseTime()) : "") + npi.getResponseTime() + "ms" : "") + (health.getValue() ? " " + Command.SECTIONSIGN() + getHealth(player.getHealth() + player.getAbsorptionAmount()) + MathHelper.ceil(player.getHealth() + player.getAbsorptionAmount()) : "") + (TotemPopManager.poplist.containsKey(player) && pops.getValue() ? " " + ChatFormatting.DARK_RED + "-" + TotemPopManager.poplist.get(player) : "");
        name = name.replace(".0", "");
        float distance = mc.player.getDistance(player);
        float var15 = (distance / 5.0f <= 2.0f ? 2.0f : distance / 5.0f * (scale.getValue() /10 + 1.0f)) * 2.5f * (scale.getValue() / 20.0f);
        GL11.glTranslated((x), ((y + height.getValue()) - (player.isSneaking() ? 0.4 : 0.0) + (distance / 5.0f > 2.0f ? (distance / 12.0f) - 0.7 : 0.0)), (z));
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef((-mc.renderManager.playerViewY), 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(mc.renderManager.playerViewX, (mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), 0.0f, 0.0f);
        GL11.glScalef((-var15), (-var15), var15);
        renderUtil.disableGlCap(2896, 2929);
        renderUtil.enableGlCap(3042);
        GL11.glBlendFunc(770, 771);
        int width = cf.getValue() ? loveClient.customFont.getStringWidth(name) / 2 + 1 : var13.getStringWidth(name) / 2;
        int color = ColorUtil.Colors.BLACK;
        int outlineColor = new Color(Oared.getValue(), Green.getValue(), Blue.getValue(), Alpha.getValue()).getRGB();
        if (Rainbow.getValue()) {
            outlineColor = ColorUtil.changeAlpha(loveClient.rgb, Alpha.getValue());
        }
        Gui.drawRect((-width - 2), 10, (width + 1), 20, ColorUtil.changeAlpha(color, 120));
        if (outline.getValue()) {
            RenderUtil.drawOutlineLine(-width - 2, 10.0, width + 1, 20.0, Width.getValue(), outlineColor);
        }
        if (cf.getValue()) {
            loveClient.customFont.drawStringWithShadow(name, -width, 11.0, -1);
        } else {
            mc.fontRenderer.drawStringWithShadow(name, (-width), 11.0f, -1);
        }
        if (armor.getValue()) {
            int index;
            int xOffset = -6;
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack == null) continue;
                xOffset -= 8;
                if (armourStack.getItem() == Items.AIR) continue;
                ++count;
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) {
                ++count;
            }
            int cacheX = xOffset - 8;
            xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);
            if (reversedHand.getValue() ? player.getHeldItemOffhand().getItem() != Items.AIR : player.getHeldItemMainhand().getItem() != Items.AIR) {
                ItemStack renderStack;
                xOffset -= 8;
                if (reversedHand.getValue()) {
                    renderStack = player.getHeldItemOffhand().copy();
                    renderItem(renderStack, xOffset, -10, cacheX, false);
                } else {
                    renderStack = player.getHeldItemMainhand().copy();
                    renderItem(renderStack, xOffset, -10, cacheX, true);
                }
                xOffset += 16;
            } else if (!reversedHand.getValue()) {
                shownItem = true;
            }
            if (reversed.getValue()) {
                for (index = 0; index <= 3; ++index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack.getItem() == Items.AIR) continue;
                    ItemStack renderStack1 = armourStack.copy();
                    renderItem(renderStack1, xOffset, -10, cacheX, false);
                    xOffset += 16;
                }
            } else {
                for (index = 3; index >= 0; --index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack.getItem() == Items.AIR) continue;
                    ItemStack renderStack1 = armourStack.copy();
                    renderItem(renderStack1, xOffset, -10, cacheX, false);
                    xOffset += 16;
                }
            }
            if (reversedHand.getValue() ? player.getHeldItemMainhand().getItem() != Items.AIR : player.getHeldItemOffhand().getItem() != Items.AIR) {
                ItemStack renderOffhand;
                if (reversedHand.getValue()) {
                    renderOffhand = player.getHeldItemMainhand().copy();
                    renderItem(renderOffhand, xOffset, -10, cacheX, true);
                } else {
                    renderOffhand = player.getHeldItemOffhand().copy();
                    renderItem(renderOffhand, xOffset, -10, cacheX, false);
                }
            }
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        } else if (durability.getValue()) {
            int xOffset = -6;
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack == null) continue;
                xOffset -= 8;
                if (armourStack.getItem() == Items.AIR) continue;
                ++count;
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) {
                ++count;
            }
            xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);
            if (reversed.getValue()) {
                for (int index = 0; index <= 3; ++index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack.getItem() == Items.AIR) continue;
                    ItemStack renderStack1 = armourStack.copy();
                    renderDurabilityText(renderStack1, xOffset);
                    xOffset += 16;
                }
            } else {
                for (int index = 3; index >= 0; --index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack.getItem() == Items.AIR) continue;
                    ItemStack renderStack1 = armourStack.copy();
                    renderDurabilityText(renderStack1, xOffset);
                    xOffset += 16;
                }
            }
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        }
        renderUtil.resetCaps();
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    public String getPing(float ping) {
        if (ping > 200.0f) {
            return "c";
        }
        if (ping > 100.0f) {
            return "e";
        }
        return "a";
    }

    private void renderDurabilityText(ItemStack stack, int x) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
            o(stack, x, -10);
        }
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    public void renderItem(ItemStack stack, int x, int y, int nameX, boolean showHeldItemText) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        Nametags.mc.getRenderItem().zLevel = -100.0f;
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y / 2 - 12);
        if (durability.getValue()) {
            mc.getRenderItem().renderItemOverlays(Nametags.mc.fontRenderer, stack, x, y / 2 - 12);
        }
        Nametags.mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        renderEnchantText(stack, x, y - 18);
        if (!shownItem && item.getValue() && showHeldItemText) {
            if (cf.getValue()) {
                loveClient.customFont.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - loveClient.customFont.getStringWidth(stack.getDisplayName()) / 2f, y - 37, -1);
            } else {
                Nametags.mc.fontRenderer.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), (float)(nameX * 2 + 95 - Nametags.mc.fontRenderer.getStringWidth(stack.getDisplayName()) / 2), (float)(y - 37), -1);
            }
            shownItem = true;
        }
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    private String getName(EntityPlayer player) {
        return player.getName();
    }

    public String getHealth(float health) {
        if (health > 18.0f) {
            return "a";
        }
        if (health > 16.0f) {
            return "2";
        }
        if (health > 12.0f) {
            return "e";
        }
        if (health > 8.0f) {
            return "6";
        }
        if (health > 5.0f) {
            return "c";
        }
        return "4";
    }

    public void renderEnchantText(ItemStack stack, int x, int y) {
        int yCount = y;
        if ((stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) && durability.getValue()) {
            o(stack, x, y);
        }
        if (max.getValue() && isMaxEnchants(stack)) {
            GL11.glPushMatrix();
            GL11.glScalef(1.0f, 1.0f, 0.0f);
            if (maxText.getValue()) {
                if (cf.getValue()) {
                    loveClient.customFont.drawStringWithShadow("Max", x * 2 + 7, yCount + 24, ColorUtil.Colors.RED);
                } else {
                    Nametags.mc.fontRenderer.drawStringWithShadow("Max", (x * 2 + 7), (yCount + 24), ColorUtil.Colors.RED);
                }
            }
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
            return;
        }
        NBTTagList enchants = stack.getEnchantmentTagList();
        for (int index = 0; index < enchants.tagCount(); ++index) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.getEnchantmentByID(id);
            if (enc == null || enc.isCurse()) continue;
            String encName = level == 1 ? enc.getTranslatedName(level).substring(0, 3).toLowerCase() : enc.getTranslatedName(level).substring(0, 2).toLowerCase() + level;
            encName = encName.substring(0, 1).toUpperCase() + encName.substring(1);
            GL11.glPushMatrix();
            GL11.glScalef(1.0f, 1.0f, 0.0f);
            if (cf.getValue()) {
                loveClient.customFont.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
            } else {
                Nametags.mc.fontRenderer.drawStringWithShadow(encName, (x * 2 + 3), yCount, -1);
            }
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
            yCount += 8;
        }
    }

    private void o(ItemStack stack, int x, int y) {
        float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
        float red = 1.0f - green;
        int dmg = 100 - (int)(red * 100.0f);
        if (cf.getValue()) {
            loveClient.customFont.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10, ColorUtil.ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
        } else {
            Nametags.mc.fontRenderer.drawStringWithShadow(dmg + "%", (float)(x * 2 + 4), (float)(y - 10), ColorUtil.ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
    }

    public boolean isMaxEnchants(ItemStack stack) {
        NBTTagList enchants = stack.getEnchantmentTagList();
        ArrayList<String> enchantments = new ArrayList<>();
        int count = 0;
        int maxnum;
        for (int index = 0; index < enchants.tagCount(); ++index) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.getEnchantmentByID(id);
            if (enc == null) continue;
            enchantments.add(enc.getTranslatedName(level));
        }
        if (stack.getItem() == Items.DIAMOND_HELMET) {
            maxnum = 5;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Respiration III")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Aqua Affinity")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= maxnum;
        }
        if (stack.getItem() == Items.DIAMOND_CHESTPLATE) {
            maxnum = 3;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= maxnum;
        }
        if (stack.getItem() == Items.DIAMOND_LEGGINGS) {
            maxnum = 3;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Blast Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= maxnum;
        }
        if (stack.getItem() == Items.DIAMOND_BOOTS) {
            maxnum = 5;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Feather Falling IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Depth Strider III")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= maxnum;
        }
        return false;
    }

    public String getShortName(String gameType) {
        if (gameType.equals("survival")) {
            return "S";
        }
        if (gameType.equals("creative")) {
            return "C";
        }
        if (gameType.equals("adventure")) {
            return "A";
        }
        if (gameType.equals("spectator")) {
            return "SP";
        }
        return "NONE";
    }

}

package me.love2004.porn.modules.combat;

import me.love2004.porn.modules.Module;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.util.BlockUtil;
import me.love2004.porn.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import scala.Int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trapper extends Module
{
    private final Setting<Boolean> disable;
    private final Setting<Integer> speed;
    private final ArrayList<BlockPos> renderBlocks;
    private int ticksOn;
    
    public Trapper() {
        super("Trapper", "", Category.COMBAT,true,false,false);
        this.disable = register(new Setting<>("Disable", true));
        this.speed = register(new Setting<>("Speed", 3, 1, 30));
        this.renderBlocks = new ArrayList<>();
    }
    
    @Override
    public void onEnable() {
        this.ticksOn = 0;
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        final List<BlockPos> blocks = new ArrayList<BlockPos>(Arrays.asList(new BlockPos(this.mc.player.getPositionVector()).add(2, 0, 0), new BlockPos(this.mc.player.getPositionVector()).add(-2, 0, 0), new BlockPos(this.mc.player.getPositionVector()).add(0, 0, 2), new BlockPos(this.mc.player.getPositionVector()).add(0, 0, -2), new BlockPos(this.mc.player.getPositionVector()).add(2, 0, 1), new BlockPos(this.mc.player.getPositionVector()).add(-2, 0, 1), new BlockPos(this.mc.player.getPositionVector()).add(1, 0, 2), new BlockPos(this.mc.player.getPositionVector()).add(1, 0, -2), new BlockPos(this.mc.player.getPositionVector()).add(2, 0, -1), new BlockPos(this.mc.player.getPositionVector()).add(-2, 0, -1), new BlockPos(this.mc.player.getPositionVector()).add(-1, 0, 2), new BlockPos(this.mc.player.getPositionVector()).add(-1, 0, -2), new BlockPos(this.mc.player.getPositionVector()).add(0, -1, 0), new BlockPos(this.mc.player.getPositionVector()).add(1, -1, 0), new BlockPos(this.mc.player.getPositionVector()).add(-1, -1, 0), new BlockPos(this.mc.player.getPositionVector()).add(1, -1, 1), new BlockPos(this.mc.player.getPositionVector()).add(1, -1, -1), new BlockPos(this.mc.player.getPositionVector()).add(-1, -1, 1), new BlockPos(this.mc.player.getPositionVector()).add(-1, -1, -1)));
        this.renderBlocks.clear();
        for (final Object bP : new ArrayList<Object>(blocks)) {
            final BlockPos block = (BlockPos)bP;
            blocks.add(0, block.down());
            if (this.mc.world.getBlockState(block).getBlock().equals(Blocks.AIR)) {
                this.renderBlocks.add(block);
            }
        }
        final int slot = this.getObsidianSlot();
        if (slot != -1) {
            if (this.disable.getValue()) {
                ++this.ticksOn;
            }
            int i = 0;
            final int hand = this.mc.player.inventory.currentItem;
            for (final BlockPos blockPos : blocks) {
                if (BlockUtil.placeBlock(blockPos, slot, true, false)) {
                    ++i;
                }
                final int BPT = Math.round(this.speed.getValue() / 10.0f) + 1;
                if (i >= BPT) {
                    break;
                }
            }
            this.mc.player.inventory.currentItem = hand;
            if (this.ticksOn > 30) {
                if (this.disable.getValue()) {
                    this.disable();
                }
                this.renderBlocks.clear();
            }
        }
        else {
            if (this.disable.getValue()) {
                this.disable();
            }
            this.renderBlocks.clear();
        }
    }
    
    public int getObsidianSlot() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = this.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        for (final BlockPos renderBlock : this.renderBlocks) {
            RenderUtil.drawBoxFromBlockpos(renderBlock, 0.5f, 0.0f, 0.0f, 0.3f);
        }
    }
}

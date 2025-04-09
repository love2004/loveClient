package me.love2004.porn.manager;

import me.love2004.porn.util.Util;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmeticsManager
        implements Util {
    public Map<String, List<ModelBase>> cosmeticsUserMap = new HashMap<String, List<ModelBase>>();

    public CosmeticsManager() {
    }

    public List<ModelBase> getRenderModels(EntityPlayer player) {
        return this.cosmeticsUserMap.get(player.getUniqueID().toString());
    }

    public boolean hasCosmetics(EntityPlayer player) {
        return this.cosmeticsUserMap.containsKey(player.getUniqueID().toString());
    }
}


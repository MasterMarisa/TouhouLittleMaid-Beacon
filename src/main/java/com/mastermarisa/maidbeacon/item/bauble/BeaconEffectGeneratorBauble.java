package com.mastermarisa.maidbeacon.item.bauble;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.mastermarisa.maidbeacon.config.Config;
import com.mastermarisa.maidbeacon.logic.AuraApplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class BeaconEffectGeneratorBauble implements IMaidBauble {
    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide()) {
            return;
        }
        if (maid.level().getGameTime() % Config.checkInterval() != 0) {
            return;
        }
        if (maid.level() instanceof ServerLevel serverLevel) {
            AuraApplier.apply(serverLevel, maid, baubleItem);
        }
    }

    @Override
    public void onPutOn(EntityMaid maid, ItemStack baubleItem) {
        BaubleItemHandler handler = maid.getMaidBauble();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getBaubleInSlot(i) instanceof BeaconEffectGeneratorBauble
                    && !handler.getStackInSlot(i).equals(baubleItem)) {
                ItemStack toDrop = handler.extractItem(i, 1, false);
                ItemEntity entity = new ItemEntity(maid.level(), maid.getX(), maid.getY(), maid.getZ(), toDrop);
                entity.setDefaultPickUpDelay();
                maid.level().addFreshEntity(entity);
            }
        }
    }

    @Override
    public boolean syncClient(EntityMaid maid, ItemStack baubleItem) {
        return true;
    }
}


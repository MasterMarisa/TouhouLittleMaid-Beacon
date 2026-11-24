package com.mastermarisa.maidbeacon.utils;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.mastermarisa.maidbeacon.item.bauble.BeaconEffectGeneratorBauble;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class MaidUtils {
    public static ItemStack getEquippedMobileBeacon(EntityMaid maid) {
        BaubleItemHandler handler = maid.getMaidBauble();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getBaubleInSlot(i) instanceof BeaconEffectGeneratorBauble) {
                return handler.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> tryExtract(IItemHandler handler, int count, Predicate<ItemStack> filter, boolean strict) {
        List<ItemStack> extracted = new ArrayList<>();
        if (strict && count(handler, filter) < count) {
            return extracted;
        }

        int taken = 0;
        for (int slot : findStackSlots(handler, filter)) {
            if (taken >= count) {
                break;
            }
            ItemStack stack = handler.getStackInSlot(slot);
            int wanted = Math.min(count - taken, stack.getCount());
            ItemStack removed = handler.extractItem(slot, wanted, false);
            taken += removed.getCount();
            if (!removed.isEmpty()) {
                extracted.add(removed);
            }
        }
        return extracted;
    }

    public static List<Integer> findStackSlots(IItemHandler handler, Predicate<ItemStack> filter) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (filter.test(handler.getStackInSlot(i))) {
                slots.add(i);
            }
        }
        return slots;
    }

    public static int count(IItemHandler handler, Predicate<ItemStack> predicate) {
        int total = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (predicate.test(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private MaidUtils() {
    }
}


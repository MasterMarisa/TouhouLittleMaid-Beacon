package com.mastermarisa.maidbeacon.item;

import com.mastermarisa.maidbeacon.client.screen.BeaconScreen;
import com.mastermarisa.maidbeacon.content.BeaconComponent;
import com.mastermarisa.maidbeacon.logic.BeaconState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class MobileBeaconItem extends Item {
    public MobileBeaconItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack beacon = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            BeaconScreen.open(usedHand);
        }
        return InteractionResultHolder.sidedSuccess(beacon, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BeaconState state = stack.getOrDefault(BeaconComponent.BEACON_STATE.get(), BeaconState.EMPTY);
        tooltip.add(Component.translatable("tooltip.maidbeacon.level", state.level()).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.maidbeacon.active_auras", state.auras().size())
                .withStyle(ChatFormatting.GRAY));
    }
}


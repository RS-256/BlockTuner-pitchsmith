/*
 *     Copyright (c) 2023, xwjcool.
 *     Copyright (c) 2025, Lumine1909.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rs256.blocktuner.mixin;

import com.rs256.blocktuner.BlockTunerConfig;
import com.rs256.blocktuner.display.TuningScreen;
import com.rs256.blocktuner.util.InputUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@NotNullByDefault
@Mixin(NoteBlock.class)
public class ClientNoteBlockMixin extends Block {

    public ClientNoteBlockMixin(Properties properties) {
        super(properties);
    }

    @Unique
    private static void copyBlockState(BlockState state, net.minecraft.world.item.ItemStack stack) {
        stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(NoteBlock.NOTE, state));
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean bl) {
        ItemStack stack = super.getCloneItemStack(levelReader, blockPos, blockState, bl);
        if (!BlockTunerConfig.isRequireCtrlToCopyPitch() || InputUtil.isCtrlDown()) {
            copyBlockState(blockState, stack);
        }
        return stack;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        Minecraft client = Minecraft.getInstance();
        boolean openGuiModifierSatisfied = !BlockTunerConfig.isRequireCtrlToOpenGui() || InputUtil.isCtrlDown();
        if (livingEntity != null && livingEntity.equals(client.player) && openGuiModifierSatisfied && TuningScreen.shouldOpenGui(itemStack)) {
            //? if <26.2 {
            /*client.execute(() -> client.setScreen(new TuningScreen(Component.empty(), blockPos)));
            *///?} else {
            client.execute(() -> client.gui.setScreen(new TuningScreen(Component.empty(), blockPos)));
            //?}
        }
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
    }
}

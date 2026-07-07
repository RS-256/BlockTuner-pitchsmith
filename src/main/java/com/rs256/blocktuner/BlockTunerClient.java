/*
 *     Copyright (c) 2021, xwjcool.
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

package com.rs256.blocktuner;

import com.rs256.blocktuner.display.TuningScreen;
import com.rs256.blocktuner.network.ClientBoundHelloPacket;
import com.rs256.blocktuner.network.ServerBoundHelloPacket;
import com.rs256.blocktuner.util.InputUtil;
import com.rs256.blocktuner.util.MidiManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static com.rs256.blocktuner.BlockTuner.TUNING_PROTOCOL;

@Environment(EnvType.CLIENT)
public class BlockTunerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockTunerConfig.load();
        MidiManager.getMidiManager().refreshMidiDevice();
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (BlockTunerConfig.onBlockTunerServer
                && (!BlockTunerConfig.isRequireCtrlToOpenGui() || InputUtil.isCtrlDown())
                && !player.isSpectator()
                && !player.isShiftKeyDown()
                && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.NOTE_BLOCK
                && !shouldDeferToWorldEdit(player.getItemInHand(hand))
                && player.getMainHandItem().getItem() != Items.BLAZE_ROD) {
                Minecraft client = Minecraft.getInstance();
                client.execute(() -> client.setScreen(new TuningScreen(Component.empty(), hitResult.getBlockPos())));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
        ClientPlayNetworking.registerGlobalReceiver(ClientBoundHelloPacket.TYPE, ClientBoundHelloPacket::receive);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> sender.sendPacket(new ServerBoundHelloPacket(TUNING_PROTOCOL)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> BlockTunerConfig.onBlockTunerServer = false);
    }

    private static boolean shouldDeferToWorldEdit(ItemStack stack) {
        return FabricLoader.getInstance().isModLoaded("worldedit") && stack.getItem() == Items.WOODEN_AXE;
    }
}
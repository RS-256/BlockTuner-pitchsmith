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

import com.rs256.blocktuner.network.ClientBoundHelloPacket;
import com.rs256.blocktuner.network.ServerBoundHelloPacket;
import com.rs256.blocktuner.network.ServerBoundTuningPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockTuner implements ModInitializer {

    public static final String MOD_ID = "blocktuner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int TUNING_PROTOCOL = 3;

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[BlockTuner] Now Loading BlockTuner!");
        CommandRegistrationCallback.EVENT.register(BlockTunerCommands::register);
        //? if <26.1 {
        /*PayloadTypeRegistry.playS2C().register(ClientBoundHelloPacket.TYPE, ClientBoundHelloPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerBoundHelloPacket.TYPE, ServerBoundHelloPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerBoundTuningPacket.TYPE, ServerBoundTuningPacket.CODEC);
        *///?} else {
        PayloadTypeRegistry.clientboundPlay().register(ClientBoundHelloPacket.TYPE, ClientBoundHelloPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerBoundHelloPacket.TYPE, ServerBoundHelloPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerBoundTuningPacket.TYPE, ServerBoundTuningPacket.CODEC);
        //?}
        ServerPlayNetworking.registerGlobalReceiver(ServerBoundTuningPacket.TYPE, ServerBoundTuningPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ServerBoundHelloPacket.TYPE, ServerBoundHelloPacket::receive);
    }
}

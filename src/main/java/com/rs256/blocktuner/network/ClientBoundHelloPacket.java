/*
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
package com.rs256.blocktuner.network;

import com.rs256.blocktuner.BlockTuner;
import com.rs256.blocktuner.BlockTunerConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNullByDefault;

import static com.rs256.blocktuner.BlockTuner.id;

@NotNullByDefault
public record ClientBoundHelloPacket(int protocolVersion) implements CustomPacketPayload {

    public static final Identifier ID = id("client_bound_hello");
    public static final CustomPacketPayload.Type<ClientBoundHelloPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundHelloPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        ClientBoundHelloPacket::protocolVersion,
        ClientBoundHelloPacket::new
    );

    public static void receive(ClientBoundHelloPacket payload, ClientPlayNetworking.Context context) {
        if (BlockTuner.TUNING_PROTOCOL == payload.protocolVersion()) {
            Minecraft.getInstance().execute(() -> BlockTunerConfig.onBlockTunerServer = true);
            //? if >=26.1 {
            context.player().sendSystemMessage(Component.translatable("blocktuner.available"));
            //?} else {
            /*context.player().displayClientMessage(Component.translatable("blocktuner.available"), false);
            *///?}
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

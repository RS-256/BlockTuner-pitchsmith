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

package com.rs256.blocktuner;

import com.rs256.blocktuner.mixin.NoteBlockInvoker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockTunerCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(
            Commands.literal("tune")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(Commands.argument("note", IntegerArgumentType.integer(0, 24))
                        .executes(context -> tune(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "pos"), IntegerArgumentType.getInteger(context, "note"))))));
    }

    private static int tune(CommandSourceStack source, BlockPos pos, int note) {
        ServerLevel world = source.getLevel();
        if (world.getBlockState(pos).getBlock() != Blocks.NOTE_BLOCK || !source.getPosition().closerThan(Vec3.atCenterOf(pos), 5.0d)) {
            return -1;
        }
        world.setBlock(pos, world.getBlockState(pos).setValue(NoteBlock.NOTE, note), 2 | 16);
        BlockState state = world.getBlockState(pos);
        // please do not change this to world.addSyncedBlockEvent() as it does not allow chords to be played. <- more suitable method; NoteBlock#playNote
        if (world.getBlockState(pos.above()).isAir()) {
            ((NoteBlockInvoker) state.getBlock()).blocktuner$playNote(source.getEntity(), state, world, pos);
        }
        return note;
    }
}

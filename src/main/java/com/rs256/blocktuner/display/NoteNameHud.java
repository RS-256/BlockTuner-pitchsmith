/*
 *     Copyright (c) 2022, xwjcool.
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

package com.rs256.blocktuner.display;

import com.rs256.blocktuner.util.InputUtil;
import com.rs256.blocktuner.util.NoteNames;
import net.minecraft.client.Minecraft;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class NoteNameHud {

    //? if >=26.1 {
    public static void extractRenderState(GuiGraphicsExtractor graphics) {
    //?} else {
    /*public static void render(GuiGraphics graphics) {
    *///?}
        Minecraft client = Minecraft.getInstance();
        assert client.level != null;
        assert client.player != null;
        if (InputUtil.isCtrlDown() && !client.player.isSpectator()) {
            HitResult hitResult = client.hitResult;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState state = client.level.getBlockState(blockPos);
                if (state.getBlock() == Blocks.NOTE_BLOCK) {
                    int note = state.getValue(NoteBlock.NOTE);
                    int x = client.getWindow().getGuiScaledWidth() / 2 + 4;
                    int y = client.getWindow().getGuiScaledHeight() / 2 + 4;
                    //? if >=26.1 {
                    graphics.textRenderer().accept(
                        TextAlignment.LEFT,
                        x,
                        y,
                        Component.literal(NoteNames.get(note) + ", " + note).withColor(0x66ccff)
                    );
                    //?} else {
                    /*graphics.drawString(client.font, NoteNames.get(note) + ", " + note, x, y, 0xff66ccff);
                    *///?}
                }
            }
        }
    }
}
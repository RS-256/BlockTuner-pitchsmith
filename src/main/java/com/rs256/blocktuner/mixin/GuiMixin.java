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

package com.rs256.blocktuner.mixin;

import com.rs256.blocktuner.display.NoteNameHud;
import net.minecraft.client.DeltaTracker;
//? if <26.2 {
/*import net.minecraft.client.gui.Gui;
*///?} else {
import net.minecraft.client.gui.Hud;
//?}
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 26.2 moved the HUD rendering out of Gui into Hud, keeping the same signature.
//? if <26.2 {
/*@Mixin(Gui.class)
*///?} else {
@Mixin(Hud.class)
//?}
public class GuiMixin {

    //? if <26.1 {
    /*@Inject(method = "render", at = @At("TAIL"))
    private void renderNoteNameHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        NoteNameHud.render(guiGraphics);
    }
    *///?} else {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderNoteNameHud(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        NoteNameHud.extractRenderState(guiGraphics);
    }
    //?}
}

package com.rs256.blocktuner.mixin;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.server.commands.PlaySoundCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlaySoundCommand.class)
public class PlaySoundCommandMixin {

    @Redirect(
        method = "source",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/arguments/FloatArgumentType;floatArg(FF)Lcom/mojang/brigadier/arguments/FloatArgumentType;",
            ordinal = 0,
            remap = false
        )
    )
    private static FloatArgumentType source(float min, float max) {
        return FloatArgumentType.floatArg(0);
    }
}

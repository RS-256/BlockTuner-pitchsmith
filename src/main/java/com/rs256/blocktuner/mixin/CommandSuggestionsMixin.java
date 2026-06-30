package com.rs256.blocktuner.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.suggestion.Suggestion;
import com.rs256.blocktuner.util.InputUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(CommandSuggestions.SuggestionsList.class)
public abstract class CommandSuggestionsMixin {

    @Final
    @Shadow(aliases = {"this$0", "field_21615"})
    CommandSuggestions commandSuggestions;
    @Shadow
    boolean tabCycles;
    @Shadow
    @Final
    private List<Suggestion> suggestionList;
    @Shadow
    private int current;

    @Shadow
    public abstract void cycle(int i);

    @Inject(
        method = "useSuggestion",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/components/CommandSuggestions;keepSuggestions:Z",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void injected(CallbackInfo ci, @Local LocalRef<Suggestion> suggestion) {
        if (InputUtil.isCtrlDown()) {
            if (this.tabCycles) {
                this.cycle(InputUtil.isShiftDown() ? 1 : -1);
                suggestion.set(suggestionList.get(this.current));
            }
            commandSuggestions.keepSuggestions = false;
        }
    }
}
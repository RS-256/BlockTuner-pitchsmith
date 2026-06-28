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

package com.rs256.blocktuner.display;

import com.rs256.blocktuner.BlockTuner;
import com.rs256.blocktuner.BlockTunerConfig;
import com.rs256.blocktuner.network.ServerBoundTuningPacket;
import com.rs256.blocktuner.util.MidiManager;
import com.rs256.blocktuner.util.NoteNames;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import static com.rs256.blocktuner.util.InputUtil.DUMMY_EVENT;

@NotNullByDefault
@Environment(EnvType.CLIENT)
public class TuningScreen extends Screen {

    protected static final Component PLAY_MODE_TOGGLE_TOOLTIP = Component.translatable("settings.blocktuner.play_mode");
    protected static final Component KEY_TO_PIANO_TOGGLE_TOOLTIP = Component.translatable("settings.blocktuner.key_to_piano");
    protected static final Component EMPTY_MIDI_DEVICE = Component.translatable("midi_device.empty");
    protected static final Component MIDI_DEVICE_REFRESH_TOOLTIP = Component.translatable("settings.blocktuner.refresh");
    static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("blocktuner", "textures/gui/container/tune.png");
    private final BlockPos pos;
    private final PianoKeyWidget[] pianoKeys = new PianoKeyWidget[25];
    private final MidiManager midiManager;
    private final MidiReceiver receiver;
    protected int backgroundWidth = 256;
    protected int backgroundHeight = 112;
    protected int x;
    protected int y;
    private @Nullable PianoKeyWidget pressedKey = null;
    private @Nullable MidiDevice currentDevice;
    private @Nullable Component deviceName;
    private boolean deviceAvailable = true;
    private boolean configChanged = false;

    public TuningScreen(Component title, BlockPos pos) {
        super(title);
        this.pos = pos;

        midiManager = MidiManager.getMidiManager();
        currentDevice = midiManager.getCurrentDevice();
        receiver = new MidiReceiver();
    }

    public static void sendTuningPacket(BlockPos pos, int note) {
        note = Mth.clamp(note, 0, 24);
        ClientPlayNetworking.send(new ServerBoundTuningPacket(pos, note));
    }

    protected static int keyToNote(int scanCode) {
        return switch (scanCode) {
            case 3, 38 -> 7;
            case 4, 39 -> 9;
            case 6 -> 12;
            case 7 -> 14;
            case 8 -> 16;
            case 10 -> 19;
            case 11 -> 21;
            case 13 -> 24;
            case 16, 51 -> 6;
            case 17, 52 -> 8;
            case 18, 53 -> 10;
            case 19 -> 11;
            case 20 -> 13;
            case 21 -> 15;
            case 22 -> 17;
            case 23 -> 18;
            case 24 -> 20;
            case 25 -> 22;
            case 26 -> 23;
            case 34 -> 0;
            case 35 -> 2;
            case 36 -> 4;
            case 48 -> 1;
            case 49 -> 3;
            case 50 -> 5;
            default -> -1;
        };
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        // Fancy(?) keyboard

        this.addRenderableWidget(new WhiteKeyWidget(this.x + 16, this.y + 65, 1, 1));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 32, this.y + 65, 3, 1));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 48, this.y + 65, 5, 2));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 64, this.y + 65, 6, 0));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 80, this.y + 65, 8, 1));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 96, this.y + 65, 10, 2));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 112, this.y + 65, 11, 0));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 128, this.y + 65, 13, 1));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 144, this.y + 65, 15, 1));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 160, this.y + 65, 17, 2));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 176, this.y + 65, 18, 0));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 192, this.y + 65, 20, 1));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 208, this.y + 65, 22, 2));
        this.addRenderableWidget(new WhiteKeyWidget(this.x + 224, this.y + 65, 23, 0));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 8, this.y + 40, 0));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 24, this.y + 40, 2));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 40, this.y + 40, 4));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 72, this.y + 40, 7));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 88, this.y + 40, 9));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 120, this.y + 40, 12));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 136, this.y + 40, 14));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 152, this.y + 40, 16));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 184, this.y + 40, 19));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 200, this.y + 40, 21));
        this.addRenderableWidget(new BlackKeyWidget(this.x + 232, this.y + 40, 24));

        this.addRenderableWidget(new PlayModeToggle(this.x + 184, this.y + 8));
        this.addRenderableWidget(new KeyToPianoToggle(this.x + 200, this.y + 8));
        this.addRenderableWidget(new MidiSwitch(this.x + 216, this.y + 8));
        this.addRenderableWidget(new MidiDeviceRefreshButton(this.x + 232, this.y + 8));

        this.addRenderableOnly(new KeySignature(this.x + 112, this.y + 8));
        this.addRenderableWidget(new KeyAddSharpButton(this.x + 144, this.y + 8));
        this.addRenderableWidget(new KeyAddFlatButton(this.x + 144, this.y + 16));

        if (currentDevice != null && !currentDevice.isOpen()) {
            openCurrentDevice();
        }
    }

    @Override
    public void tick() {
        if (minecraft.level == null || minecraft.level.getBlockState(pos).getBlock() != Blocks.NOTE_BLOCK) {
            this.close();
        }
    }

    //? if <26.1 {
    /*@Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
    }
    *///?}

    //? if >=26.1 {
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        this.drawBackground(graphics);
    }

    protected void drawBackground(GuiGraphicsExtractor graphics) {
    //?} else {
    /*@Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderBackground(graphics, mouseX, mouseY, delta);
        this.drawBackground(graphics);
    }

    protected void drawBackground(GuiGraphics graphics) {
    *///?}
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.setDragging(false);
        if (pressedKey != null) {
            return pressedKey.mouseReleased(event);
        } else {
            return super.mouseReleased(event);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (BlockTunerConfig.isKeyToPiano() && event.key() != 256) {
            int note = keyToNote(event.scancode());
            if (note >= 0 && note <= 24 && !pianoKeys[note].played) {
                pianoKeys[note].onClick(DUMMY_EVENT, false);
            }
            return true;
        } else {
            if (this.minecraft.options.keyInventory.matches(event)) {
                this.close();
                return true;
            }
            return super.keyPressed(event);
        }
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        int note = keyToNote(event.scancode());
        if (note >= 0 && note <= 24) {
            pianoKeys[note].onRelease(DUMMY_EVENT);
        }
        return super.keyReleased(event);
    }

    public void close() {
        if (currentDevice != null && currentDevice.isOpen()) {
            currentDevice.close();
        }
        receiver.close();
        if (configChanged) {
            BlockTunerConfig.save();
        }
        super.onClose();
    }

    protected void openCurrentDevice() {
        if (currentDevice == null) {
            return;
        }
        try {
            currentDevice.open();
            deviceAvailable = true;
            currentDevice.getTransmitter().setReceiver(receiver);
        } catch (MidiUnavailableException e) {
            deviceAvailable = false;
            BlockTuner.LOGGER.info("[BlockTuner] MIDI device \"{}\" is currently unavailable. Is it busy or unplugged?", currentDevice.getDeviceInfo().getName());
        }
    }

    private static boolean hasNoteBlockState(ItemStack stack) {
        if (!stack.has(DataComponents.BLOCK_STATE)) {
            return false;
        }

        var blockState = stack.get(DataComponents.BLOCK_STATE);
        if (blockState == null) {
            return false;
        }

        return blockState.properties().containsKey("note");
    }

    public static boolean shouldOpenGui(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return true;
        if (!(blockItem.getBlock() instanceof NoteBlock)) return true;

        return !hasNoteBlockState(stack);
    }


    static class KeySignature implements Renderable {

        public int x;
        public int y;

        public KeySignature(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        //? if >=26.1 {
        public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        //?} else {
        /*public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        *///?}
            int keySignature = BlockTunerConfig.getKeySignature();
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.x, this.y, (keySignature + 8) % 8 * 32, (float) (keySignature + 8) / 8 * 16 + 224, 32, 16, 256, 256);
        }
    }

    abstract class PianoKeyWidget extends AbstractWidget {

        private final int note;
        protected boolean played;

        protected PianoKeyWidget(int x, int y, int width, int height, int note) {
            super(x, y, width, height, Component.empty());
            this.note = note;
            pianoKeys[note] = this;
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            if (!this.visible) {
                return;
            }
            if (this.isHovered) {
                graphics.setTooltipForNextFrame(TuningScreen.this.font, Component.literal(NoteNames.get(note)), TuningScreen.this.x - 8, TuningScreen.this.y - 2);
            }
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
            pressedKey = this;
            played = true;

            if (minecraft.player != null && minecraft.getConnection() != null) {
                sendTuningPacket(pos, note);
                minecraft.player.swing(InteractionHand.MAIN_HAND);
            }

            if (!BlockTunerConfig.isPlayMode()) {
                close();
            }
        }

        @Override
        public void onRelease(MouseButtonEvent mouseButtonEvent) {
            played = false;
            pressedKey = null;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
            if (this.active && this.visible) {
                if (this.isValidClickButton(event.buttonInfo())) {
                    boolean b = this.isMouseOver(event.x(), event.y());
                    if (b) {
                        this.onClick(event, bl);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean isMouseOver(double d, double e) {
            return this.active && this.visible && this.isHovered;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    class BlackKeyWidget extends PianoKeyWidget {

        public BlackKeyWidget(int x, int y, int note) {
            super(x, y, 16, 38, note);
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (played) {
                status = 1;
            } else if (this.isHovered()) {
                status = 2;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 16 * status, 112, 16, 38, 256, 256);
        }

    }

    class WhiteKeyWidget extends PianoKeyWidget {

        private final int keyShape;

        public WhiteKeyWidget(int x, int y, int note, int keyShape) {
            super(x, y, 16, 38, note);
            this.keyShape = keyShape;
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            boolean mask = mouseX >= this.getX() + 8 - 8 * keyShape && mouseY >= this.getY() && mouseX < this.getX() + 24 - 8 * keyShape && mouseY < this.getY() + 13;
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            this.isHovered = this.isHovered && !mask;

            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (played) {
                status = 1;
            } else if (this.isHovered()) {
                status = 2;
            }

            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 16 * status + 48 * keyShape + 48, 112, 16, 38, 256, 256);
        }

    }

    class PlayModeToggle extends AbstractWidget {

        public PlayModeToggle(int x, int y) {
            super(x, y, 16, 16, Component.empty());
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (BlockTunerConfig.isPlayMode()) {
                status = 2;
            }
            if (this.isHovered()) {
                status += 1;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 192 + 16 * status, 112, 16, 16, 256, 256);
            if (this.isHovered()) {
                graphics.setTooltipForNextFrame(TuningScreen.this.font, PLAY_MODE_TOGGLE_TOOLTIP, TuningScreen.this.x - 8, TuningScreen.this.y - 2);
            }
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
            BlockTunerConfig.togglePlayMode();
            configChanged = true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    class KeyToPianoToggle extends AbstractWidget {

        public KeyToPianoToggle(int x, int y) {
            super(x, y, 16, 16, Component.empty());
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (BlockTunerConfig.isKeyToPiano()) {
                status = 2;
            }
            if (this.isHovered()) {
                status += 1;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 192 + 16 * status, 128, 16, 16, 256, 256);
            if (this.isHovered()) {
                graphics.setTooltipForNextFrame(TuningScreen.this.font, KEY_TO_PIANO_TOGGLE_TOOLTIP, TuningScreen.this.x - 8, TuningScreen.this.y - 2);
            }
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
            BlockTunerConfig.toggleKeyToPiano();
            configChanged = true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    class MidiSwitch extends AbstractWidget {

        public MidiSwitch(int x, int y) {
            super(x, y, 16, 16, Component.empty());
            if (currentDevice == null) {
                deviceName = EMPTY_MIDI_DEVICE;
            } else {
                deviceName = Component.literal(currentDevice.getDeviceInfo().getName());
            }
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (midiManager.getDeviceIndex() > 0) {
                status = 2;
            }
            if (this.isHovered()) {
                status += 1;
            }
            if (!deviceAvailable) {
                status += 4;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 192 + 16 * (status % 4), 144 + 16 * ((float) status / 4), 16, 16, 256, 256);
            if (this.isHovered() && deviceName != null) {
                graphics.setTooltipForNextFrame(TuningScreen.this.font, Component.translatable("settings.blocktuner.midi_device", deviceName), TuningScreen.this.x - 8, TuningScreen.this.y - 2);
            }
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {

            if (currentDevice != null && currentDevice.isOpen()) {
                currentDevice.close();
            }

            midiManager.loopDeviceIndex();
            currentDevice = midiManager.getCurrentDevice();

            if (currentDevice != null) {
                BlockTunerConfig.setMidiDeviceName(currentDevice.getDeviceInfo().getName());
                deviceName = Component.literal(BlockTunerConfig.getMidiDeviceName());
                openCurrentDevice();
            } else {
                BlockTunerConfig.setMidiDeviceName("");
                deviceName = EMPTY_MIDI_DEVICE;
            }
            configChanged = true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    class MidiDeviceRefreshButton extends AbstractWidget {

        public MidiDeviceRefreshButton(int x, int y) {
            super(x, y, 16, 16, Component.empty());
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (this.isHovered()) {
                status += 1;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 192 + 16 * status, 176, 16, 16, 256, 256);
            if (this.isHovered()) {
                graphics.setTooltipForNextFrame(TuningScreen.this.font, MIDI_DEVICE_REFRESH_TOOLTIP, TuningScreen.this.x - 8, TuningScreen.this.y - 2);
            }
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
            midiManager.refreshMidiDevice();
            if (currentDevice != null) {
                openCurrentDevice();
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }

    class KeyAddSharpButton extends AbstractWidget {

        public KeyAddSharpButton(int x, int y) {
            super(x, y, 8, 8, Component.empty());
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (this.isHovered()) {
                status += 1;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 8 * status, 152, 8, 8, 256, 256);
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
            BlockTunerConfig.keyAddSharp();
            configChanged = true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

    }

    class KeyAddFlatButton extends AbstractWidget {

        public KeyAddFlatButton(int x, int y) {
            super(x, y, 8, 8, Component.empty());
        }

        @Override
        //? if >=26.1 {
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //?} else {
        /*public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        *///?}
            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int status = 0;
            if (this.isHovered()) {
                status += 1;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 8 * status + 16, 152, 8, 8, 256, 256);
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
            BlockTunerConfig.keyAddFlat();
            configChanged = true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    class MidiReceiver implements Receiver {

        public MidiReceiver() {
        }

        public void send(MidiMessage msg, long timeStamp) {
            byte[] message = msg.getMessage();
            if (message.length == 3 && message[0] <= -97 && message[1] >= 54 && message[1] <= 78) {
                if (message[0] >= -112 && message[2] != 0) {
                    // MIDI note on
                    minecraft.execute(() -> pianoKeys[message[1] - 54].onClick(DUMMY_EVENT, false));

                } else {
                    // MIDI note off
                    minecraft.execute(() -> pianoKeys[message[1] - 54].onRelease(DUMMY_EVENT));
                }
            }
        }

        public void close() {
        }
    }
}
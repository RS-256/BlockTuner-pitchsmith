package com.rs256.blocktuner.util;

import com.mojang.blaze3d.platform.InputConstants;
//? if <26.3 {
/*import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
*///?}
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
//? if <26.3 {
/*import org.lwjgl.glfw.GLFW;
*///?}

public class InputUtil {

    public static final MouseButtonEvent DUMMY_EVENT = new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0));

    //? if <26.3 {
    /*public static boolean isKeyPressed(int glfwKey) {
        Window handle = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(handle, glfwKey);
    }

    // has two keys
    public static boolean isCtrlDown() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
                || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isAltDown() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT)
                || isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static boolean isShiftDown() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)
                || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
    *///?} else {
    public static boolean isKeyPressed(int key) {
        return InputConstants.isKeyDown(key);
    }

    // has two keys
    public static boolean isCtrlDown() {
        return isKeyPressed(InputConstants.KEY_LCONTROL)
                || isKeyPressed(InputConstants.KEY_RCONTROL);
    }

    public static boolean isAltDown() {
        return isKeyPressed(InputConstants.KEY_LALT)
                || isKeyPressed(InputConstants.KEY_RALT);
    }

    public static boolean isShiftDown() {
        return isKeyPressed(InputConstants.KEY_LSHIFT)
                || isKeyPressed(InputConstants.KEY_RSHIFT);
    }
    //?}
}

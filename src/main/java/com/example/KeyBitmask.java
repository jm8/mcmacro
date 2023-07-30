package com.example;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

public class KeyBitmask {
    public static KeyBinding[] trackedKeys(GameOptions options) {
        return new KeyBinding[] {
                options.forwardKey,
                options.backKey,
                options.leftKey,
                options.rightKey,
                options.attackKey,
                options.useKey,
                options.sneakKey,
                options.jumpKey,
        };
    }

    public static int getKeyBitmask(GameOptions options) {
        KeyBinding[] bindings = trackedKeys(options);
        int result = 0;
        for (int i = 0; i < bindings.length; i++) {
            if (bindings[i].isPressed()) {
                result |= (1 << i);
            }
        }
        return result;
    }

    public static void useKeyBitmask(int bitmask, GameOptions options) {
        KeyBinding[] bindings = trackedKeys(options);
        for (int i = 0; i < bindings.length; i++) {
            bindings[i].setPressed((bitmask & (1 << i)) != 0);
        }
    }
}

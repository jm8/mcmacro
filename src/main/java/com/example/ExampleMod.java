package com.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import java.util.Random;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.StringArgumentType;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				return;
			}
			var key = client.options.leftKey;
			key.setPressed(true);
			// if (key.isPressed()) {
			// client.player.sendMessage(Text.literal("left"));
			// }
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
					literal("macro").then(literal("get")
							.then(argument("name", StringArgumentType.string()).executes(ctx -> {
								var name = StringArgumentType.getString(ctx, "name");
								var number = ClientState.loadMacro(name);
								ctx.getSource().getPlayer().sendMessage(Text.of(Integer.toString(number)));
								return 1;
							}))).then(literal("set")
									.then(argument("name", StringArgumentType.string()).executes(ctx -> {
										var name = StringArgumentType.getString(ctx, "name");
										int num = (int) (Math.random() * 100);
										ClientState.setMacro(name, num);
										return 1;
									}))));
		});

	}
}
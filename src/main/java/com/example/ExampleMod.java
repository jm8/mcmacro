package com.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	enum State {
		NONE,
		RECORDING,
		PLAYING,
	};

	State state = State.NONE;
	String macroName;
	int time = 0;
	boolean loop;
	ArrayList<Integer> macro;

	int play(FabricClientCommandSource source, String name, boolean loop) {
		this.loop = loop;
		macroName = name;
		try {
			macro = Storage.loadMacro(macroName);
			state = State.PLAYING;
			time = 0;
			source.getPlayer().sendMessage(Text.of("Looping..."));
		} catch (FileNotFoundException e) {
			source.getPlayer().sendMessage(Text.of("Macro not found"));
			return 0;
		}
		source.getPlayer().sendMessage(Text.of("Playing..."));
		return 1;
	}

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
			if (state == State.PLAYING) {
				if (time >= macro.size()) {
					if (loop) {
						time = 0;
					} else {
						client.player.sendMessage(Text.of("Done playing"));
						state = State.NONE;
					}
				} else {
					KeyBitmask.useKeyBitmask(macro.get(time), client.options);
				}
			}
			if (state == State.RECORDING) {
				macro.add(KeyBitmask.getKeyBitmask(client.options));

			}
			time++;
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
					literal("macro")
							.then(literal("loop").then(argument("name", StringArgumentType.string()).executes(ctx -> {
								return play(ctx.getSource(), StringArgumentType.getString(ctx, "name"), true);
							})))
							.then(literal("play").then(argument("name", StringArgumentType.string()).executes(ctx -> {
								return play(ctx.getSource(), StringArgumentType.getString(ctx, "name"), false);
							})))
							.then(literal("record")
									.then(argument("name", StringArgumentType.string()).executes(ctx -> {
										macroName = StringArgumentType.getString(ctx, "name");
										macro = new ArrayList<>();
										state = State.RECORDING;
										time = 0;
										ctx.getSource().getPlayer().sendMessage(Text.of("Recording..."));
										return Command.SINGLE_SUCCESS;
									})))
							.then(literal("stop").executes(ctx -> {
								if (state == State.RECORDING) {
									Storage.saveMacro(macroName, macro);
									ctx.getSource().getPlayer()
											.sendMessage(Text.of("Stopped recording '" + macroName + "'"));
								} else if (state == State.PLAYING) {
									ctx.getSource().getPlayer()
											.sendMessage(Text.of("Stopped playing '" + macroName + "'"));
								} else {
									ctx.getSource().getPlayer().sendMessage(Text.of("Nothing to stop"));
								}
								state = State.NONE;
								return Command.SINGLE_SUCCESS;
							})));
		});

	}
}
package com.example;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.api.FabricLoader;

public class ClientState {
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
    private static Path directory = FabricLoader.getInstance().getConfigDir().resolve("mcmacro");

    public static int loadMacro(String name) {
        directory.toFile().mkdirs();

        try (var scanner = new Scanner(directory.resolve(name).toFile())) {
            int result = scanner.nextInt();
            return result;
        } catch (FileNotFoundException e) {
            return -1;
        }
    }

    public static void setMacro(String name, int x) {
        try (var writer = new BufferedWriter(new FileWriter(directory.resolve(name).toFile()))) {
            writer.write(Integer.toString(x));
        } catch (IOException e) {

        }
    }
}

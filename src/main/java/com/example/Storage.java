package com.example;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.api.FabricLoader;

public class Storage {
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
    private static Path directory = FabricLoader.getInstance().getConfigDir().resolve("mcmacro");

    public static ArrayList<Integer> loadMacro(String name) throws FileNotFoundException {
        directory.toFile().mkdirs();

        try (var scanner = new Scanner(directory.resolve(name).toFile())) {
            var result = new ArrayList<Integer>();
            while (scanner.hasNextInt()) {
                result.add(scanner.nextInt());
            }
            return result;
        }
    }

    public static ArrayList<String> listMacros() {
        directory.toFile().mkdirs();
        var result = new ArrayList<String>();
        for (var file : directory.toFile().listFiles()) {
            result.add(file.getName());
        }
        return result;
    }

    public static void saveMacro(String name, List<Integer> numbers) {
        try (var writer = new BufferedWriter(new FileWriter(directory.resolve(name).toFile()))) {
            for (var number : numbers) {
                writer.write(Integer.toString(number) + "\n");
            }
        } catch (IOException e) {

        }
    }
}

package com.foc.maaltest.loader;

import com.foc.minecraftasalibrary.MinecraftTransformer;
import net.minestom.server.extensions.Extension;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class MaalLoader extends Extension {
    public static Logger logger;

    @Override
    public LoadStatus initialize() {
        logger = logger();

        final String McVersion = "1.19.2";

        final Path originalServerJar = Path.of(dataDirectory().toString(), McVersion+".original.jar");
        final Path depsJar = Path.of(dataDirectory().toString(), McVersion+".deps.jar");
        final Path remappedJar = Path.of(dataDirectory().toString(), McVersion+".remapped.jar");
        final Path mappings = Path.of(dataDirectory().toString(), McVersion+".mappings.tiny.gz");

        if (!Files.exists(remappedJar)) {
            try {
                logger.info("Downloading the official Minecraft server jar...");
                MinecraftTransformer.downloadMinecraft(McVersion, originalServerJar);

                logger.info("Downloading QuiltMC tiny mappings...");
                MinecraftTransformer.downloadMappings("https://maven.quiltmc.org/repository/release/org/quiltmc/quilt-mappings/1.19.2+build.14/quilt-mappings-1.19.2+build.14-tiny.gz", mappings);

                logger.info("Remapping and flattening jars...");
                MinecraftTransformer.flattenAndRemapJar(originalServerJar, mappings, depsJar, remappedJar);

                logger.info("The server jar has been remapped!");
            } catch (IOException e) {
                logger.error("Couldn't download one of the needed files for `MinecraftAsALibrary`!", e);
                return LoadStatus.FAILED;
            }
        } else {
            logger.info("The remapped jar already exists, using that!");
        }

        URLClassLoader clsloader;
        try {
            Path extension = Path.of(dataDirectory().toString(), "extension.jar");
            clsloader = MinecraftTransformer.createClassLoader(getClass().getClassLoader(), depsJar.toUri().toURL(), remappedJar.toUri().toURL(), extension.toUri().toURL());

        } catch (MalformedURLException e) {
            logger.error("The URL for the remapped jar is invalid! Did something go wrong?", e);
            return LoadStatus.FAILED;
        }

        try {
            Class<?> cls = clsloader.loadClass("com.foc.maaltest.extension.MaalExtension");
            cls.getMethod("initialise", Logger.class).invoke(cls, logger);
        } catch (ClassNotFoundException e) {
            logger.error("Can't load the extension class containing the logic for MinecraftAsALibrary!", e);
        } catch (NoSuchMethodException e) {
            logger.error("Can't load the needed function! Did something go wrong?", e);
        } catch (InvocationTargetException e) {
            logger.error("We're not allowed to invoke this method!", e);
        } catch (IllegalAccessException e) {
            logger.error("We're not allowed to access this method!", e);
        }

        return LoadStatus.SUCCESS;
    }

    @Override
    public void terminate() {

    }
}

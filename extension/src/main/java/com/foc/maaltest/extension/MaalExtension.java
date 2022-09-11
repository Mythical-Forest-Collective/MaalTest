package com.foc.maaltest.extension;

import net.minecraft.item.ItemStack;
import org.slf4j.Logger;

public class MaalExtension {
    public static Logger logger;

    public static void initialise(Logger mLogger) {
        logger = mLogger;

        logger.info(String.valueOf(ItemStack.class));
    }
}

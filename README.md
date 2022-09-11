# MaalTest
MAAL being used as a Minestom extension! To use this, first run `gradle shadowJar`, then copy `loader/build/libs/loader-all.jar` to your Minestom
`extensions` folder, then in the `extensions` folder on the Minestom server, create a folder called `MaalLoader`, and copy
`extension/build/libs/extension-all.jar` to `extensions/MaalLoader/extension.jar`! This just allows us to do things such as
`import net.minecraft.item.ItemStack;` in the Java code!

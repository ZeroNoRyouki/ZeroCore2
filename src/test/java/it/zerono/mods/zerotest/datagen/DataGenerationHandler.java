package it.zerono.mods.zerotest.datagen;

import it.zerono.mods.zerocore.lib.datagen.IModDataGenerator;
import it.zerono.mods.zerotest.datagen.client.AtlasTestProvider;
import it.zerono.mods.zerotest.datagen.client.GenericBlockStateProvider;
import net.minecraftforge.common.util.NonNullConsumer;
import org.jetbrains.annotations.NotNull;

public class DataGenerationHandler
        implements NonNullConsumer<IModDataGenerator> {

    //region NonNullConsumer<IModDataGenerator>

    @Override
    public void accept(@NotNull IModDataGenerator generator) {

        // common stuff

        generator.addBlockTagsProvider(new BlockTagsDataProvider());

        // client stuff
        generator.addProvider(AtlasTestProvider::new);
        generator.addProvider(GenericBlockStateProvider::new);
    }


    //endregion
}

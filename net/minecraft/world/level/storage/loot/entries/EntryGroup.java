package net.minecraft.world.level.storage.loot.entries;

import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
    EntryGroup(final LootPoolEntryContainer[] arr, final LootItemCondition[] arr) {
        super(arr, arr);
    }
    
    @Override
    protected ComposableEntryContainer compose(final ComposableEntryContainer[] arr) {
        switch (arr.length) {
            case 0: {
                return EntryGroup.ALWAYS_TRUE;
            }
            case 1: {
                return arr[0];
            }
            case 2: {
                final ComposableEntryContainer cpi3 = arr[0];
                final ComposableEntryContainer cpi4 = arr[1];
                final ComposableEntryContainer composableEntryContainer;
                final ComposableEntryContainer composableEntryContainer2;
                return (coy, consumer) -> {
                    composableEntryContainer.expand(coy, consumer);
                    composableEntryContainer2.expand(coy, consumer);
                    return true;
                };
            }
            default: {
                int length;
                int i = 0;
                ComposableEntryContainer cpi5;
                return (coy, consumer) -> {
                    for (length = arr.length; i < length; ++i) {
                        cpi5 = arr[i];
                        cpi5.expand(coy, consumer);
                    }
                    return true;
                };
            }
        }
    }
}

package net.quackimpala7321.crafter.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class CrafterRecipeCache {
    private static final int CACHE_SIZE = 10;
    private static final CachedRecipe[] cache = new CachedRecipe[CACHE_SIZE];

    public static synchronized ItemStack findMatchingRecipe(InventoryCrafting matrix, World world) {
        ItemStack[] inputs = new ItemStack[9];
        boolean empty = true;
        for (int i = 0; i < 9; i++) {
            inputs[i] = matrix.getStackInSlot(i);
            if (inputs[i] != null) empty = false;
        }
        if (empty) return null;

        for (int i = 0; i < CACHE_SIZE; i++) {
            CachedRecipe entry = cache[i];
            if (entry != null && entry.matches(inputs)) {
                moveToFront(i);
                return entry.recipe != null ? entry.recipe.getCraftingResult(matrix) : null;
            }
        }

        IRecipe matched = null;
        for (Object r : CraftingManager.getInstance().getRecipeList()) {
            IRecipe recipe = (IRecipe) r;
            if (recipe.matches(matrix, world)) {
                matched = recipe;
                break;
            }
        }
        cacheRecipe(inputs, matched);
        return matched != null ? matched.getCraftingResult(matrix) : null;
    }

    private static void moveToFront(int index) {
        if (index > 0) {
            CachedRecipe match = cache[index];
            System.arraycopy(cache, 0, cache, 1, index);
            cache[0] = match;
        }
    }

    private static void cacheRecipe(ItemStack[] inputs, IRecipe recipe) {
        ItemStack[] copy = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            if (inputs[i] != null) copy[i] = inputs[i].copy();
        }
        System.arraycopy(cache, 0, cache, 1, CACHE_SIZE - 1);
        cache[0] = new CachedRecipe(copy, recipe);
    }

    private static class CachedRecipe {
        final ItemStack[] inputs;
        final IRecipe recipe;

        CachedRecipe(ItemStack[] inputs, IRecipe recipe) {
            this.inputs = inputs;
            this.recipe = recipe;
        }

        boolean matches(ItemStack[] test) {
            for (int i = 0; i < 9; i++) {
                if (!areItemsAndTagsEqual(inputs[i], test[i])) return false;
            }
            return true;
        }
    }

    public static boolean areItemsAndTagsEqual(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getItem() == b.getItem()
            && a.getItemDamage() == b.getItemDamage()
            && ItemStack.areItemStackTagsEqual(a, b);
    }
}

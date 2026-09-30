package net.quackimpala7321.crafter.recipe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RecipeCache {
    private final CachedRecipe[] cache;
    private WeakReference<RecipeManager> recipeManagerRef = new WeakReference<>(null);

    public RecipeCache(int size) {
        this.cache = new CachedRecipe[size];
    }

    public Optional<CraftingRecipe> getRecipe(World world, CraftingInventory inputInventory) {
        if (inputInventory.isEmpty()) {
            return Optional.empty();
        } else {
            this.validateRecipeManager(world);

            List<ItemStack> inputStacks = new ArrayList<>(inputInventory.size());
            for (int i = 0; i < inputInventory.size(); i++) {
                inputStacks.add(inputInventory.getStack(i));
            }

            for(int i = 0; i < this.cache.length; ++i) {
                CachedRecipe cachedRecipe = this.cache[i];
                if (cachedRecipe != null && cachedRecipe.matches(inputStacks)) {
                    this.sendToFront(i);
                    return Optional.ofNullable(cachedRecipe.craftingRecipe());
                }
            }

            return this.getAndCacheRecipe(inputInventory, inputStacks, world);
        }
    }

    private void validateRecipeManager(World world) {
        RecipeManager recipeManager = world.getRecipeManager();
        if (recipeManager != this.recipeManagerRef.get()) {
            this.recipeManagerRef = new WeakReference<>(recipeManager);
            Arrays.fill(this.cache, null);
        }

    }

    private Optional<CraftingRecipe> getAndCacheRecipe(CraftingInventory inputInventory, List<ItemStack> inputStacks, World world) {
        Optional<CraftingRecipe> optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inputInventory, world);
        this.cache(inputStacks, optional.orElse(null));
        return optional;
    }

    private void sendToFront(int index) {
        if (index > 0) {
            CachedRecipe cachedRecipe = this.cache[index];
            System.arraycopy(this.cache, 0, this.cache, 1, index);
            this.cache[0] = cachedRecipe;
        }

    }

    private void cache(List<ItemStack> inputStacks, @Nullable CraftingRecipe recipe) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inputStacks.size(), ItemStack.EMPTY);

        for(int i = 0; i < inputStacks.size(); ++i) {
            ItemStack stack = inputStacks.get(i).copy();
            stack.setCount(1);
            defaultedList.set(i, stack);
        }

        System.arraycopy(this.cache, 0, this.cache, 1, this.cache.length - 1);
        this.cache[0] = new CachedRecipe(defaultedList, recipe);
    }

    record CachedRecipe(DefaultedList<ItemStack> defaultedList, @Nullable CraftingRecipe craftingRecipe) {

        public boolean matches(List<ItemStack> inputs) {
            if (this.defaultedList.size() != inputs.size()) {
                return false;
            } else {
                for(int i = 0; i < this.defaultedList.size(); ++i) {
                    if (!(this.defaultedList.get(i).getItem() == inputs.get(i).getItem() && ItemStack.areTagsEqual(this.defaultedList.get(i), inputs.get(i)))) {
                        return false;
                    }
                }

                return true;
            }
        }
    }
}

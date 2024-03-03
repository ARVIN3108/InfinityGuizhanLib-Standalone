package net.guizhanss.guizhanlib.slimefun.machines;

import id.arvin3108.standalone.SlimefunAddonInstance;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import net.guizhanss.guizhanlib.utils.StackUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public final class MachineBlock extends AbstractMachineBlock {

    @Setter
    protected MachineLayout layout = MachineLayout.MACHINE_DEFAULT;
    private final List<MachineBlockRecipe> recipes = new ArrayList<>();
    private int ticksPerOutput = -1;

    public MachineBlock(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    public MachineBlock addRecipe(ItemStack output, ItemStack... inputs) {
        if (inputs.length == 0) {
            throw new IllegalArgumentException("Cannot add recipe with no input!");
        }
        MachineBlockRecipe recipe = new MachineBlockRecipe(output, inputs);
        recipes.add(recipe);
        return this;
    }

    @Nonnull
    public MachineBlock addRecipesFrom(MachineRecipeType recipeType) {
        recipeType.sendRecipesTo((in, out) -> addRecipe(out, in));
        return this;
    }

    @Nonnull
    public MachineBlock ticksPerOutput(int ticks) {
        if (ticks < 1) {
            throw new IllegalArgumentException("Ticks Per Output must be at least 1!");
        }
        ticksPerOutput = ticks;
        return this;
    }

    @Override
    protected void setup(BlockMenuPreset preset) {
        preset.drawBackground(OUTPUT_BORDER, layout.getOutputBorder());
        preset.drawBackground(INPUT_BORDER, layout.getInputBorder());
        preset.drawBackground(BACKGROUND_ITEM, layout.getBackground());
        preset.addItem(layout.getStatusSlot(), IDLE_ITEM, ChestMenuUtils.getEmptyClickHandler());
    }

    @Override
    protected int[] getInputSlots() {
        return layout.getInputSlots();
    }

    @Override
    protected int[] getOutputSlots() {
        return layout.getOutputSlots();
    }

    @Override
    public void preRegister() {
        if (ticksPerOutput == -1) {
            throw new IllegalStateException("You must call .ticksPerOutput() before registering!");
        }
        super.preRegister();
    }

    @Override
    protected boolean process(Block b, BlockMenu menu) {
        if (SlimefunAddonInstance.getSlimefunTickCount() % ticksPerOutput != 0) {
            return true;
        }

        int[] slots = layout.getInputSlots();
        ItemStack[] input = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            input[i] = menu.getItemInSlot(slots[i]);
        }

        MachineBlockRecipe recipe = getOutput(input);
        if (recipe != null) {
            ItemStack rem = menu.pushItem(recipe.output.clone(), layout.getOutputSlots());
            if (rem == null || rem.getAmount() < recipe.output.getAmount()) {
                recipe.consume();
                if (menu.hasViewer()) {
                    menu.replaceExistingItem(getStatusSlot(), PROCESSING_ITEM);
                }
                return true;
            } else {
                if (menu.hasViewer()) {
                    menu.replaceExistingItem(getStatusSlot(), NO_ROOM_ITEM);
                }
                return false;
            }
        }

        if (menu.hasViewer()) {
            menu.replaceExistingItem(getStatusSlot(), IDLE_ITEM);
        }
        return false;
    }

    @Nullable
    MachineBlockRecipe getOutput(ItemStack[] items) {
        Map<String, MachineInput> map = new HashMap<>(2, 1F);
        for (ItemStack item : items) {
            if (item != null) {
                String string = StackUtil.getId(item);
                if (string == null) {
                    string = item.getType().name();
                }
                map.compute(string, (str, input) -> input == null ? new MachineInput(item) : input.add(item));
            }
        }

        for (MachineBlockRecipe recipe : recipes) {
            if (recipe.check(map)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    protected int getStatusSlot() {
        return layout.getStatusSlot();
    }

    @Override
    protected ItemStack getNoEnergyItem() {
        return NO_ENERGY_ITEM;
    }

}
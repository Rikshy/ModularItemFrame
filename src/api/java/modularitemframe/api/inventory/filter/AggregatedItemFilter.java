package modularitemframe.api.inventory.filter;

import net.minecraft.item.ItemStack;

public class AggregatedItemFilter implements IItemFilter {
    private final AggregateType type;
    private final IItemFilter[] filters;

    public AggregatedItemFilter(AggregateType type, IItemFilter... filters) {
        this.type = type;
        this.filters = filters;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (type == AggregateType.ALL) {
            for (IItemFilter filter : filters) {
                if (!filter.test(stack)) {
                    return false;
                }
            }
            return true;
        } else {
            for (IItemFilter filter : filters) {
                if (filter.test(stack)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static IItemFilter anyOf(IItemFilter... filters) {
        return new AggregatedItemFilter(AggregateType.ANY, filters);
    }

    public static IItemFilter anyOf(ItemStack... stacks) {
        if (stacks == null || stacks.length == 0) {
            return DefaultFilter.NOTHING;
        }
        if (stacks.length == 1) {
            return new StackItemFilter(stacks[0]);
        }
        IItemFilter[] filters = new IItemFilter[stacks.length];
        int i = 0;
        for (ItemStack stack : stacks) {
            filters[i++] = new StackItemFilter(stack);
        }
        return anyOf(filters);
    }

    public enum AggregateType {
        ALL(),
        ANY()
    }
}

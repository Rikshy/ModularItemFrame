package modularitemframe.api.inventory.filter;

import net.minecraft.item.ItemStack;

public interface IItemFilter {
    boolean test(ItemStack stack);

    default IItemFilter or(IItemFilter filter) {
        return new AggregatedItemFilter(AggregatedItemFilter.AggregateType.ANY, this, filter);
    }

    default IItemFilter and(IItemFilter filter) {
        return new AggregatedItemFilter(AggregatedItemFilter.AggregateType.ALL, this, filter);
    }

    default IItemFilter negate() {
        return new NegatedItemFilter(this);
    }
}

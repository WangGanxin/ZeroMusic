package com.viewpagerindicator;

public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);
    int getorientation();
    // From PagerAdapter
    int getCount();
}

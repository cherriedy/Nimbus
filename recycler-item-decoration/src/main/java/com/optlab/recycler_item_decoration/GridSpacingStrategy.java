package com.optlab.recycler_item_decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class GridSpacingStrategy implements SpacingStrategy {
    private final int spacing;

    public GridSpacingStrategy(Context context, int spacing) {
        this.spacing = dpToPx(context, spacing);
    }

    /**
     * Sets the spacing for each item in the RecyclerView.
     *
     * <p>This method is called by the RecyclerView to set the spacing for each item in the grid. It
     * calculates the spacing based on the position of the item in the grid and the number of spans.
     * It also ensures that the spacing is applied correctly for the first row of items.
     *
     * @param outRect the Rect to set the spacing
     * @param view the view to set the spacing for
     * @param parent the RecyclerView
     * @param state the RecyclerView state
     */
    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager glm = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.LayoutParams viewLayoutParams =
                (GridLayoutManager.LayoutParams) view.getLayoutParams();

        // Get the number of spans in the grid layout.
        int spanCount = Objects.requireNonNull(glm).getSpanCount();

        // Get the index of view in a single row.
        int spanIndex = viewLayoutParams.getSpanIndex();

        // Get the position of view in the grid layout.
        int position = parent.getChildAdapterPosition(view);

        // Only applying top margin to the first row.
        int spanGroupIndex = glm.getSpanSizeLookup().getSpanGroupIndex(position, spanCount);
        if (spanGroupIndex == 0) {
            outRect.top = spacing;
        }

        outRect.bottom = spacing;

        outRect.left = spacing - ((spanIndex * spacing) / spanCount);
        outRect.right = ((spanIndex + 1) * spacing) / spanCount;
    }
}

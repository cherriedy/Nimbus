package com.optlab.nimbus.ui.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.EnumSet;
import java.util.Set;

public class LinearSpacingStrategy implements SpacingStrategy {
    /** Spacing in dp to be applied between items. */
    private final int spacing;

    /** Set of directions to apply spacing. */
    private final Set<Direction> directions;

    /** Whether to include spacing at the edges of the RecyclerView. */
    private final boolean includeEdgeSpacing;

    public LinearSpacingStrategy(
            Context context, int spacing, Set<Direction> directions, boolean includeEdgeSpacing) {
        this.spacing = dpToPx(context, spacing);
        this.directions = directions;
        this.includeEdgeSpacing = includeEdgeSpacing;
    }

    public LinearSpacingStrategy(Context context, int spacing, boolean includeEdgeSpacing) {
        this(context, spacing, EnumSet.allOf(Direction.class), includeEdgeSpacing);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;

        if (parent.getLayoutManager() instanceof LinearLayoutManager llm) {
            if (llm.getOrientation() == RecyclerView.VERTICAL) {
                applyVerticalSpacing(outRect, position, itemCount);
            } else {
                applyHorizontalSpacing(outRect, position, itemCount);
            }
        } else {
            throw new IllegalStateException(
                    "LayoutManager must be LinearLayoutManager. Found: "
                            + parent.getLayoutManager().getClass().getSimpleName());
        }
    }

    /**
     * Applies spacing for horizontal orientation.
     *
     * <p>For horizontal orientation, the spacing is applied as follows:
     *
     * <ul>
     *   <li>Left edge: Spacing is applied if includeEdgeSpacing is true and position is 0.
     *   <li>Right edge: Spacing is applied if includeEdgeSpacing is true or if position is not the
     *       last item.
     *   <li>Top edge: Spacing is always applied.
     *   <li>Bottom edge: Spacing is always applied.
     * </ul>
     *
     * @param outRect the Rect to apply spacing to
     * @param position the position of the item
     * @param itemCount the total number of items in the adapter
     */
    private void applyHorizontalSpacing(Rect outRect, int position, int itemCount) {
        if (directions.contains(Direction.LEFT) && includeEdgeSpacing && position == 0) {
            outRect.left = spacing;
        }
        if (directions.contains(Direction.RIGHT)
                && (includeEdgeSpacing || position != itemCount - 1)) {
            outRect.right = spacing;
        }
        if (directions.contains(Direction.TOP)) {
            outRect.top = spacing;
        }
        if (directions.contains(Direction.BOTTOM)) {
            outRect.bottom = spacing;
        }
    }

    /**
     * Applies spacing for vertical orientation.
     *
     * <p>For vertical orientation, the spacing is applied as follows:
     *
     * <ul>
     *   <li>Top edge: Spacing is applied if includeEdgeSpacing is true and position is 0.
     *   <li>Bottom edge: Spacing is applied if includeEdgeSpacing is true or if position is not the
     *       last item.
     *   <li>Left edge: Spacing is always applied.
     *   <li>Right edge: Spacing is always applied.
     * </ul>
     *
     * @param outRect the Rect to apply spacing to
     * @param position the position of the item
     * @param itemCount the total number of items in the adapter
     */
    private void applyVerticalSpacing(Rect outRect, int position, int itemCount) {
        if (directions.contains(Direction.TOP) && includeEdgeSpacing && position == 0) {
            outRect.top = spacing;
        }
        if (directions.contains(Direction.BOTTOM)
                && (includeEdgeSpacing || position != itemCount - 1)) {
            outRect.bottom = spacing;
        }
        if (directions.contains(Direction.LEFT)) {
            outRect.left = spacing;
        }
        if (directions.contains(Direction.RIGHT)) {
            outRect.right = spacing;
        }
    }
}

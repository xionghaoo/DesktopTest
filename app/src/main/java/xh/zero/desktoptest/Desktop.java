package xh.zero.desktoptest;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

import static xh.zero.desktoptest.Definitions.WallpaperScroll.Inverse;
import static xh.zero.desktoptest.Definitions.WallpaperScroll.Off;

public final class Desktop extends ViewPager implements DesktopCallback {
    private OnDesktopEditListener _desktopEditListener;
    private boolean _inEditMode;
    private PagerIndicator _pageIndicator;

    private final List<CellContainer> _pages = new ArrayList<>();
    private final Point _previousDragPoint = new Point();

    private Point _coordinate = new Point(-1, -1);
    private DesktopAdapter _adapter;
    private Item _previousItem;
    private View _previousItemView;
    private int _previousPage;

    public String[] otherPageTitles = new String[] {
            "page_1", "page_2", "page_3"
    };

    public Desktop(Context context) {
        super(context, null);
    }

    public Desktop(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public static boolean handleOnDropOver(MainActivity mainActivity, Item dropItem, Item item, View itemView, CellContainer parent, int page, Definitions.ItemPosition itemPosition, DesktopCallback callback) {
        // dropItem 正在拖动的icon
        // item 拖动放下的目标icon
        if (item != null) {
            if (dropItem != null) {
                Item.Type type = item._type;
                if (type != null) {
                    switch (type) {
                        case APP:
                        case SHORTCUT:
                            if (Item.Type.APP.equals(dropItem._type) || Item.Type.SHORTCUT.equals(dropItem._type)) {
                                // 添加App Icon到新的桌面或者当前桌面的文件夹
                                // App -> App
                                parent.removeView(itemView);
                                Item group = Item.newGroupItem();
                                item._location = Definitions.ItemPosition.Group;
                                dropItem._location = Definitions.ItemPosition.Group;
                                group.getGroupItems().add(item);
                                group.getGroupItems().add(dropItem);
                                group._x = item._x;
                                group._y = item._y;
                                mainActivity.Companion.getDb().saveItem(dropItem, page, Definitions.ItemPosition.Group);
                                mainActivity.Companion.getDb().saveItem(item, Definitions.ItemState.Hidden);
                                mainActivity.Companion.getDb().saveItem(dropItem, Definitions.ItemState.Hidden);
                                mainActivity.Companion.getDb().saveItem(group, page, itemPosition);
                                callback.addItemToPage(group, page);
                                MainActivity launcher = MainActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
//                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            } else if (Item.Type.GROUP.equals(dropItem._type) && dropItem.getGroupItems().size() < GroupPopupView.GroupDef._maxItem) {
                                // 添加App Icon到桌面文件夹
                                // folder -> app
                                parent.removeView(itemView);
                                Item group = Item.newGroupItem();
                                item._location = Definitions.ItemPosition.Group;
                                dropItem._location = Definitions.ItemPosition.Group;
                                group.getGroupItems().add(item);
                                group.getGroupItems().addAll(dropItem.getGroupItems());
                                group._x = item._x;
                                group._y = item._y;
                                MainActivity.Companion.getDb().deleteItem(dropItem, false);
                                MainActivity.Companion.getDb().saveItem(item, Definitions.ItemState.Hidden);
                                MainActivity.Companion.getDb().saveItem(group, page, itemPosition);
                                callback.addItemToPage(group, page);
                                MainActivity launcher = MainActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
//                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            }
                            break;
                        case GROUP:
                            if ((Item.Type.APP.equals(dropItem._type) || Item.Type.SHORTCUT.equals(dropItem._type)) && item.getGroupItems().size() < GroupPopupView.GroupDef._maxItem) {
                                // App -> folder
                                parent.removeView(itemView);
                                dropItem._location = Definitions.ItemPosition.Group;
                                item.getGroupItems().add(dropItem);
                                MainActivity.Companion.getDb().saveItem(dropItem, page, Definitions.ItemPosition.Group);
                                MainActivity.Companion.getDb().saveItem(dropItem, Definitions.ItemState.Hidden);
                                MainActivity.Companion.getDb().saveItem(item, page, itemPosition);
                                callback.addItemToPage(item, page);
                                MainActivity launcher = MainActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
//                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            } else if (Item.Type.GROUP.equals(dropItem._type) && item.getGroupItems().size() < GroupPopupView.GroupDef._maxItem && dropItem.getGroupItems().size() < GroupPopupView.GroupDef._maxItem) {
                                // folder -> folder
                                parent.removeView(itemView);
                                item.getGroupItems().addAll(dropItem.getGroupItems());
                                MainActivity.Companion.getDb().saveItem(item, page, itemPosition);
                                MainActivity.Companion.getDb().deleteItem(dropItem, false);
                                callback.addItemToPage(item, page);
                                MainActivity launcher = MainActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
//                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public final class DesktopAdapter extends PagerAdapter {

        private final Desktop _desktop;

        public DesktopAdapter(Desktop desktop) {
            _desktop = desktop;
            _desktop.getPages().clear();
            int count = MainActivity.Companion.getDb().getDesktop().size();
            if (count == 0) count++;
            for (int i = 0; i < count; i++) {
                _desktop.getPages().add(getItemLayout());
            }
        }

//        private SimpleFingerGestures.OnFingerGestureListener getGestureListener() {
//            return new DesktopGestureListener(_desktop, Setup.desktopGestureCallback());
//        }

        private CellContainer getItemLayout() {
            Context context = _desktop.getContext();
            CellContainer layout = new CellContainer(context);
            SimpleFingerGestures mySfg = new SimpleFingerGestures();
//            mySfg.setOnFingerGestureListener(getGestureListener());
            layout.setGestures(mySfg);
            layout.setGridSize(5, 6);
            // TODO 去掉桌面退出长按编辑模式
//            layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    exitDesktopEditMode();
//                }
//            });
            // TODO 去掉桌面长按编辑模式
//            layout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    enterDesktopEditMode();
//                    if (true) {
//                        Tool.vibrate(MainActivity.Companion.getLauncher().getDesktop());
//                    }
//                    return true;
//                }
//            });
            return layout;
        }

        public void addPageLeft() {
            _desktop.getPages().add(0, getItemLayout());
            notifyDataSetChanged();
        }

        public void addPageRight() {
            _desktop.getPages().add(getItemLayout());
            notifyDataSetChanged();
        }

        public void removePage(int position, boolean deleteItems) {
            if (deleteItems) {
                for (View view : _desktop.getPages().get(position).getAllCells()) {
                    Object item = view.getTag();
                    if (item instanceof Item) {
                        MainActivity.Companion.getDb().deleteItem((Item) item, true);
                    }
                }
            }
            _desktop.getPages().remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return _desktop.getPages().size() + otherPageTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        @Override
        public boolean isViewFromObject(View p1, Object p2) {
            return p1 == p2;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View layout = null;
            // 区分页面类型
            if (position >= 0 && position < otherPageTitles.length) {
                FrameLayout page = new FrameLayout(container.getContext());
                layout = page;
                container.addView(page);
                page.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                page.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                TextView tv = new TextView(container.getContext());
                tv.setText(otherPageTitles[position]);
                page.addView(tv);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) tv.getLayoutParams();
                page.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                page.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
            } else {
                layout = _desktop.getPages().get(position - otherPageTitles.length);
                container.addView(layout);
            }
            return layout;
//            CellContainer layout = _desktop.getPages().get(position);
//            container.addView(layout);
//            return layout;
        }
    }

    public void enterDesktopEditMode() {
        float scaleFactor = 0.9f;
        float translateFactor = (float) Tool.dp2px(true ? 20 : 40);
        for (CellContainer v : getPages()) {
            v.setBlockTouch(true);
            v.animateBackgroundShow();
            ViewPropertyAnimator animation = v.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationY(translateFactor);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        setInEditMode(true);
        if (getDesktopEditListener() != null) {
            OnDesktopEditListener desktopEditListener = getDesktopEditListener();
            desktopEditListener.onStartDesktopEdit();
        }
    }

    public void exitDesktopEditMode() {
        float scaleFactor = 1.0f;
        float translateFactor = 0.0f;
        for (CellContainer v : getPages()) {
            v.setBlockTouch(false);
            v.animateBackgroundHide();
            ViewPropertyAnimator animation = v.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationY(translateFactor);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        setInEditMode(false);
        if (getDesktopEditListener() != null) {
            OnDesktopEditListener desktopEditListener = getDesktopEditListener();
            desktopEditListener.onFinishDesktopEdit();
        }
    }

    public final List<CellContainer> getPages() {
        return _pages;
    }

    public final OnDesktopEditListener getDesktopEditListener() {
        return _desktopEditListener;
    }

    public final void setDesktopEditListener(@Nullable OnDesktopEditListener v) {
        _desktopEditListener = v;
    }

    public final boolean getInEditMode() {
        return _inEditMode;
    }

    public final void setInEditMode(boolean v) {
        _inEditMode = v;
    }

    public final boolean isCurrentPageEmpty() {
        return getCurrentPage().getChildCount() == 0;
    }

    public final CellContainer getCurrentPage() {
        int currentIndex = getCurrentItem() - otherPageTitles.length;
        return _pages.get(currentIndex < 0 ? 0 : currentIndex);
    }

    public final int getRelatedCurrentItem() {
        int currentIndex = getCurrentItem() - otherPageTitles.length;
        return currentIndex < 0 ? 0 : currentIndex;
    }

    public final int getTotalPageSize() {
        return _pages.size() + otherPageTitles.length;
    }

    public final void setPageIndicator(PagerIndicator pageIndicator) {
        _pageIndicator = pageIndicator;
    }

    public final void initDesktop() {
        _adapter = new DesktopAdapter(this);
        setAdapter(_adapter);
        setCurrentItem(0);
        if (_pageIndicator != null) {
            _pageIndicator.setViewPager(this);
        }

        int columns = 5;
        int rows = 6;
        List<List<Item>> desktopItems = MainActivity.Companion.getDb().getDesktop();
        for (int pageCount = 0; pageCount < desktopItems.size(); pageCount++) {
            List<Item> page = desktopItems.get(pageCount);
            _pages.get(pageCount).removeAllViews();
            for (int itemCount = 0; itemCount < page.size(); itemCount++) {
                Item item = page.get(itemCount);
                if (item._x + item._spanX <= columns && item._y + item._spanY <= rows) {
                    addItemToPage(item, pageCount);
                }
            }
        }
    }

    public final void initCell() {

    }

    public final void addPageRight(boolean showGrid) {
        int previousPage = getCurrentItem();
        _adapter.addPageRight();
        setCurrentItem(previousPage + 1);
        _pageIndicator.invalidate();
    }

    public final void addPageLeft(boolean showGrid) {
        int previousPage = getCurrentItem();
        _adapter.addPageLeft();
        setCurrentItem(previousPage + 1, false);
        setCurrentItem(previousPage - 1);
        if (true) {
            for (CellContainer cellContainer : _pages) {
                cellContainer.setHideGrid(!showGrid);
            }
        }
        _pageIndicator.invalidate();
    }

//    public final void removeCurrentPage() {
//        int previousPage = getCurrentItem();
//        _adapter.removePage(getCurrentItem(), true);
//        if (_pages.size() == 0) {
//            addPageRight(false);
//            _adapter.exitDesktopEditMode();
//        } else {
//            setCurrentItem(previousPage, true);
//            _pageIndicator.invalidate();
//        }
//    }

    public final void updateIconProjection(int x, int y) {
        MainActivity launcher = MainActivity.Companion.getLauncher();
        ItemOptionView dragNDropView = launcher.getItemOptionView();
        CellContainer.DragState state = getCurrentPage().peekItemAndSwap(x, y, _coordinate);
        if (!_coordinate.equals(_previousDragPoint)) {
            dragNDropView.cancelFolderPreview();
        }
        _previousDragPoint.set(_coordinate.x, _coordinate.y);
        switch (state) {
            case CurrentNotOccupied:
                getCurrentPage().projectImageOutlineAt(_coordinate, DragHandler._cachedDragBitmap);
                break;
            case CurrentOccupied:
                Item.Type type = dragNDropView.getDragItem()._type;
                for (CellContainer page : _pages) {
                    page.clearCachedOutlineBitmap();
                }
                if (!type.equals(Item.Type.WIDGET) && (getCurrentPage().coordinateToChildView(_coordinate) instanceof AppItemView)) {
                    dragNDropView.showFolderPreviewAt(this, getCurrentPage().getCellWidth() * (_coordinate.x + 0.5f), getCurrentPage().getCellHeight() * (_coordinate.y + 0.5f));
                }
                break;
            case OutOffRange:
            case ItemViewNotFound:
            default:
                break;
        }
    }

    @Override
    public void setLastItem(Item item, View view) {
        _previousPage = getCurrentItem();
        _previousItemView = view;
        _previousItem = item;
        getCurrentPage().removeView(view);
    }

    @Override
    public void revertLastItem() {
        if (_previousItemView != null) {
            if (_adapter.getCount() >= _previousPage && _previousPage > -1) {
                CellContainer cellContainer = _pages.get(_previousPage);
                cellContainer.addViewToGrid(_previousItemView);
                _previousItem = null;
                _previousItemView = null;
                _previousPage = -1;
            }
        }
    }

    @Override
    public void consumeLastItem() {
        _previousItem = null;
        _previousItemView = null;
        _previousPage = -1;
    }

    public boolean addItemToPage(@NonNull Item item, int page) {
        View itemView = ItemViewFactory.getItemView(getContext(), this, DragAction.Action.DESKTOP, item);
        if (itemView == null) {
            // TODO see if this fixes SD card bug
            // apps that are located on SD card disappear on reboot
            // might be from this line of code so comment out for now
            //MainActivity._db.deleteItem(item, true);
            return false;
        }
        item._location = Definitions.ItemPosition.Desktop;
        _pages.get(page).addViewToGrid(itemView, item._x, item._y, item._spanX, item._spanY);
        return true;
    }

    public boolean addItemToPoint(@NonNull Item item, int x, int y) {
        CellContainer.LayoutParams positionToLayoutPrams = getCurrentPage().coordinateToLayoutParams(x, y, item._spanX, item._spanY);
        if (positionToLayoutPrams == null) {
            return false;
        }
        item._location = Definitions.ItemPosition.Desktop;
        item._x = positionToLayoutPrams.getX();
        item._y = positionToLayoutPrams.getY();
        View itemView = ItemViewFactory.getItemView(getContext(), this, DragAction.Action.DESKTOP, item);
        if (itemView != null) {
            itemView.setLayoutParams(positionToLayoutPrams);
            getCurrentPage().addView(itemView);
        }
        return true;
    }

    public boolean addItemToCell(@NonNull Item item, int x, int y) {
        item._location = Definitions.ItemPosition.Desktop;
        item._x = x;
        item._y = y;
        View itemView = ItemViewFactory.getItemView(getContext(), this, DragAction.Action.DESKTOP, item);
        if (itemView == null) {
            return false;
        }
        getCurrentPage().addViewToGrid(itemView, item._x, item._y, item._spanX, item._spanY);
        return true;
    }

    public void removeItem(final View view, boolean animate) {
        if (animate) {
            view.animate().setDuration(100).scaleX(0.0f).scaleY(0.0f).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (getCurrentPage().equals(view.getParent())) {
                        getCurrentPage().removeView(view);
                    }
                }
            });
        } else if (getCurrentPage().equals(view.getParent())) {
            getCurrentPage().removeView(view);
        }
    }

    public void removePage(int position) {
        _adapter.removePage(position, true);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        Definitions.WallpaperScroll scroll = Definitions.WallpaperScroll.Normal;
        float xOffset = (position + offset) / (_pages.size() - 1);
        if (scroll.equals(Inverse)) {
            xOffset = 1f - xOffset;
        } else if (scroll.equals(Off)) {
            xOffset = 0.5f;
        }

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        wallpaperManager.setWallpaperOffsets(getWindowToken(), xOffset, 0.0f);
        super.onPageScrolled(position, offset, offsetPixels);
    }

//    @Override
//    public void setCurrentItem(int item) {
//        super.setCurrentItem(item);
//        for (int i = 0; i < _pages.size(); i++) {
//            CellContainer page = _pages.get(i);
//            Log.d("Desktop", "page " + i + " is empty = " + page.isEmpty());
//        }
//    }

    public interface OnDesktopEditListener {
        void onStartDesktopEdit();

        void onFinishDesktopEdit();
    }
}

package xh.zero.desktoptest.homeparts;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import xh.zero.desktoptest.CellContainer;
import xh.zero.desktoptest.Definitions;
import xh.zero.desktoptest.Desktop;
import xh.zero.desktoptest.DragAction;
import xh.zero.desktoptest.DragAction.Action;
import xh.zero.desktoptest.DropTargetListener;
import xh.zero.desktoptest.Item;
import xh.zero.desktoptest.ItemOptionView;
import xh.zero.desktoptest.MainActivity;
import xh.zero.desktoptest.R;
import xh.zero.desktoptest.Tool;

public class HpDragOption {
    public void initDragNDrop(@NonNull final MainActivity _homeActivity, final View leftDragHandle, final View rightDragHandle, @NonNull final ItemOptionView dragNDropView) {
        final Handler dragHandler = new Handler();

        // 左边缘处理
        dragNDropView.registerDropTarget(new DropTargetListener() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int i = _homeActivity.getDesktop().getRelatedCurrentItem();
                    if (i > 0) {
                        _homeActivity.getDesktop().setCurrentItem(_homeActivity.getDesktop().getCurrentItem() - 1);
                    } else if (i <= 0) {
//                        _homeActivity.getDesktop().addPageLeft(true);
                    }
                    dragHandler.postDelayed(this, 1000);
                }
            };

            @Override
            public View getView() {
                return leftDragHandle;
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                leftDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnter(Action action, PointF location) {
                dragHandler.post(runnable);
                leftDragHandle.animate().alpha(0.9f);
            }

            @Override
            public void onMove(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                // do nothing
            }

            @Override
            public void onExit(Action action, PointF location) {
                dragHandler.removeCallbacksAndMessages(null);
                leftDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnd() {
                dragHandler.removeCallbacksAndMessages(null);
                leftDragHandle.animate().alpha(0f);
            }
        });

        // 右边缘处理
        dragNDropView.registerDropTarget(new DropTargetListener() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int i = _homeActivity.getDesktop().getCurrentItem();
                    if (i < _homeActivity.getDesktop().getTotalPageSize() - 1) {
                        // 移动到新的页面
                        Log.d("mytest", "move to new page");
                        _homeActivity.getDesktop().setCurrentItem(_homeActivity.getDesktop().getCurrentItem() + 1);
                    } else if (i == _homeActivity.getDesktop().getTotalPageSize() - 1) {
                        // 添加新的页面
                        if (!_homeActivity.getDesktop().getCurrentPage().isEmpty()) {
                            _homeActivity.getDesktop().addPageRight(false);
                        }
                        Log.d("mytest", "add new page");
                    }
                    dragHandler.postDelayed(this, 1000);
                }
            };

            @Override
            public View getView() {
                return rightDragHandle;
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                rightDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnter(Action action, PointF location) {
                dragHandler.post(runnable);
                rightDragHandle.animate().alpha(0.9f);
            }

            @Override
            public void onMove(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                // do nothing
            }

            @Override
            public void onExit(Action action, PointF location) {
                dragHandler.removeCallbacksAndMessages(null);
                rightDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnd() {
                dragHandler.removeCallbacksAndMessages(null);
                rightDragHandle.animate().alpha(0f);
            }
        });

        // desktop drag event
        dragNDropView.registerDropTarget(new DropTargetListener() {
            @Override
            public View getView() {
                return _homeActivity.getDesktop();
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                Log.d("mytest", "drag onStart");

//                _homeActivity.getDesktop().enterDesktopEditMode();
//                Tool.vibrate(MainActivity.Companion.getLauncher().getDesktop());

                _homeActivity.getItemOptionView().showItemPopup(_homeActivity);
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
//                _homeActivity.closeAppDrawer();
//                _homeActivity.getSearchBar().collapse();
//                if (Setup.appSettings().getDesktopShowGrid()) {
//                    _homeActivity.getDock().setHideGrid(false);
//                    for (CellContainer cellContainer : _homeActivity.getDesktop().getPages()) {
//                        cellContainer.setHideGrid(false);
//                    }
//                }
            }

            @Override
            public void onEnter(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                // this statement makes sure that adding an app multiple times from the app drawer works
                // the app will get a new id every time
//                if (DragAction.Action.DRAWER.equals(action)) {
//                    if (_homeActivity.getAppDrawerController()._isOpen) {
//                        return;
//                    }
//                    item.reset();
//                }

                int x = (int) location.x;
                int y = (int) location.y;
                if (_homeActivity.getDesktop().addItemToPoint(item, x, y)) {
                    // 在当前页面拖动放置icon(空白处)，如果有合并，会跳到else
                    _homeActivity.getDesktop().consumeLastItem();
//                    _homeActivity.getDock().consumeLastItem();
                    // add the item to the database
                    MainActivity.Companion.getDb().saveItem(item, _homeActivity.getDesktop().getRelatedCurrentItem(), Definitions.ItemPosition.Desktop);
                } else {
                    Point pos = new Point();
                    _homeActivity.getDesktop().getCurrentPage().touchPosToCoordinate(pos, x, y, item._spanX, item._spanY, false);
                    View itemView = _homeActivity.getDesktop().getCurrentPage().coordinateToChildView(pos);

                    if (itemView != null && Desktop.handleOnDropOver(_homeActivity, item, (Item) itemView.getTag(), itemView, _homeActivity.getDesktop().getCurrentPage(), _homeActivity.getDesktop().getRelatedCurrentItem(), Definitions.ItemPosition.Desktop, _homeActivity.getDesktop())) {
                        _homeActivity.getDesktop().consumeLastItem();
//                        _homeActivity.getDock().consumeLastItem();
                    } else {
                        Tool.toast(_homeActivity, R.string.toast_not_enough_space);
                        _homeActivity.getDesktop().revertLastItem();
//                        _homeActivity.getDock().revertLastItem();
                    }
                }
            }

            @Override
            public void onMove(Action action, PointF location) {
                _homeActivity.getDesktop().updateIconProjection((int) location.x, (int) location.y);
            }

            @Override
            public void onExit(Action action, PointF location) {
                for (CellContainer page : _homeActivity.getDesktop().getPages()) {
                    page.clearCachedOutlineBitmap();
                }
                dragNDropView.cancelFolderPreview();
                Log.d("mytest", "drag onExit");
            }

            @Override
            public void onEnd() {
//                if (_homeActivity.getDesktop().isInEditMode()) {
//
//                }
//                _homeActivity.getDesktop().exitDesktopEditMode();
                Log.d("mytest", "drag onEnd");

                for (int i = 0; i < _homeActivity.getDesktop().getPages().size(); i++) {
                    CellContainer page = _homeActivity.getDesktop().getPages().get(i);
                    page.clearCachedOutlineBitmap();
                    // 删除空白页面
                    if (page.isEmpty()) {
                        _homeActivity.getDesktop().removePage(i);
                    }
                }

//                if (Setup.appSettings().getDesktopShowGrid()) {
//                    _homeActivity.getDock().setHideGrid(true);
//                    for (CellContainer cellContainer : _homeActivity.getDesktop().getPages()) {
//                        cellContainer.setHideGrid(true);
//                    }
//                }
            }
        });

        // dock drag event
//        dragNDropView.registerDropTarget(new DropTargetListener() {
//            @Override
//            public View getView() {
//                return _homeActivity.getDock();
//            }
//
//            @Override
//            public boolean onStart(Action action, PointF location, boolean isInside) {
//                return true;
//            }
//
//            @Override
//            public void onStartDrag(Action action, PointF location) {
//                // do nothing
//            }
//
//            @Override
//            public void onDrop(Action action, PointF location, Item item) {
//                if (DragAction.Action.DRAWER.equals(action)) {
//                    if (_homeActivity.getAppDrawerController()._isOpen) {
//                        return;
//                    }
//                    item.reset();
//                }
//
//                int x = (int) location.x;
//                int y = (int) location.y;
//                if (_homeActivity.getDock().addItemToPoint(item, x, y)) {
//                    _homeActivity.getDesktop().consumeLastItem();
//                    _homeActivity.getDock().consumeLastItem();
//
//                    // add the item to the database
//                    HomeActivity._db.saveItem(item, 0, Definitions.ItemPosition.Dock);
//                } else {
//                    Point pos = new Point();
//                    _homeActivity.getDock().touchPosToCoordinate(pos, x, y, item._spanX, item._spanY, false);
//                    View itemView = _homeActivity.getDock().coordinateToChildView(pos);
//                    if (itemView != null) {
//                        if (Desktop.handleOnDropOver(_homeActivity, item, (Item) itemView.getTag(), itemView, _homeActivity.getDock(), 0, Definitions.ItemPosition.Dock, _homeActivity.getDock())) {
//                            _homeActivity.getDesktop().consumeLastItem();
//                            _homeActivity.getDock().consumeLastItem();
//                        } else {
//                            Tool.toast(_homeActivity, R.string.toast_not_enough_space);
//                            _homeActivity.getDesktop().revertLastItem();
//                            _homeActivity.getDock().revertLastItem();
//                        }
//                    } else {
//                        Tool.toast(_homeActivity, R.string.toast_not_enough_space);
//                        _homeActivity.getDesktop().revertLastItem();
//                        _homeActivity.getDock().revertLastItem();
//                    }
//                }
//            }
//
//            @Override
//            public void onMove(Action action, PointF location) {
//                _homeActivity.getDock().updateIconProjection((int) location.x, (int) location.y);
//            }
//
//            @Override
//            public void onEnter(Action action, PointF location) {
//                // do nothing
//            }
//
//            @Override
//            public void onExit(Action action, PointF location) {
//                _homeActivity.getDock().clearCachedOutlineBitmap();
//                dragNDropView.cancelFolderPreview();
//            }
//
//            @Override
//            public void onEnd() {
//                _homeActivity.getDock().clearCachedOutlineBitmap();
//            }
//        });
    }
}

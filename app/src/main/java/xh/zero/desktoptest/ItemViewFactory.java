package xh.zero.desktoptest;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemViewFactory {
    private static Logger LOG = LoggerFactory.getLogger("ItemViewFactory");

    public static View getItemView(final Context context, final DesktopCallback callback, final DragAction.Action type, final Item item, Boolean showLabel) {
        View view = null;
        if (item.getType().equals(Item.Type.WIDGET)) {
//            view = getWidgetView(context, callback, type, item);
        } else {
            AppItemView.Builder builder = new AppItemView.Builder(context);
            builder.setIconSize(52);
            builder.vibrateWhenLongPress(false);
            builder.withOnLongClick(item, type, callback);
            switch (type) {
                case DRAWER:
                    builder.setLabelVisibility(true);
                    builder.setTextColor(Color.WHITE);
                    break;
                case DESKTOP:
                default:
                    builder.setLabelVisibility(true);
                    builder.setTextColor(Color.WHITE);
                    break;
            }
            if (showLabel != null) {
                boolean labelVisibility = showLabel.booleanValue();
                builder.setLabelVisibility(labelVisibility);
            }

            switch (item.getType()) {
                case APP:
                    final App app = MainActivity.Companion.getAppLoader().findItemApp(item);
                    if (app == null) break;
                    view = builder.setAppItem(item).getView();

//                    if (Setup.appSettings().getNotificationStatus()) {
//                        NotificationListener.setNotificationCallback(app.getPackageName(), (NotificationListener.NotificationCallback) view);
//                    }
                    break;
                case SHORTCUT:
                    view = builder.setShortcutItem(item).getView();
                    break;
                case GROUP:
                    view = builder.setGroupItem(context, callback, item).getView();
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    break;
                case ACTION:
//                    view = builder.setActionItem(item).getView();
                    break;
            }
        }

        // TODO find out why tag is set here
        if (view != null) {
            view.setTag(item);
        }

        return view;
    }

    public static View getItemView(final Context context, final DesktopCallback callback, final DragAction.Action type, final Item item) {
        return getItemView(context, callback, type, item, null);
    }

//    public static View getWidgetView(final Context context, final DesktopCallback callback, final DragAction.Action type, final Item item) {
//        if (MainActivity.Companion.getAppWidgetHost() == null) return null;
//
//        AppWidgetProviderInfo appWidgetInfo = MainActivity.Companion.getAppWidgetManager().getAppWidgetInfo(item.getWidgetValue());
//
//        // If we can't find the Widget, we don't want to proceed or we'll end up with a phantom on the home screen.
//        if (appWidgetInfo == null) {
//            int appWidgetId = MainActivity.Companion.getAppWidgetHost().allocateAppWidgetId();
//            if (item._label.contains(Definitions.DELIMITER)) {
//                String[] cnSplit = item._label.split(Definitions.DELIMITER);
//                ComponentName cn = new ComponentName(cnSplit[0], cnSplit[1]);
//
//                if (MainActivity.Companion.getAppWidgetManager().bindAppWidgetIdIfAllowed(appWidgetId, cn)) {
//                    appWidgetInfo = MainActivity.Companion.getAppWidgetManager().getAppWidgetInfo(appWidgetId);
//                    item.setWidgetValue(appWidgetId);
//                    MainActivity.Companion.getDb().updateItem(item);
//                } else {
//                    LOG.error("Unable to bind app widget id: {} ", cn);
//                    Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
//                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, cn);
//
//                    MainActivity.Companion.getLauncher().startActivityForResult(intent, MainActivity.REQUEST_PICK_APPWIDGET);
//                    return null;
//                }
//            } else {
//                // Delete the Widget if we don't have enough information to rehydrate it.
//                LOG.debug("Unable to identify Widget for rehydration; removing from database");
//                MainActivity.Companion.getDb().deleteItem(item, false);
//                return null;
//            }
//        }
//
//        final WidgetView widgetView = (WidgetView) MainActivity.Companion.getAppWidgetHost().createView(context, item.getWidgetValue(), appWidgetInfo);
//        widgetView.setAppWidget(item.getWidgetValue(), appWidgetInfo);
//
//        final WidgetContainer widgetContainer = new WidgetContainer(context, widgetView, item);
//
//        // TODO move this to standard DragHandler.getLongClick() method
//        // needs to be set on widgetView but use widgetContainer inside
//        widgetView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
////                if (Setup.appSettings().getDesktopLock()) {
////                    return false;
////                }
////                if (Setup.appSettings().getGestureFeedback()) {
////                    Tool.vibrate(view);
////                }
//                DragHandler.startDrag(widgetContainer, item, DragAction.Action.DESKTOP, callback);
//                return true;
//            }
//        });
//
//        widgetView.post(new Runnable() {
//            @Override
//            public void run() {
//                widgetContainer.updateWidgetOption(item);
//            }
//        });
//
//        return widgetContainer;
//    }
}

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
        AppItemView.Builder builder = new AppItemView.Builder(context);
        builder.setIconSize(52);
        builder.vibrateWhenLongPress(false);
        builder.withOnLongClick(item, type, callback);
        switch (type) {
//                case DRAWER:
//                    builder.setLabelVisibility(true);
//                    builder.setTextColor(Color.WHITE);
//                    break;
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
                break;
            case SHORTCUT:
                view = builder.setShortcutItem(item).getView();
                break;
            case GROUP:
                view = builder.setGroupItem(context, callback, item).getView();
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                break;
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
}

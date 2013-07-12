package com.example.mywebview_v1.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * status bar나 navigation/system bar와 같은 시스템 UI를 showing하고 hiding 하는 걸 돕는 유틸리티 클래스이다.
 * 이 클래스는 이번버전 호환성을 사용한다.
 * This class uses backward-compatibility
 * http://developer.android.com/training/backward-compatible-ui/index.html 이 주소의
 * Creating Backward-Compatible UIs 에 보면 다 호환되는거 볼 수 있다. 구체적으로 말하자면, 새로운
 * 장치를 위한 별도의 추상클래스가 있다
 *
 * {@link #getInstance} will return a {@link SystemUiHiderHoneycomb} instance,
 * while on older devices {@link #getInstance} will return a
 * {@link SystemUiHiderBase} instance.
 * <p>
 * For more on system bars, see <a href=
 * "http://developer.android.com/design/get-started/ui-overview.html#system-bars"
 * > System Bars</a>.
 * 
 * @see android.view.View#setSystemUiVisibility(int)
 * @see android.view.WindowManager.LayoutParams#FLAG_FULLSCREEN
 */
public abstract class SystemUiHider {
    /**
     * 이 플래그가 set됬을 때, the
     * {@link android.view.WindowManager.LayoutParams#FLAG_LAYOUT_IN_SCREEN}
     *  status bar "float"을 엑티비티 레이아웃의 탑에 만들면서 flag가 오래된 장치들에게 set 될거다.
     * 엑티비티 레이아웃의 탑에 컨트롤이 없을 때 이건  굉장히 유용하다.
     * <p>

     * This flag isn't used on newer devices because the <a
     * href="http://developer.android.com/design/patterns/actionbar.html">action
     * bar</a>, the most important structural element of an Android app, should
     * be visible and not obscured by the system UI.
     */
    public static final int FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES = 0x1;

    /**
     * When this flag is set, {@link #show()} and {@link #hide()} will toggle
     * the visibility of the status bar. If there is a navigation bar, show and
     * hide will toggle low profile mode.
     */
    public static final int FLAG_FULLSCREEN = 0x2;

    /**
     * When this flag is set, {@link #show()} and {@link #hide()} will toggle
     * the visibility of the navigation bar, if it's present on the device and
     * the device allows hiding it. In cases where the navigation bar is present
     * but cannot be hidden, show and hide will toggle low profile mode.
     */
    public static final int FLAG_HIDE_NAVIGATION = FLAG_FULLSCREEN | 0x4;

    /**
     * The activity associated with this UI hider object.
     */
    protected Activity mActivity;

    /**
     * The view on which {@link View#setSystemUiVisibility(int)} will be called.
     */
    protected View mAnchorView;

    /**
     * The current UI hider flags.
     * 
     * @see #FLAG_FULLSCREEN
     * @see #FLAG_HIDE_NAVIGATION
     * @see #FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
     */
    protected int mFlags;

    /**
     * The current visibility callback.
     */
    protected OnVisibilityChangeListener mOnVisibilityChangeListener = sDummyListener;

    /**
     * Creates and returns an instance of {@link SystemUiHider} that is
     * appropriate for this device. The object will be either a
     * {@link SystemUiHiderBase} or {@link SystemUiHiderHoneycomb} depending on
     * the device.
     * 
     * @param activity The activity whose window's system UI should be
     *            controlled by this class.
     * @param anchorView The view on which
     *            {@link View#setSystemUiVisibility(int)} will be called.
     * @param flags Either 0 or any combination of {@link #FLAG_FULLSCREEN},
     *            {@link #FLAG_HIDE_NAVIGATION}, and
     *            {@link #FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES}.
     */
    public static SystemUiHider getInstance(Activity activity, View anchorView, int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new SystemUiHiderHoneycomb(activity, anchorView, flags);
        } else {
            return new SystemUiHiderBase(activity, anchorView, flags);
        }
    }

    protected SystemUiHider(Activity activity, View anchorView, int flags) {
        mActivity = activity;
        mAnchorView = anchorView;
        mFlags = flags;
    }

    /**
     * Sets up the system UI hider. Should be called from
     * {@link Activity#onCreate}.
     */
    public abstract void setup();

    /**
     * Returns whether or not the system UI is visible.
     */
    public abstract boolean isVisible();

    /**
     * Hide the system UI.
     */
    public abstract void hide();

    /**
     * Show the system UI.
     */
    public abstract void show();

    /**
     * Toggle the visibility of the system UI.
     */
    public void toggle() {
        if (isVisible()) {
            hide();
        } else {
            show();
        }
    }

    /**
     * Registers a callback, to be triggered when the system UI visibility
     * changes.
     */
    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        if (listener == null) {
            listener = sDummyListener;
        }

        mOnVisibilityChangeListener = listener;
    }

    /**
     * A dummy no-op callback for use when there is no other listener set.
     */
    private static OnVisibilityChangeListener sDummyListener = new OnVisibilityChangeListener() {
        @Override
        public void onVisibilityChange(boolean visible) {
        }
    };

    /**
     * A callback interface used to listen for system UI visibility changes.
     */
    public interface OnVisibilityChangeListener {
        /**
         * Called when the system UI visibility has changed.
         * 
         * @param visible True if the system UI is visible.
         */
        public void onVisibilityChange(boolean visible);
    }
}

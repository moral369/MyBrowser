package browser.dnm.cross.mybrowser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private final int VIEW_SIZE = 5;
    static ArrayList<String> openList = null;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.content_browser);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(VIEW_SIZE);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        openList = new ArrayList<String>();
        for(int i = 0; i < VIEW_SIZE; i++) {
            openList.add("Loding...");
        }

        mViewPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int state) {
                AvLog.i("state = "+state);
            }
        });
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            // page가 변경되었을 때 변경된 페이지 콜백
            @Override
            public void onPageSelected(int selectedPage) {
                // TODO Auto-generated method stub
                AvLog.i("selectedPage = "+selectedPage);
            }

            //페이지를 넘기는 중 현재 페이지와 위치정보 콜백
            @Override
            public void onPageScrolled(int currentPage, float locationA, int locationB) {
                // TODO Auto-generated method stub
            }


            //그냥 상태는 0, 스크롤 중일 때 1, 스크롤 중 터치를 놓았을 때 2
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
                AvLog.i("arg0 = "+arg0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null && resultCode == 1) {
            AvLog.i("current view pager item = "+mViewPager.getCurrentItem());
            mViewPager.setCurrentItem(intent.getIntExtra("position", mViewPager.getCurrentItem()));
        }
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter  {

        private static final String TAG = "SectionsPagerAdapter";
        Preferences pref;
        AutoCompleteTextView actv_address_bar;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            AvLog.i("");
            pref = new Preferences(getApplicationContext());
            actv_address_bar = (AutoCompleteTextView) findViewById(R.id.actv_address_bar);
        }

        @Override
        public Fragment getItem(int position) {
            // 주어진 페이지를 위한 프레그먼트가 초기화될때 호출되는 getItem입니다.
            // static inner class로 아래 정의한 DummySectionFragment가 페이지 번호인 인수와 함께
            // 리턴 됨
            // 로그 찍어보니까 처음 어플 실행될하면 페이져 양만큼 찍히네 지정한 페이저 숫자가 5개니까 position이 0부터 4까지(5개) 찍혔다.
            AvLog.i("");
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);

//            ((DummySectionFragment) fragment).position = position;
            return fragment;
        }

        @Override
        public int getCount() {
            AvLog.i("");
            return VIEW_SIZE;
        }

    }

    public static class DummySectionFragment extends Fragment implements View.OnClickListener {

        public static final String ARG_SECTION_NUMBER = "section_number";
        private Content mWebview;
        public boolean mWaitingForAppFinishFlag = false;
        private final int FLAG = 0;
        private AutoCompleteTextView actv_address_bar;
        public Handler mHandler;
        private WebSettings set;
        private String homeUrl;
        private ImageView btn_text_clear;
        public Preferences pref;
        private View rootView;

        String[] auto_complete_list;
        public int position;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            AvLog.i("");
            setHasOptionsMenu(true);
            pref = new Preferences(getActivity());
            auto_complete_list = new String[]{"Naver.com", "Nate.com", "Google.co.kr", "unichal.com", "microsoft.com"};
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            AvLog.i("");
            rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            mWebview = (Content) rootView.findViewById(R.id.content);
            mWebview.setWebViewClient(new WebClient());
            mWebview.setWebChromeClient(new WebChrome());
            mWebview.getHorizontalFadingEdgeLength();
            mWebview.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                    if (keyEvent.getAction() != keyEvent.ACTION_DOWN) {
                        return false;
                    }

                    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
                        mWebview.goBack();
                        return true;

                    } else if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack() == false && !mWaitingForAppFinishFlag) {
                        mWaitingForAppFinishFlag = true;
                        mHandler.sendEmptyMessageDelayed(FLAG, 2000);
                        return true;

                    } else if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack() == false && mWaitingForAppFinishFlag) {
                        getActivity().finish();
                        Toast.makeText(getActivity(), "한번 더 누르면 종료합니다.", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }
            });

            set = mWebview.getSettings();
            set.setJavaScriptEnabled(true);
            set.setBuiltInZoomControls(true);
            set.setDisplayZoomControls(false);

            homeUrl = pref.getString("Home", null);

            mWebview.init((RelativeLayout) rootView.findViewById(R.id.url_bar), (LinearLayout) rootView.findViewById(R.id.under_bar), null);
            rootView.findViewById(R.id.btn_put_here_to_favorite_list).setOnClickListener(this);
            rootView.findViewById(R.id.btn_previous).setOnClickListener(this);
            rootView.findViewById(R.id.btn_next).setOnClickListener(this);
            rootView.findViewById(R.id.btn_refresh).setOnClickListener(this);

            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    AvLog.i( "Enter into mHandler");
                    switch (msg.what) {
                        case FLAG:
                            mWaitingForAppFinishFlag = false;
                            break;
                        default:
                            break;
                    }
                }
            };

            if (homeUrl == null) {
                homeUrl = pref.getString(getActivity(), "MyHome");
                mWebview.loadUrl(homeUrl);
            } else {
                mWebview.loadUrl(homeUrl);
            }

            btn_text_clear = (ImageView) rootView.findViewById(R.id.btn_text_clear);
            actv_address_bar = (AutoCompleteTextView) rootView.findViewById(R.id.actv_address_bar);
            actv_address_bar.setText(homeUrl);
            actv_address_bar.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, auto_complete_list));
            actv_address_bar.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        loadUrl(actv_address_bar.getText().toString());
                        return true;
                    }
                    return false;
                }
            });


            actv_address_bar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AvLog.i("position = "+position);
                    loadUrl(actv_address_bar.getText().toString());
                }
            });
            btn_text_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do stuff for signInButtonClick
                    // 이걸 즐겨찾기로 바꿔야겠다.
                    actv_address_bar.getEditableText().clear();
                    actv_address_bar.getText().clear();

                }
            });

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);

            inflater.inflate(R.menu.content_actions, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            AvLog.i( "onOptionsItemSelected in fragment");
            switch (item.getItemId()) {

                case R.id.add_to_bookmark:
                    Browser.saveBookmark(getActivity(), mWebview.getTitle(), mWebview.getUrl());
                    break;

                case R.id.move_to_designated_home:
                    homeUrl = pref.getString(getActivity(), "MyHome");
                    mWebview.loadUrl(homeUrl);
                    break;

                case R.id.change_home_to_here:
                    homeUrl = mWebview.getUrl();

                    pref.putString("MyHome", homeUrl);
                    Toast.makeText(getActivity(), "Changed Hompage...",
                            Toast.LENGTH_SHORT).show();
                    break;

                case R.id.favorite_list:
                    Intent intent = new Intent(getActivity(), BookmarkActivity.class);
                    startActivityForResult(intent, 0);

                    break;

                case R.id.show_all_pager_location:
                    intent = new Intent(getActivity(), PageActivity.class);
                    startActivityForResult(intent, 1);
                    break;

            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode,
                                     Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (data != null && resultCode == 0) {
                String url = data.getStringExtra("URL");
                actv_address_bar.setText(url);
                mWebview.loadUrl(url);
            }
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.btn_put_here_to_favorite_list:
                    if(actv_address_bar.getText().toString()!=null) {
                        AvLog.i("");
                        loadUrl(actv_address_bar.getText().toString());
                    }
                    break;

                case R.id.btn_previous:
                    if(mWebview.canGoBack()) {
                        mWebview.goBack();
                    }
                    break;

                case R.id.btn_next:
                    if(mWebview.canGoForward()) {
                        mWebview.goForward();
                    }
                    break;

                case R.id.btn_refresh:
                    mWebview.reload();
                    break;

                case R.id.btn_text_clear:
                    actv_address_bar.setText("");
                    break;

                default:
                    break;
            }
        }

        public void loadUrl(String url) {
            AvLog.i("url = "+url);
            String sub = url.substring(0, 4);
            AvLog.i("sub = "+sub);
            if (sub.equals("http"))
                mWebview.loadUrl(url);
            else
                mWebview.loadUrl("http://" + url);

        }

        private class WebClient extends WebViewClient {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                AvLog.i("url = " + url);
                openList.set(position, mWebview.getUrl());
                actv_address_bar.setText(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                AvLog.i("");
            }
        }

        private class WebChrome extends WebChromeClient {

            ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.progressBar);

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                pb.setProgress(newProgress);
                if (newProgress == 100) {
                    AvLog.i( "newProgress==100");
                    pb.setVisibility(View.GONE);
                } else {
                    pb.setVisibility(View.VISIBLE);
                }
            }
        }

        public static class Content extends WebView implements View.OnSystemUiVisibilityChangeListener {

            RelativeLayout url_bar;
            LinearLayout under_bar;
            int mBaseSystemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN;

            int mLastSystemUiVis;
            boolean first_retrived = false;

            Handler contentHandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            setNavVisibility(true);
                            break;
                        case 2:
                            break;
                        default:
                            break;
                    }
                };
            };

            public Content(Context context, AttributeSet attrs) {
                super(context, attrs);
                setOnSystemUiVisibilityChangeListener(this);
            }

            public void init(RelativeLayout title, LinearLayout viewById, ZoomControls zoomControls) {
                url_bar = title;
                under_bar = viewById;
//                this.zoomControls = zoomControls;
                setNavVisibility(true);
            }

            void setNavVisibility(boolean visible) {
                AvLog.i("visible ? =  "+visible);
                int newVis = mBaseSystemUiVisibility;
                if (!visible) {
                    newVis |= SYSTEM_UI_FLAG_LOW_PROFILE | SYSTEM_UI_FLAG_FULLSCREEN;
                    first_retrived = false;
                }
                final boolean changed = newVis == getSystemUiVisibility();

                if (changed || visible) {
                    contentHandler.removeMessages(0);
                }

                setSystemUiVisibility(newVis);
                url_bar.setVisibility(visible ? VISIBLE : INVISIBLE);
                under_bar.setVisibility(visible ? VISIBLE : INVISIBLE);

            }

            @Override
            protected void onWindowVisibilityChanged(int visibility) {
                super.onWindowVisibilityChanged(visibility);
                setNavVisibility(true);
                contentHandler.sendEmptyMessageDelayed(0, 2000);
            }

            @Override
            protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
                if (scrollY == 0 && !first_retrived) {
                    int curVis = getSystemUiVisibility();
                    setNavVisibility(true);
                    first_retrived = true;
                }
            }

            @Override
            public void onSystemUiVisibilityChange(int visibility) {

                int diff = mLastSystemUiVis ^ visibility;
                mLastSystemUiVis = visibility;
                if ((diff & SYSTEM_UI_FLAG_LOW_PROFILE) != 0
                        && (visibility & SYSTEM_UI_FLAG_LOW_PROFILE) == 0) {
                    setNavVisibility(true);
                }
            }

            @Override
            protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                setNavVisibility(false);

            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                // TODO Auto-generated method stub

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        contentHandler.sendEmptyMessageDelayed(2, 2000);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        contentHandler.sendEmptyMessage(2);
                        break;

                    default:
                        break;
                }

                return super.onTouchEvent(event);
            }

        }
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private float MIN_SCALE = 0.85f;
        private float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private  float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}

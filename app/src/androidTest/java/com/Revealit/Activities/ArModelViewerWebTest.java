package com.Revealit.Activities;

import android.content.res.Resources;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.collection.SimpleArrayMap;
import androidx.collection.SparseArrayCompat;
import androidx.core.app.ComponentActivity;
import androidx.fragment.app.FragmentController;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.test.rule.ActivityTestRule;

import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;

public class ArModelViewerWebTest {
    @Mock
    ArModelViewerWeb mActivity;
    @Mock
    ArModelViewerWeb mContext;
    @Mock
    SessionManager mSessionManager;
    @Mock
    DatabaseHelper mDatabaseHelper;
    @Mock
    WebView webView;
    @Mock
    ProgressBar progressbar;
    @Mock
    ImageView imgARview;
    @Mock
    ImageView imgCancel;
    @Mock
    AppCompatDelegate mDelegate;
    @Mock
    Resources mResources;
    @Mock
    FragmentController mFragments;
    @Mock
    LifecycleRegistry mFragmentLifecycleRegistry;
    @Mock
    SparseArrayCompat<String> mPendingFragmentActivityResults;
    @Mock
    LifecycleRegistry mLifecycleRegistry;
    //Field mSavedStateRegistryController of type SavedStateRegistryController - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    ViewModelStore mViewModelStore;
    @Mock
    ViewModelProvider.Factory mDefaultFactory;
    //Field mOnBackPressedDispatcher of type OnBackPressedDispatcher - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    SimpleArrayMap<Class<? extends ComponentActivity.ExtraData>, ComponentActivity.ExtraData> mExtraDataMap;

    @Rule
    public ActivityTestRule<ArModelViewerWeb> mArModelViewerWeb = new ActivityTestRule(ArModelViewerWeb.class);


    @Before
    public void setUp() {

        // MockitoAnnotations.initMocks(this);
        mActivity = mArModelViewerWeb.getActivity();
        mContext = mArModelViewerWeb.getActivity();

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        webView = (WebView) mActivity.findViewById(R.id.webView);
        progressbar = (ProgressBar) mActivity.findViewById(R.id.progressbar);
        imgCancel =(ImageView)  mActivity.findViewById(R.id.imgCancel);
        imgARview =(ImageView)  mActivity.findViewById(R.id.imgARview);

    }

    @Test
    public void testPreconditions() {

        assertNotNull(mSessionManager);
        assertNotNull(mDatabaseHelper);
        assertNotNull(webView);
        assertNotNull(progressbar);
        assertNotNull(imgCancel);
        assertNotNull(imgARview);
    }


}


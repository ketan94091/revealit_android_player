package com.Revealit.Activities;

import android.content.Context;
import android.content.res.Resources;
import android.widget.FrameLayout;

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
import com.Revealit.SqliteDatabase.DatabaseHelper;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;

public class ARviewActivityTest {
    @Mock
    FrameLayout frameOverlay;
    @Mock
    ARviewActivity mActivity;
    @Mock
    Context mContext;
    @Mock
    SessionManager mSessionManager;
    @Mock
    DatabaseHelper mDatabaseHelper;
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
    public ActivityTestRule<ARviewActivity> mARviewActivity = new ActivityTestRule(ARviewActivity.class);

    @Before
    public void setUp() {

        mActivity =mARviewActivity.getActivity();
    }


}


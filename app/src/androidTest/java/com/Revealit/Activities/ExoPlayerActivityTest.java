package com.Revealit.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.exoplayer2.SimpleExoPlayer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExoPlayerActivityTest {
    @Mock
    ExoPlayerActivity mActivity;
    @Mock
    Context mContext;
    @Mock
    SessionManager mSessionManager;
    @Mock
    DatabaseHelper mDatabaseHelper;
    //Field exoPlayer of type SimpleExoPlayerView - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    SimpleExoPlayer player;
    @Mock
    ProgressBar progress;
    @Mock
    ImageView imgShareImage;
    @Mock
    ImageView imgVoulume;
    @Mock
    ImageView imgBackArrow;
    @Mock
    ImageView imgRecipe;
    @Mock
    ImageView imgInfluencer;
    @Mock
    ImageView imgShare;
    @Mock
    LinearLayout linearRecipeShareInfluencer;
    @Mock
    LinearLayout linearMainBottomController;
    @Mock
    SeekBar seekFontSize;
    @Mock
    SeekBar ckVolumeBar;
    @Mock
    AudioManager audioManager;
    @Mock
    FrameLayout frameOverlay;
    @Mock
    ImageView imgDynamicCoordinateView;
    @Mock
    List<DotsLocationsModel.Datum> locationData;
    @Mock
    RelativeLayout relativeCaptureImageWithText;
    @Mock
    RelativeLayout relativeShareView;
    @Mock
    EditText edtTextOnCaptureImage;
    @Mock
    TextView txtVendorName;
    @Mock
    TextView txtCancel;
    @Mock
    TextView txtShare;
    //Field savedBitMap of type Bitmap - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    ProgressBar progressLoadData;
    @Mock
    AlertDialog mAlertDialogRecipe;
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

    private int deviceScreenWidth , deviceScreenHeight;

    @Rule
    public ActivityTestRule<ExoPlayerActivity> mExoPlayerActivity = new ActivityTestRule(ExoPlayerActivity.class);


    @Before
    public void setUp() {

       // MockitoAnnotations.initMocks(this);
        mActivity = mExoPlayerActivity.getActivity();
        mContext = mExoPlayerActivity.getActivity();

        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        deviceScreenWidth = metrics.widthPixels;
        deviceScreenHeight = metrics.heightPixels;

        mActivity.heightVideo = 1200;
        mActivity.widthVideo =510;

        //SET STATIC MEDIA URL
        mActivity.strMediaURL = "https://revtesting.sgp1.digitaloceanspaces.com/video_media/_02d78c5fadffc4a277f27a1002bca8ea.mp4";

    }


    @Test
    public void testGetScreenResolutionXCoordinate() throws Exception {

        float xCoordinate =112.12f;

        float result = mActivity.getScreenResolutionX(mContext, xCoordinate);

        float expectedResult  = (xCoordinate * deviceScreenWidth) / mActivity.widthVideo;

        assertEquals("RESULT X- COORDINATE", expectedResult, result, 0.5);

    }

    @Test
    public void testGetScreenResolutionYCoordinate() throws Exception {

        float yCoordinate = 80f;

        float result = mActivity.getScreenResolutionY(mContext, yCoordinate);


        float expectedResult  = (yCoordinate * deviceScreenHeight) / mActivity.heightVideo;

        assertEquals("RESULT Y- COORDINATE", expectedResult, result, 0.5);

    }


}


package com.Revealit.Activities;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest  {

    @Mock
    Activity mActivity;
    @Mock
    Context mContext;
    @Mock
    NavigationView navigationView;
    @Mock
    SessionManager mSessionManager;
    @Mock
    DatabaseHelper mDatabaseHelper;
    @Mock
    WebView webView;
    @Mock
    EditText edtPassword;
    @Mock
    EditText edtUsername;
    @Mock
    TextView txtLogin;

    //Field a of type a - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field b of type YouTubePlayerView - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field d of type Bundle - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @InjectMocks
    LoginActivityActivity loginActivityActivity = new LoginActivityActivity();


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testCheckValidation() throws Exception {

        boolean result = loginActivityActivity.checkValidation("xxx","xxx");
        Assert.assertEquals(true, result);
    }

}

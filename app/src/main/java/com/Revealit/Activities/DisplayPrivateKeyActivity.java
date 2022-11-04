package com.Revealit.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.CommonClasse.SessionManager;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.Revealit.Utils.Cryptography;
import com.Revealit.Utils.DeCryptor;
import com.Revealit.Utils.EnCryptor;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DisplayPrivateKeyActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DisplayPrivateKeyActivi";
    private Activity mActivity;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private SessionManager mSessionManager;
    private RelativeLayout relativeBack;
    private ImageView imgCopy,imgCopyPhrase;
    private TextView txtPrivateKeyPhrase1,txtPrivateKeyPhrase2,txtPrivateKeyPhrase3,txtPrivateKeyPhrase4,txtPrivateKeyPhrase5,txtPrivateKeyPhrase6,txtPrivateKeyPhrase7,txtPrivateKeyPhrase8,txtPrivateKeyPhrase9,txtPrivateKeyPhrase10,txtPrivateKeyPhrase11,txtPrivateKeyPhrase12,txtPrivateKey;
    private DeCryptor decryptor;
    private EnCryptor encryptor;
    private String mMnemonics,mPrivateKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_private_key);

        setId();
        setOnClicks();
    }


    private void setId(){
        
        mActivity = DisplayPrivateKeyActivity.this;
        mContext = DisplayPrivateKeyActivity.this;

        mSessionManager = new SessionManager(mContext);
        mSessionManager.openSettings();

        mDatabaseHelper = new DatabaseHelper(mContext);
        mDatabaseHelper.open();

        relativeBack =(RelativeLayout)findViewById(R.id.relativeBack);

        imgCopy =(ImageView)findViewById(R.id.imgCopy);
        imgCopyPhrase =(ImageView)findViewById(R.id.imgCopyPhrase);

        txtPrivateKey=(TextView)findViewById(R.id.txtPrivateKey);
        txtPrivateKeyPhrase1=(TextView)findViewById(R.id.txtPrivateKeyPhrase1);
        txtPrivateKeyPhrase2=(TextView)findViewById(R.id.txtPrivateKeyPhrase2);
        txtPrivateKeyPhrase3=(TextView)findViewById(R.id.txtPrivateKeyPhrase3);
        txtPrivateKeyPhrase4=(TextView)findViewById(R.id.txtPrivateKeyPhrase4);
        txtPrivateKeyPhrase5=(TextView)findViewById(R.id.txtPrivateKeyPhrase5);
        txtPrivateKeyPhrase6=(TextView)findViewById(R.id.txtPrivateKeyPhrase6);
        txtPrivateKeyPhrase7=(TextView)findViewById(R.id.txtPrivateKeyPhrase7);
        txtPrivateKeyPhrase8=(TextView)findViewById(R.id.txtPrivateKeyPhrase8);
        txtPrivateKeyPhrase9=(TextView)findViewById(R.id.txtPrivateKeyPhrase9);
        txtPrivateKeyPhrase10=(TextView)findViewById(R.id.txtPrivateKeyPhrase10);
        txtPrivateKeyPhrase11=(TextView)findViewById(R.id.txtPrivateKeyPhrase11);
        txtPrivateKeyPhrase12=(TextView)findViewById(R.id.txtPrivateKeyPhrase12);

        try{
            //OPEN KEYSTORE
            Cryptography mCryptography = new Cryptography(mSessionManager.getPreference(Constants.KEY_REVEALIT_PRIVATE_KEY));

            //GET PRIVATE KEY
            mPrivateKey = mCryptography.decrypt(mSessionManager.getPreference(Constants.KEY_PRIVATE_KEY));

            //GET PUBLIC KEY
            String mPublicKey = mCryptography.decrypt(mSessionManager.getPreference(Constants.KEY_PUBLIC_KEY));

            //GET MNEMONICS
            mMnemonics = mCryptography.decrypt(mSessionManager.getPreference(Constants.KEY_MNEMONICS));

            //SET PRIVATE KEY
            txtPrivateKey.setText(mPrivateKey);

            //SPLIT MNEMONIC
            String[] animalsArray = mMnemonics.split(" ");
            txtPrivateKeyPhrase1.setText("1."+animalsArray[0]);
            txtPrivateKeyPhrase2.setText("2."+animalsArray[1]);
            txtPrivateKeyPhrase3.setText("3."+animalsArray[2]);
            txtPrivateKeyPhrase4.setText("4."+animalsArray[3]);
            txtPrivateKeyPhrase5.setText("5."+animalsArray[4]);
            txtPrivateKeyPhrase6.setText("6."+animalsArray[5]);
            txtPrivateKeyPhrase7.setText("7."+animalsArray[6]);
            txtPrivateKeyPhrase8.setText("8."+animalsArray[7]);
            txtPrivateKeyPhrase9.setText("9."+animalsArray[8]);
            txtPrivateKeyPhrase10.setText("10."+animalsArray[9]);
            txtPrivateKeyPhrase11.setText("11."+animalsArray[10]);
            txtPrivateKeyPhrase12.setText("12."+animalsArray[11]);

        }catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

    }

    private void setOnClicks() {

        relativeBack.setOnClickListener(this);
        imgCopy.setOnClickListener(this);
        imgCopyPhrase.setOnClickListener(this);

    }

    @Override
    public void onClick(View mView) {

        switch (mView.getId()){

            case R.id.relativeBack:
                finish();
                break;

            case R.id.imgCopy:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getString(R.string.strPrivateKey),mPrivateKey);
                    clipboard.setPrimaryClip(clip);

                    //TOAST MSG
                    CommonMethods.displayToast(mContext, getString(R.string.strUsernameCopied));
                break;

            case R.id.imgCopyPhrase:
                    ClipboardManager clipboardPhras = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipPhras = ClipData.newPlainText(getString(R.string.strPhras),mMnemonics);
                    clipboardPhras.setPrimaryClip(clipPhras);

                    //TOAST MSG
                    CommonMethods.displayToast(mContext, getString(R.string.strUsernameCopied));
                break;
        }

    }
}
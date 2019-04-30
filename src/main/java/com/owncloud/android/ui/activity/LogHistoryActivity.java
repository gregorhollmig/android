/*
 *   ownCloud Android client application
 *
 *   Copyright (C) 2015 ownCloud Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.owncloud.android.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.owncloud.android.R;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.ui.dialog.LoadingDialog;
import com.owncloud.android.utils.ThemeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class LogHistoryActivity extends ToolbarActivity {

    private static final String MAIL_ATTACHMENT_TYPE = "text/plain";

    private static final String KEY_LOG_TEXT = "LOG_TEXT";

    private static final String TAG = LogHistoryActivity.class.getSimpleName();

    private static final String DIALOG_WAIT_TAG = "DIALOG_WAIT";

    private Unbinder unbinder;

    private String logPath = Log_OC.getLogPath();
    private File logDir;
    private String logText;

    @BindView(R.id.deleteLogHistoryButton)
    Button deleteHistoryButton;

    @BindView(R.id.sendLogHistoryButton)
    Button sendHistoryButton;

    @BindView(R.id.logTV)
    TextView logTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.log_send_file);
        unbinder = ButterKnife.bind(this);

        setupToolbar();

        setTitle(getText(R.string.actionbar_logger));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MaterialButton deleteHistoryButton = findViewById(R.id.deleteLogHistoryButton);
        MaterialButton sendHistoryButton = findViewById(R.id.sendLogHistoryButton);
        sendHistoryButton.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
        sendHistoryButton.setBackgroundTintList(ColorStateList.valueOf(ThemeUtils.primaryColor(this, true)));
        deleteHistoryButton.setTextColor(ThemeUtils.primaryColor(this, true));

        if (savedInstanceState == null) {
            if (logPath != null) {
                logDir = new File(logPath);
            }

            if (logDir != null && logDir.isDirectory()) {
                // Show a dialog while log data is being loaded
                showLoadingDialog();

                // Start a new thread that will load all the log data
                LoadingLogTask task = new LoadingLogTask(logTV);
                task.execute();
            }
        } else {
            logText = savedInstanceState.getString(KEY_LOG_TEXT);
            logTV.setText(logText);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean retval = true;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                retval = super.onOptionsItemSelected(item);
                break;
        }
        return retval;
    }

    @OnClick(R.id.deleteLogHistoryButton)
    void deleteHistoryLogging() {
        Log_OC.deleteHistoryLogging();
        finish();
    }

    /**
     * Start activity for sending email with logs attached
     */
    @OnClick(R.id.sendLogHistoryButton)
    void sendMail() {
        String emailAddress = getString(R.string.mail_logger);

        ArrayList<Uri> uris = new ArrayList<>();

        // Convert from paths to Android friendly Parcelable Uri's
        for (String file : Log_OC.getLogFileNames()) {
            File logFile = new File(logPath, file);
            if (logFile.exists()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    uris.add(Uri.fromFile(logFile));
                } else {
                    uris.add(FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), logFile));
                }
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        String subject = String.format(getString(R.string.log_send_mail_subject), getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType(MAIL_ATTACHMENT_TYPE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(findViewById(android.R.id.content), R.string.log_send_no_mail_app, Snackbar.LENGTH_LONG).show();
            Log_OC.i(TAG, "Could not find app for sending log history.");
        }

    }

    /**
     * Class for loading the log data async
     */
    private class LoadingLogTask extends AsyncTask<String, Void, String> {
        private final WeakReference<TextView> textViewReference;

        LoadingLogTask(TextView logTV) {
            // Use of a WeakReference to ensure the TextView can be garbage collected
            textViewReference = new WeakReference<>(logTV);
        }

        protected String doInBackground(String... args) {
            return readLogFile();
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                final TextView logTV = textViewReference.get();
                if (logTV != null) {
                    logText = result;
                    logTV.setText(logText);
                    dismissLoadingDialog();
                }
            }
        }

        /**
         * Read and show log file info
         */
        private String readLogFile() {

            String[] logFileName = Log_OC.getLogFileNames();

            //Read text from files
            StringBuilder text = new StringBuilder();

            BufferedReader br = null;
            try {
                String line;

                for (int i = logFileName.length - 1; i >= 0; i--) {
                    File file = new File(logPath, logFileName[i]);
                    if (file.exists()) {
                        // Check if FileReader is ready
                        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),
                                                                                         Charset.forName("UTF-8"))) {
                            if (inputStreamReader.ready()) {
                                br = new BufferedReader(inputStreamReader);
                                while ((line = br.readLine()) != null) {
                                    // Append the log info
                                    text.append(line);
                                    text.append('\n');
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Log_OC.d(TAG, e.getMessage());

            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        // ignore
                        Log_OC.d(TAG, "Error closing log reader", e);
                    }
                }
            }

            return text.toString();
        }
    }

    /**
     * Show loading dialog
     */
    public void showLoadingDialog() {
        // Construct dialog
        LoadingDialog loading = LoadingDialog.newInstance(getResources().getString(R.string.log_progress_dialog_text));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        loading.show(ft, DIALOG_WAIT_TAG);
    }

    /**
     * Dismiss loading dialog
     */
    public void dismissLoadingDialog() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(DIALOG_WAIT_TAG);
        if (frag != null) {
            LoadingDialog loading = (LoadingDialog) frag;
            loading.dismissAllowingStateLoss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (isChangingConfigurations()) {
            // global state
            outState.putString(KEY_LOG_TEXT, logText);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

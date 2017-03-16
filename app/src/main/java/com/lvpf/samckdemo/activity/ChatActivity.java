package com.lvpf.samckdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.adapter.MessageListAdapter;
import com.lvpf.samckdemo.manager.ContacterManager;
import com.lvpf.samckdemo.manager.XmppConnectionManager;
import com.lvpf.samckdemo.model.IMMessage;
import com.lvpf.samckdemo.model.User;
import com.lvpf.samckdemo.model.UserInfo4XMPP;
import com.lvpf.samckdemo.utils.CommonUtils;
import com.lvpf.samckdemo.utils.EmoticonsEditText;
import com.lvpf.samckdemo.utils.LogUtils;
import com.lvpf.samckdemo.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 聊天app
 */
public class ChatActivity extends AChatActivity implements View.OnClickListener {
    private Button btn_chat_send, btn_chat_add, btn_chat_keyboard, btn_speak,
            btn_chat_voice;
    private ListView chat_list;//聊天列表
    private RelativeLayout layout_record;//voice
    private ImageView iv_record;
    private TextView tv_voice_tips;
    private MessageListAdapter chatAdapter;
    private TextView tv_picture, tv_camera;
    private User user;//
    private LinearLayout layout_more, layout_add;
    private EmoticonsEditText messageInput = null;
    private static String TAG = "ChatActivity";
    private static final String PATH = "/sdcard/MyVoiceForder/Record/";
    private String localCameraPath = "";

    private Handler mHandler = new Handler();
    private Toolbar tool_bar;
    /**
     * 语音文件保存路径
     */
    private String mFileName = null;
    /**
     * 用于完成录音
     */
    private MediaRecorder mRecorder = new MediaRecorder();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initview();
        initBottomView();
    }

    //view初始化
    private void initview() {
        tool_bar= (Toolbar) findViewById(R.id.tool_bar);
        tool_bar.setTitle(to);
        tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chat_list = (ListView) findViewById(R.id.chat_list);
        chatAdapter = new MessageListAdapter(ChatActivity.this, getMessages());
        chat_list.setAdapter(chatAdapter);
        chat_list.setSelection(ListView.FOCUS_DOWN);
        user = ContacterManager.getByUserJid(to, XmppConnectionManager
                .getInstance().getConnection());


    }

    //底部view初始化
    private void initBottomView() {

        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
        btn_chat_add.setOnClickListener(this);
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);

        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);

        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_picture.setOnClickListener(this);
        tv_camera.setOnClickListener(this);

        btn_speak = (Button) findViewById(R.id.btn_speak);
        btn_speak.setOnTouchListener(new View.OnTouchListener() {
            long beforeTime;
            long afterTime;
            int timeDistance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String dir = "";//文件名
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        beforeTime = System.currentTimeMillis();
                        try {
                            v.setPressed(true);
                            layout_record.setVisibility(View.VISIBLE);
                            tv_voice_tips
                                    .setText(getString(R.string.voice_cancel_tips));

                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    layout_record.setVisibility(View.VISIBLE);
                                }
                            }, 300);
                            // recordManager.startRecording(targetId);
                        } catch (Exception e) {
                        }
                        dir = startVoice();
                        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        afterTime = System.currentTimeMillis();
                        System.out.println(timeDistance + "声音录制时间");
                        v.setPressed(false);
                        layout_record.setVisibility(View.INVISIBLE);
                        String voiceFile = CommonUtils.VOICE_SIGN
                                + CommonUtils.GetImageStr(mFileName) + "@" + dir
                                + CommonUtils.VOICE_SIGN;
                        mHandler.removeCallbacks(mSleepTask);
                        mHandler.removeCallbacks(mPollTask);
                        stopVoice();
                        if ("".equals(mFileName)) {
                            Toast.makeText(ChatActivity.this, "不能为空",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                if ((afterTime - beforeTime) < 500) {
                                    Toast.makeText(getApplicationContext(),
                                            "录音时间太短！", Toast.LENGTH_SHORT).show();
                                    File file = new File(mFileName);
                                    file.delete();
                                } else if ((afterTime - beforeTime) > 60000) {
                                    Toast.makeText(getApplicationContext(),
                                            "录音时间太长！", Toast.LENGTH_SHORT).show();
                                    File file = new File(mFileName);
                                    file.delete();
                                } else {
                                    String time = String.valueOf((afterTime - beforeTime) / 1000);
                                    String mesStr = CommonUtils.VOICE_SIGN + CommonUtils.GetImageStr(mFileName) + "@" + dir + "@" + time + CommonUtils.VOICE_SIGN;
                                    sendMessage(mesStr);
                                }

                            } catch (Exception e) {
                                Toast.makeText(ChatActivity.this, "信息发送失败", Toast.LENGTH_SHORT).show();
                            }
                            closeInput();
                        }
                        iv_record.setImageResource(R.drawable.chat_icon_voice1);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        // initVoiceAnimRes();


        messageInput = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
        messageInput.setOnClickListener(this);
        messageInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_add.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                        btn_chat_add.setVisibility(View.VISIBLE);
                    } else {
                        btn_chat_add.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    /**
     * 开始录音
     */
    private String startVoice() {
        String dir = String.valueOf(System.currentTimeMillis());
        // 设置录音保存路径
        mFileName = PATH + dir + ".amr";
        System.out.println(mFileName);
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            Log.i(TAG, "SD Card is not mounted,It is  " + state + ".");
        }
        File directory = new File(mFileName).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            Log.i(TAG, "Path to file could not be created");
        }
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        } else {
            mRecorder.reset();
        }

        // mRecorder =
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mRecorder.start();
        return dir;
    }

    /**
     * 停止录音
     */
    private void stopVoice() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        // Toast.makeText(getApplicationContext(), "保存录音" + mFileName,
        // 0).show();
    }

    private static final int POLL_INTERVAL = 300;
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mRecorder.getMaxAmplitude() / 2700.0;
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };

    private Runnable mSleepTask = new Runnable() {
        public void run() {
        }
    };

    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
            case 2:
                iv_record.setImageResource(R.drawable.chat_icon_voice1);
                break;
            case 3:
            case 4:
            case 5:
                iv_record.setImageResource(R.drawable.chat_icon_voice2);
                break;
            case 6:
            case 7:
            case 8:
                iv_record.setImageResource(R.drawable.chat_icon_voice3);
                break;
            case 9:
            case 10:
            case 11:
                iv_record.setImageResource(R.drawable.chat_icon_voice4);
                break;
            case 12:
            case 13:
            case 14:
                iv_record.setImageResource(R.drawable.chat_icon_voice5);
                break;
            default:
                iv_record.setImageResource(R.drawable.chat_icon_voice6);
                break;
        }
    }


    @Override
    protected void receiveNewMessage(IMMessage message) {

    }

    @Override
    protected void refreshMessage(List<IMMessage> messages) {
        chatAdapter.notifyDataSetChanged();
        chat_list.setSelection(ListView.FOCUS_DOWN);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_user_comment:
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_send:
                String message = messageInput.getText().toString();
                if ("".equals(message)) {
                    Toast.makeText(ChatActivity.this, "不能为空", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    try {
//                        如果输入内容是url则跳转web浏览器
                        if (StringUtils.isURl(message)) {
                            UserInfo4XMPP userInfo4XMPP=new UserInfo4XMPP();
                            userInfo4XMPP.setNameText("百度");
                            userInfo4XMPP.setNameText(message);
                            sendMessage(message, CommonUtils.URL_SIGN,userInfo4XMPP);
                        } else {
                            sendMessage(message);
                        }
                        messageInput.setText("");
                        btn_chat_add.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(ChatActivity.this, "信息发送失败", Toast.LENGTH_LONG).show();
                        LogUtils.e("TSG", "==++==" + e.getMessage());
                    }

                }

                closeInput();
                break;
            case R.id.btn_chat_add:
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    hideSoftInputView();
                } else {
                    layout_more.setVisibility(View.GONE);
                }

                break;

            case R.id.btn_chat_voice:
                messageInput.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;

            case R.id.btn_chat_keyboard:
                showEditState(false);
                break;

            case R.id.tv_camera:
                selectImageFromCamera();
                break;
            case R.id.tv_picture:
                selectImageFromLocal();
                break;
            default:
                break;
        }
    }

    public void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, 2);
    }

    public void selectImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/bmobimdemo/image/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, 1);
    }

    //    表情
    private void showEditState(boolean isEmo) {
        messageInput.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        messageInput.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .showSoftInput(messageInput, 0);
        }
    }

    /**
     * 关闭键盘事件
     *
     * @author shimiso
     * @update 2012-7-4 下午2:34:34
     */
    public void closeInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && this.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:// 获取照相机照片
                    try {
                        String[] ll = localCameraPath.split("/");
                        String picBaseStr = CommonUtils.getImageBase64(localCameraPath, ll[ll.length - 1]);
                        Log.d(TAG, "onActivityResult: " + localCameraPath + ll[ll.length - 1]);
                        sendMessage(picBaseStr);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 2: // 获取本地照片
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(
                                    selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            Log.d(TAG, "onActivityResult: " + localSelectPath);
                            cursor.close();
                            if (localSelectPath == null
                                    || localSelectPath.equals("null")) {
                                Toast.makeText(getApplicationContext(),
                                        "找不到您想要的图片", Toast.LENGTH_SHORT);
                                return;
                            }
                            try {
                                String[] ll = localSelectPath.split("/");
                                String picBaseStr = CommonUtils.getImageBase64(localSelectPath, ll[ll.length - 1]);
                                Log.d(TAG, "onActivityResult: " + localSelectPath + ll[ll.length - 1]);
                                sendMessage(picBaseStr);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }
                    break;
            }
        }
    }
}

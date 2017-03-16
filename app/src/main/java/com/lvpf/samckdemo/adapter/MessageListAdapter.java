package com.lvpf.samckdemo.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.model.IMMessage;
import com.lvpf.samckdemo.model.UserInfo4XMPP;
import com.lvpf.samckdemo.utils.CommonUtils;


import java.io.File;
import java.util.List;


public class MessageListAdapter extends BaseAdapter {
    private List<IMMessage> mList;
    private Context mContext;
    private String currentVoice;// 当前播放的语音路径；
    public MessageListAdapter(Context mContext,
                              List<IMMessage> mList) {
        // TODO Auto-generated constructor stub
        this.mList = mList;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (holder == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.adapter_messagelist, null);
            holder.left_content = (RelativeLayout) convertView.findViewById(R.id.left_content);
            holder.row_msg = (TextView) convertView.findViewById(R.id.row_msg);
            holder.voice_time = (TextView) convertView.findViewById(R.id.voice_time);
            holder.iv_picture= (ImageView) convertView.findViewById(R.id.iv_picture);

            holder.right_content = (RelativeLayout) convertView.findViewById(R.id.right_content);
            holder.row_msg_right = (TextView) convertView.findViewById(R.id.row_msg_right);
            holder.voice_time_right = (TextView) convertView.findViewById(R.id.voice_time_right);
            holder.iv_picture_right= (ImageView) convertView.findViewById(R.id.iv_picture_right);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        IMMessage message = mList.get(position);
//        0表示接收
        if (message.getMsgType() == 0) {
            holder.right_content.setVisibility(View.GONE);
            holder.left_content.setVisibility(View.VISIBLE);
//            语音文件
            if (message.getContent().contains(CommonUtils.VOICE_SIGN)) {
                holder.iv_picture.setVisibility(View.GONE);
                holder.voice_time.setVisibility(View.VISIBLE);
                holder.row_msg.setVisibility(View.VISIBLE);
                playAudio(holder.row_msg, holder.voice_time, message);
//                图片文件
            }else if (message.getContent().contains(CommonUtils.PIC_SIGN)){
                holder.iv_picture.setVisibility(View.VISIBLE);
                holder.voice_time.setVisibility(View.GONE);
                holder.row_msg.setVisibility(View.GONE);
                showPicture(holder.iv_picture,message);

            }else {
                holder.iv_picture.setVisibility(View.GONE);
                holder.voice_time.setVisibility(View.GONE);
                holder.row_msg.setText(message.getContent());
                if (CommonUtils.URL_SIGN.equals(message.getSubject())){
                    final UserInfo4XMPP userInfo4XMPP=message.getUserInfo4XMPP();
                    holder.row_msg.setText(userInfo4XMPP.getNameText()+""+userInfo4XMPP.getUrlText());
                    holder.row_msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(userInfo4XMPP.getUrlText()));
                            it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                            mContext.startActivity(it);
                        }
                    });
                }else {

                    holder.row_msg.setText(message.getContent());
                }

            }
//表示发送
        } else {
            holder.right_content.setVisibility(View.VISIBLE);
            holder.left_content.setVisibility(View.GONE);

            if (message.getContent().contains(CommonUtils.VOICE_SIGN)) {
                holder.iv_picture_right.setVisibility(View.GONE);
                holder.voice_time_right.setVisibility(View.VISIBLE);
                holder.row_msg_right.setVisibility(View.VISIBLE);
                playAudio(holder.row_msg_right, holder.voice_time_right, message);



            } else if (message.getContent().contains(CommonUtils.PIC_SIGN)){
                holder.voice_time_right.setVisibility(View.GONE);
                holder.row_msg_right.setVisibility(View.GONE);
                holder.iv_picture_right.setVisibility(View.VISIBLE);
                showPicture(holder.iv_picture_right,message);
            }else {
                holder.iv_picture_right.setVisibility(View.GONE);
                holder.voice_time_right.setVisibility(View.GONE);
                holder.row_msg_right.setVisibility(View.VISIBLE);
                if (CommonUtils.URL_SIGN.equals(message.getSubject())){
                        final UserInfo4XMPP userInfo4XMPP=message.getUserInfo4XMPP();
                        holder.row_msg_right.setText(userInfo4XMPP.getNameText()+""+userInfo4XMPP.getUrlText());
                        holder.row_msg_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(userInfo4XMPP.getUrlText()));
                                it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                                mContext.startActivity(it);
                            }
                        });
                }else {

                    holder.row_msg_right.setText(message.getContent());
                }
            }
        }
        return convertView;
    }


    private class ViewHolder {
        RelativeLayout left_content;
        TextView row_msg;
        TextView voice_time;
        ImageView iv_picture;
        RelativeLayout right_content;
        TextView row_msg_right;
        TextView voice_time_right;
        ImageView iv_picture_right;

    }

    /**
     * 加载图片
     * @param iv_picture
     * @param message
     */
    private void showPicture(ImageView iv_picture,IMMessage message){
        String[] arr = message.getContent().split(
                CommonUtils.PIC_SIGN);
        String[] imageStr=arr[1].split("@");
        String picPath=CommonUtils.GenerateImage(imageStr[0],imageStr[1]);
        File file = new File(picPath);
        if(file.exists()){
            Bitmap bm = BitmapFactory.decodeFile(picPath);
            iv_picture.setImageBitmap(bm);

        }
    }
    /**
     * 播放语音文件
     *
     * @param row_msg    消息显示
     * @param voice_time 秒数
     * @param message    消息
     */
    private void playAudio(TextView row_msg, TextView voice_time, IMMessage message) {
//        语音文件


        String[] arr = message.getContent().split(
                CommonUtils.VOICE_SIGN);

        String[] brr = arr[1].split("@");
        String voiceTime;

        final String audioUrl = CommonUtils.GenerateVoice(brr[0], brr[1]);
        if (brr.length > 1) {
            voiceTime = brr[2];
        } else {
            voiceTime = "0";
        }
        String msgViewLength = " ";
        for (int i = 0; i < Integer.valueOf(voiceTime); i++) {
            msgViewLength += "";
            if (i > 20) {
                break;
            }
        }
        row_msg.setText(msgViewLength);
        voice_time.setVisibility(View.VISIBLE);
        voice_time.setText(voiceTime + "\"");
        if (message.getMsgType() == 0) {
            row_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.chatto_voice_playing_left, 0);
        } else {
            row_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.chatto_voice_playing, 0);
        }
        row_msg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (audioUrl.contains(".amr")) {
                    if (audioUrl.equals(currentVoice)) {
                        CommonUtils.playMusic(audioUrl, true);
                    } else {
                        currentVoice = audioUrl;
                        CommonUtils.playMusic(audioUrl, false);

                    }
                }
            }
        });
    }
}
package layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inspira.babies.GlobalVar;
import com.inspira.babies.LibInspira;
import com.inspira.babies.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static com.inspira.babies.IndexInternal.global;

/**
 * Created by Arta on 01-Dec-17.
 */

public class ChatItemAdapter extends BaseAdapter {
    List<ChatMsgContainer> data = new ArrayList<>();
    Context con;
    String userId;

    private final String TAG = "chatitemadapter";
    public ChatItemAdapter(Context con)
    {this.con = con;}
    public ChatItemAdapter(Context con, List<ChatMsgContainer> data)
    {
        this.con = con;
        userId = LibInspira.getShared(global.userpreferences, global.user.nomor, "");

        if(data.size()>0) {
            // reset log date dan unread message
            //remove semua log
            List<Integer> listDel = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).getType().equals(ChatMsgContainer.typeMSG)) {
                    listDel.add(i);
                }
            }

            Collections.sort(listDel, Collections.reverseOrder());
            for (int i : listDel) {
                data.remove(i);
            }

            //Log.d(TAG,data.get(0).getType()+"|"+data.get(0).getId());
            //create date log
            String prevDate = "";
            prevDate = data.get(0).getSendTime().substring(0, 10);
            data.add(0, new ChatMsgContainer(dateToString(prevDate), 1));
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).getType().equals(ChatMsgContainer.typeLOG)) {
                    String loopDate = data.get(i).getSendTime().substring(0, 10);
                    if (!prevDate.equals(loopDate)) {
                        data.add(i, new ChatMsgContainer(dateToString(loopDate), 1));
                        prevDate = loopDate;
                    }
                }
            }

            //create unread message log
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getType().equals(ChatMsgContainer.typeMSG)
                        && !data.get(i).getFrom_id().equals(userId)) {
                    if (!data.get(i).getStatus().equals(ChatMsgContainer.statusRead)
                            && !data.get(i).getStatus().equals(ChatMsgContainer.statusReadDelivered)) {
                        data.add(i, new ChatMsgContainer(countUnreadMsg(data) + " Unread Message", 2));
                        break;
                    }
                }
            }
        }

        this.data = data;
    }

    public void reset(List<ChatMsgContainer> data)
    {
        userId = LibInspira.getShared(global.userpreferences, global.user.nomor, "");

        if(data.size()>0) {
            // reset log date dan unread message
            //remove semua log
            List<Integer> listDel = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).getType().equals(ChatMsgContainer.typeMSG)) {
                    listDel.add(i);
                }
            }

            Collections.sort(listDel, Collections.reverseOrder());
            for (int i : listDel) {
                data.remove(i);
            }

            //Log.d(TAG,data.get(0).getType()+"|"+data.get(0).getId());
            //create date log
            if(data.get(0).getSendTime().length() > 10) {
                String prevDate = "";
                prevDate = data.get(0).getSendTime().substring(0, 10);
                data.add(0, new ChatMsgContainer(dateToString(prevDate), 1));
                for (int i = 0; i < data.size(); i++) {
                    if (!data.get(i).getType().equals(ChatMsgContainer.typeLOG)) {
                        if (data.get(i).getSendTime().length() > 10) {
                            String loopDate = data.get(i).getSendTime().substring(0, 10);
                            if (!prevDate.equals(loopDate)) {
                                data.add(i, new ChatMsgContainer(dateToString(loopDate), 1));
                                prevDate = loopDate;
                            }
                        }
                    }
                }

                //create unread message log
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getType().equals(ChatMsgContainer.typeMSG)
                            && !data.get(i).getFrom_id().equals(userId)) {
                        if (!data.get(i).getStatus().equals(ChatMsgContainer.statusRead)
                                && !data.get(i).getStatus().equals(ChatMsgContainer.statusReadDelivered)) {
                            data.add(i, new ChatMsgContainer(countUnreadMsg(data) + " Unread Message", 2));
                            break;
                        }
                    }
                }
            }
        }

        this.data = data;
    }


    private String dateToString(String yyyy_mm_hh)
    {
        String dateString = "";

        String year = yyyy_mm_hh.substring(0,4);
        String month = yyyy_mm_hh.substring(5,7);
        String day = yyyy_mm_hh.substring(8);

        if(month.equals("01") || month.equals("1"))
        {month = "Januari";}
        else if(month.equals("02") || month.equals("2"))
        {month = "Februari";}
        else if(month.equals("03") || month.equals("3"))
        {month = "Maret";}
        else if(month.equals("04") || month.equals("4"))
        {month = "April";}
        else if(month.equals("05") || month.equals("5"))
        {month = "Mei";}
        else if(month.equals("06") || month.equals("6"))
        {month = "Juni";}
        else if(month.equals("07") || month.equals("7"))
        {month = "Juli";}
        else if(month.equals("08") || month.equals("8"))
        {month = "Agustus";}
        else if(month.equals("09") || month.equals("9"))
        {month = "September";}
        else if(month.equals("10"))
        {month = "Oktober";}
        else if(month.equals("11"))
        {month = "Novomber";}
        else if(month.equals("12"))
        {month = "Desember";}

        dateString = month+", "+day+" "+year;
        return dateString;
    }

    private String countUnreadMsg(List<ChatMsgContainer> chatdata)
    {
        int counter = 0;
        for(int i=0;i<chatdata.size();i++)
        {
            if(chatdata.get(i).getType().equals(ChatMsgContainer.typeMSG)
                    && !chatdata.get(i).getStatus().equals(ChatMsgContainer.statusRead)
                    && !chatdata.get(i).getStatus().equals(ChatMsgContainer.statusReadDelivered)
                    && !chatdata.get(i).getFrom_id().equals(userId))
            {
                counter++;
            }
        }
        return String.valueOf(counter);
    }
    public boolean isUnreadLog()
    {
        // untuk cari apakah ada log unread message di adapter
        for(ChatMsgContainer temp : this.data)
        {
            if(temp.getId().equals(ChatMsgContainer.id_UnreadMsg))
            {
                return true;
            }
        }
        return false;
    }
    public int unreadLogPosition()
    {
        if(isUnreadLog()) {
            for (int i = 0;i< this.data.size() ;i++) {
                if (this.data.get(i).getId().equals(ChatMsgContainer.id_UnreadMsg)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void add (ChatMsgContainer newmsg)
    {
        this.data.add(newmsg);
        notifyDataSetChanged();
    }

    public void clear()
    {
        data.clear();
    }

//    public void add (ChatMsgContainer newMsg)
//    {
//        Log.d("chatFragAdapter","asd"+newMsg.getMessage());
//        ChatMsgContainer temp = new ChatMsgContainer();
//        temp.copy(newMsg);
//        data.add(newMsg);
//        //this.notifyDataSetInvalidated();
//        this.notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }
    // untuk update status, cek nomor, get posisi i, get item i, set status

    @Override
    public long getItemId(int i) {
        return i;
    }

//    @Override
//    public int getViewTypeCount() {
//        if(getCount() > 1) {
//            return data.size();
//        }
//        return 1;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position-1;
//    }


    TextView tvMsgYou,tvStatusYou,tvDateTimeYou;
    TextView tvMsgOther, tvDateTimeOther, tvNameOther;
    TextView tvLog,tvLogFull;

    ImageView ivPicContainerYou, ivPicContainerOther;
    TextView tvDateTimeImageYou, tvDateTimeImageOther;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_list_adapter , null);
        }

        tvMsgYou = (TextView) view.findViewById(R.id.tvMsgContainer_you);
        tvStatusYou = (TextView) view.findViewById(R.id.tvMsgStatus_you);
        tvDateTimeYou = (TextView) view.findViewById(R.id.tvMsgDateTime_you);

        ivPicContainerYou = (ImageView) view.findViewById(R.id.ivPicContainer_you);
        tvDateTimeImageYou = (TextView) view.findViewById(R.id.tvMsgDateTime_image_you);

        tvMsgOther = (TextView) view.findViewById(R.id.tvMsgContainer_other);
        tvDateTimeOther = (TextView) view.findViewById(R.id.tvMsgDateTime_other);
        tvNameOther = (TextView) view.findViewById(R.id.tvName_other);

        ivPicContainerOther = (ImageView) view.findViewById(R.id.ivPicContainer_other);
        tvDateTimeImageOther = (TextView) view.findViewById(R.id.tvMsgDateTime_image_other);


        tvLog  = (TextView) view.findViewById(R.id.tvLog);
        tvLogFull  = (TextView) view.findViewById(R.id.tvLogFull);
        view.findViewById(R.id.llMsgYou).setVisibility(View.VISIBLE);
        view.findViewById(R.id.llMsgOther).setVisibility(View.VISIBLE);
        view.findViewById(R.id.llLog).setVisibility(View.VISIBLE);
        view.findViewById(R.id.llLogFull).setVisibility(View.VISIBLE);

        view.findViewById(R.id.LLmsg_you_text).setVisibility(View.VISIBLE);
        view.findViewById(R.id.LLmsg_you_img).setVisibility(View.VISIBLE);

        view.findViewById(R.id.LLmsg_other_text).setVisibility(View.VISIBLE);
        view.findViewById(R.id.LLmsg_other_img).setVisibility(View.VISIBLE);




        if (data.get(position).getType().equals(ChatMsgContainer.typeMSG))
        {
            //Log.d("chatFragAdapter","uname "+uname);
            if (data.get(position).getFrom_id().equals(userId)) {
                //Log.d("chatFragAdapter","if you " + data.get(position).getMessage());
                // you
                //view.findViewById(R.id.llMsgYou).setVisibility(View.VISIBLE);
                view.findViewById(R.id.llMsgOther).setVisibility(View.GONE);
                view.findViewById(R.id.llLog).setVisibility(View.GONE);
                view.findViewById(R.id.llLogFull).setVisibility(View.GONE);

                if(data.get(position).getMsgType().equals(ChatMsgContainer.message_data_type_string)) {
                    view.findViewById(R.id.LLmsg_you_img).setVisibility(View.GONE);
                    if (!data.get(position).getSendTime().equals("")) {
                        tvDateTimeYou.setText(data.get(position).getSendTime().substring(11, 16));
                    }
                    tvMsgYou.setText(data.get(position).getMessage());
                }
                else if(data.get(position).getMsgType().equals(ChatMsgContainer.message_data_type_picture))
                {
                    view.findViewById(R.id.LLmsg_you_text).setVisibility(View.GONE);
                    if (!data.get(position).getSendTime().equals("")) {
                        tvDateTimeImageYou.setText(data.get(position).getSendTime().substring(11, 16));
                    }
                    Picasso.with(con)
                            .load(GlobalVar.URL_SERVER_PICTURE_PATH+data.get(position).getMessage())
                            .resize(200, 200)
                            .centerCrop()
                            .into(ivPicContainerYou);

                    // langusng download
                    ivPicContainerOther.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // new activity dan show gambar
                        }
                    });
                }


                if (ChatMsgContainer.statusSend.equals(data.get(position).getStatus())) {
                    tvStatusYou.setText("S");
                } else if (ChatMsgContainer.statusDelivered.equals(data.get(position).getStatus())) {
                    tvStatusYou.setText("D");
                } else if (ChatMsgContainer.statusRead.equals(data.get(position).getStatus())) {
                    tvStatusYou.setText("R");
                }
                else if (ChatMsgContainer.statusReadDelivered.equals(data.get(position).getStatus())) {
                    tvStatusYou.setText("RD");
                }
                else
                {
                    tvStatusYou.setText("UNK");
                }
            }
            else
            {
                // other
                //Log.d("chatFragAdapter", "if other " + data.get(position).getMessage());
                //view.findViewById(R.id.llMsgOther).setVisibility(View.VISIBLE);
                view.findViewById(R.id.llMsgYou).setVisibility(View.GONE);
                view.findViewById(R.id.llLog).setVisibility(View.GONE);
                view.findViewById(R.id.llLogFull).setVisibility(View.GONE);

                tvNameOther.setText(data.get(position).getFrom_nama());
                if(data.get(position).getMsgType().equals(ChatMsgContainer.message_data_type_string)) {
                    view.findViewById(R.id.LLmsg_other_img).setVisibility(View.GONE);
                    tvMsgOther.setText(data.get(position).getMessage());
                    if(!data.get(position).getSendTime().equals(""))
                    {
                        tvDateTimeOther.setText(data.get(position).getSendTime().substring(11,16));
                    }
                }
                else if(data.get(position).getMsgType().equals(ChatMsgContainer.message_data_type_picture)){
                    view.findViewById(R.id.LLmsg_other_text).setVisibility(View.GONE);
                    if (!data.get(position).getSendTime().equals("")) {
                        tvDateTimeImageOther.setText(data.get(position).getSendTime().substring(11, 16));
                    }
                    Log.d(TAG,data.get(position).getMessage());
                    Picasso.with(con)
                            .load(GlobalVar.URL_SERVER_PICTURE_PATH+data.get(position).getMessage())
                            .resize(200, 200)
                            .centerCrop()
                            .into(ivPicContainerOther);

                    //langusgn download aja
                    ivPicContainerOther.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // new activity dan show gambar
                        }
                    });
                }

            }
        } else if (data.get(position).getType().equals(ChatMsgContainer.typeLOG)) {
            //view.findViewById(R.id.llLog).setVisibility(View.VISIBLE);
            view.findViewById(R.id.llMsgYou).setVisibility(View.GONE);
            view.findViewById(R.id.llMsgOther).setVisibility(View.GONE);
            view.findViewById(R.id.llLogFull).setVisibility(View.GONE);
            tvLog.setText(data.get(position).getMessage());
        }
        else if (data.get(position).getType().equals(ChatMsgContainer.typeLOG_Full)) {

            view.findViewById(R.id.llLog).setVisibility(View.GONE);
            view.findViewById(R.id.llMsgYou).setVisibility(View.GONE);
            view.findViewById(R.id.llMsgOther).setVisibility(View.GONE);
            tvLogFull.setText(data.get(position).getMessage());
        }

        return view;
    }

    public Bitmap resizeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            int REQUIRED_SIZE = 250;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadImage(int position) {
        new AsyncTask<Integer,Void ,String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Integer... params) {



                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(position, null, null);
    }
}

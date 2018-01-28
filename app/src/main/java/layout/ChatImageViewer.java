package layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inspira.babies.GlobalVar;
import com.inspira.babies.IndexInternal;
import com.inspira.babies.LibInspira;
import com.inspira.babies.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.jsonObject;

/**
 * Created by Arta on 18-Jan-18.
 */

public class ChatImageViewer extends Fragment implements View.OnClickListener{
    Context con;

    public ChatImageViewer() {
        // Required empty public constructor
    }

    List<ChatMsgContainer> data = new ArrayList<>();
    String idMsg;
    public void setup(List<ChatMsgContainer> data)
    {
        this.data = data;
    }
    public void setup(List<ChatMsgContainer> data, String idMsg)
    {
        this.data = data;
        this.idMsg = idMsg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.chat_image_viewer, container, false);
        getActivity().setTitle("View Image");
        return v;
    }


    /*****************************************************************************/
    //OnAttach dijalankan pada saat fragment ini terpasang pada Activity penampungnya
    /*****************************************************************************/
    @Override
    public void onAttach(Context context) {
        con = context;
        super.onAttach(context);
    }

    ImageView imgViewer;
    Button btnDownload;
    String msgImg="";
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        getView().findViewById(R.id.llparent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //biar ga bocor
            }
        });

        btnDownload = (Button) getView().findViewById(R.id.btnDownloadImg);
        imgViewer = (ImageView) getView().findViewById(R.id.ivImageViewer);

        btnDownload.setOnClickListener(this);

        msgImg="";
        for(ChatMsgContainer temp : this.data)
        {
            if(idMsg.equals(temp.getId()))
            {
                msgImg = temp.getMessage();
                break;
            }
        }

        Log.d("imgv",msgImg);
        Picasso.with(con)
                .load(GlobalVar.URL_SERVER_PICTURE_PATH+msgImg)
                .centerInside()
                .resize(500,500)
                .placeholder(R.drawable.cast_album_art_placeholder)
                .into(imgViewer);

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btnDownloadImg)
        {
//            try{
////                Bitmap bm = Picasso.with(con)
////                        .load(GlobalVar.URL_SERVER_PICTURE_PATH+msgImg).get();
//                String path = ChatFragment.saveImage(con,bm);
//                LibInspira.ShowShortToast(con,"Download in progress");
//
////                ChatMsgContainer temp = new ChatMsgContainer();
////                temp.copy(data.get(position));
////                temp.setMessage(path);
////                IndexInternal.replaceMessage(temp,"");
//            }catch (Exception e)
//            {
//                LibInspira.ShowShortToast(con,"Download err");
//            }

            final Target tg = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            String path = ChatFragment.saveImage(con,bitmap);
                            LibInspira.ShowShortToast(con,"Saved");

//                            ChatMsgContainer temp = new ChatMsgContainer();
//                            temp.copy(data.get(position));
//                            temp.setMessage(path);
//                            replaceMessage(temp,"");

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            //set gone semua - asumsi file gambar di hapus
                            //setAllView(viewFinal,View.GONE);
                            LibInspira.ShowShortToast(con,"Download err");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };

            Picasso.with(con)
                    .load(GlobalVar.URL_SERVER_PICTURE_PATH+msgImg)
                    .into(tg);

        }
    }


}

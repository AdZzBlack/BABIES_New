/******************************************************************************
    Author           : ADI
    Description      : dashboard untuk internal
    History          :

******************************************************************************/
package layout;

import android.app.Activity;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.jsonObject;

//import android.app.Fragment;

public class PriceListFragment extends Fragment implements View.OnClickListener{
    private EditText etSearch;
    private TextView tvInformation, tvNoData;
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    private boolean isShowHPP;
    getData getDataVar;

    public PriceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_choose, container, false);
        getActivity().setTitle("Price List");
        return v;
    }


    /*****************************************************************************/
    //OnAttach dijalankan pada saat fragment ini terpasang pada Activity penampungnya
    /*****************************************************************************/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //added by Tonny @15-Jul-2017
    //untuk mapping UI pada fragment, jangan dilakukan pada OnCreate, tapi dilakukan pada onActivityCreated
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        list = new ArrayList<ItemAdapter>();

        //reset save-save an shared preference
        LibInspira.setShared(
                global.datapreferences,
                global.data.pricehpp,""
        );
        LibInspira.setShared(
                global.datapreferences,
                global.data.price,""
        );

        ((RelativeLayout) getView().findViewById(R.id.rlSearch)).setVisibility(View.VISIBLE);
        tvInformation = (TextView) getView().findViewById(R.id.tvInformation);
        tvNoData = (TextView) getView().findViewById(R.id.tvNoData);
        etSearch = (EditText) getView().findViewById(R.id.etSearch);

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_item_pricelist, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //pengecekan jika user boleh melihat HPP
        if(LibInspira.getShared(global.userpreferences, global.user.role_hpp, "").equals("1")){
            isShowHPP = true;
        }else{
            isShowHPP = false;
        }

        refreshList();

        String actionUrl = "Master/getPrice/";
        if(isShowHPP){
            actionUrl = "Master/getPriceHPP/";
        }
        getDataVar = new getData();
        getDataVar.execute( actionUrl );
    }

    @Override
    public void onDestroy() {
        if(getDataVar!=null){ getDataVar.cancel(true); }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.ibtnSearch)
        {
            search();
        }
    }

    private void search()
    {
        itemadapter.clear();
        String strSearch = etSearch.getText().toString().toLowerCase();
        for(int ctr=0;ctr<list.size();ctr++)
        {
            if(etSearch.getText().equals(""))
            {
                itemadapter.add(list.get(ctr));
                itemadapter.notifyDataSetChanged();
//                if(!list.get(ctr).getNomor().equals(LibInspira.getShared(global.userpreferences, global.user.nomor_android, "")))
//                {
//                    itemadapter.add(list.get(ctr));
//                    itemadapter.notifyDataSetChanged();
//                }
            }
            else
            {
                if(list.get(ctr).getNama().toLowerCase().contains(strSearch)
                        || list.get(ctr).getKode().toLowerCase().contains(strSearch))
                {
                    itemadapter.add(list.get(ctr));
                    itemadapter.notifyDataSetChanged();
                }
//                if(LibInspira.contains(list.get(ctr).getNama(),etSearch.getText().toString() ))
//                {
//                    if(!list.get(ctr).getNomor().equals(LibInspira.getShared(global.userpreferences, global.user.nomor_android, "")))
//                    {
//                        itemadapter.add(list.get(ctr));
//                        itemadapter.notifyDataSetChanged();
//                    }
//                }
            }
        }
    }

    private void refreshList()
    {
        itemadapter.clear();
        list.clear();

        String data="";
        if (isShowHPP) {
            data = LibInspira.getShared(global.datapreferences, global.data.pricehpp, "");
        }else
        {
            data = LibInspira.getShared(global.datapreferences, global.data.price, "");
        }

        String[] pieces = data.trim().split("\\|");
        if(pieces.length==1 && pieces[0].equals(""))
        {
            tvNoData.setVisibility(View.VISIBLE);
        }
        else
        {
            tvNoData.setVisibility(View.GONE);
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");

                    String nomor = parts[0];
                    String kode = parts[1];
                    String nama = parts[2];
                    String harga = parts[3];
                    String hpp = parts[4];

                    if(nomor.equals("null")) nomor = "";
                    if(kode.equals("null")) kode = "";
                    if(nama.equals("null")) nama = "";
                    if(harga.equals("null")) harga = "";
                    if(hpp.equals("null")) hpp = "";

                    ItemAdapter dataItem = new ItemAdapter();
                    dataItem.setNomor(nomor);
                    dataItem.setKode(kode);
                    dataItem.setNama(nama);
                    dataItem.setHarga(harga);
                    dataItem.setHpp(hpp);
                    list.add(dataItem);

                    if(!dataItem.getNomor().equals(LibInspira.getShared(global.userpreferences, global.user.nomor_android, "")))
                    {
                        itemadapter.add(dataItem);
                        itemadapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private class getData extends AsyncTask<String, Void, String> {
        //String cabang = LibInspira.getShared(global.userpreferences, global.user.cabang, "");
        String nomorjenis = LibInspira.getShared(global.stockmonitoringpreferences, global.stock.nomorjenis, "");
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
//                jsonObject.put("cabang", cabang);
//                Log.d("cabang", cabang);
                jsonObject.put("nomorjenis", nomorjenis);
                Log.d("nomorjenis", nomorjenis);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("resultQuery", result);
            try
            {
                String tempData= "";
                JSONArray jsonarray = new JSONArray(result);
                Log.d("qwe1",jsonarray.length()+"");
                if(jsonarray.length() > 0){
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            String nomor = (obj.getString("nomor"));
                            String nama = (obj.getString("nama"));
                            String kode = (obj.getString("kode"));
                            String harga = (obj.getString("harga"));
                            String hpp = "";
                            if(isShowHPP)
                                hpp = (obj.getString("hpp"));

                            if(nomor.equals("")) nomor = "null";
                            if(kode.equals("")) kode = "null";
                            if(nama.equals("")) nama = "null";
                            if(harga.equals("")) harga = "null";
                            if(hpp.equals("")) hpp = "null";

                            tempData = tempData + nomor + "~" + kode + "~" + nama + "~" + harga + "~" + hpp + "|";
                            //Log.d("lala",i+"");
                        }
                    }
                    //Log.d("asd1","setelah_for");

                    //pengecekan offline

                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.pricehpp, "")) && isShowHPP)
                    {
                        //Log.d("asd2","sebelum shared");
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.pricehpp,
                                tempData
                        );
                        //Log.d("asd3","setelah shared");
                        refreshList();
                        Log.d("asd4","setelah refresh");
                    }else if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.price, "")) && !isShowHPP)
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.price,
                                tempData
                        );
                        refreshList();
                        Log.d("asd4","setelah refresh");
                    }
                }
                tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                tvInformation.animate().translationYBy(-80);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvInformation.setVisibility(View.VISIBLE);
        }
    }

    public class ItemAdapter {

        private String nomor;
        private String nama;
        private String kode;
        private String harga;
        private String hpp;

        public ItemAdapter() {}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public String getNama() {return nama;}
        public void setNama(String _param) {this.nama = _param;}

        public String getKode() {return kode;}
        public void setKode(String _param) {this.kode = _param;}

        public String getHarga() {return harga;}
        public void setHarga(String _param) {this.harga = _param;}

        public String getHpp() {return hpp;}
        public void setHpp(String _param) {this.hpp = _param;}
    }

    public class ItemListAdapter extends ArrayAdapter<ItemAdapter> {

        private List<ItemAdapter> items;
        private int layoutResourceId;
        private Context context;

        public ItemListAdapter(Context context, int layoutResourceId, List<ItemAdapter> items) {
            super(context, layoutResourceId, items);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.items = items;
        }

        public List<ItemAdapter> getItems() {
            return items;
        }

        public class Holder {
            ItemAdapter adapterItem;
            TextView tvNama;
            TextView tvHarga;
            TextView tvHpp;
            ImageView ivCall;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Holder holder = null;

            if(row==null)
            {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            holder = new Holder();
            holder.adapterItem = items.get(position);

            holder.tvNama = (TextView)row.findViewById(R.id.tvName);
            holder.tvHarga = (TextView)row.findViewById(R.id.tvKeterangan);
            holder.tvHpp = (TextView)row.findViewById(R.id.tvKeterangan1);
            holder.ivCall = (ImageView)row.findViewById(R.id.ivCall);

            row.setTag(holder);
            setupItem(holder);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseKotaFragment());
                }
            });

            final Holder finalHolder = holder;
            holder.tvNama.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String nomeruser = finalHolder.adapterItem.getNomor();
                }
            });

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvNama.setText(holder.adapterItem.getNama().toUpperCase()+"\nKode : "+holder.adapterItem.getKode());
            holder.tvHarga.setVisibility(View.VISIBLE);
            //holder.tvHarga.setText("Harga: Rp. " + LibInspira.delimeter(holder.adapterItem.getHarga()));
            holder.tvHarga.setText("Harga: \n" + splitHarga(holder.adapterItem.getHarga()));
            //holder.tvHarga.setText(holder.adapterItem.getHarga());

            String hpp = holder.adapterItem.getHpp();
            if(isShowHPP){
                holder.tvHpp.setVisibility(View.VISIBLE);
                if (hpp.equals(""))
                    hpp = "null";
                //holder.tvLocation.setText(holder.tvLocation.getText() + "\r\nHPP: Rp. " + LibInspira.delimeter(hpp));
                holder.tvHpp.setText("HPP: Rp. " + LibInspira.delimeter(hpp));
            }
        }

        private String capitalize(String s)
        { if(s == null) return "";
            if(s.length() == 1){ return s.toUpperCase(); }
            if(s.length() > 1)
            {
                s = s.toLowerCase();
                return s.substring(0,1).toUpperCase() + s.substring(1);
            }
            return "";
        }

        private String splitHarga(String s)
        {
            String[] parts_jenis = s.trim().split("\\@@");
            String hargaFinal = "";
            for(String tempHarga : parts_jenis)
            {
                //Log.d("qwe",tempHarga);
                String[] parts_harga = tempHarga.trim().split("\\::");
                //hargaFinal += "• " + capitalize(parts_harga[0]) + " : " + LibInspira.delimeter(parts_harga[1]) + "\n";
                if(parts_harga.length > 1) {
                    if (!parts_harga[0].equals("") && !parts_harga[1].equals("")) {
                        hargaFinal += "• " + capitalize(parts_harga[0]) + " : " + LibInspira.delimeter(parts_harga[1]) + "\n";
                    } else {
                        hargaFinal += "• " + capitalize(parts_harga[0]) + " : " + "null";
                    }
                }
                else if(parts_harga.length == 1)
                {
                    hargaFinal += "• " + capitalize(parts_harga[0]) + " : " + "---";
                }
            }

            return hargaFinal;
        }
    }
}

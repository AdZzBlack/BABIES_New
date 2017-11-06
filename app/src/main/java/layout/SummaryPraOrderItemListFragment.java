/**
 * Created by Arta on 30-Oct-17.
 */

/******************************************************************************
 Author           : ADI
 Description      : untuk menampilkan detail item dalam bentuk list
 History          :

 ******************************************************************************/
package layout;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class SummaryPraOrderItemListFragment extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    private Button btnBack, btnNext;
    protected String actionUrl;
    //private CheckData checkData;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;
    private getData getData;

    public SummaryPraOrderItemListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_praorder_summary_item, container, false);
        getActivity().setTitle("PraOrder List item");
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

        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(this);

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_praorder_summary_item, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        relativeLayout = (RelativeLayout) getView().findViewById(R.id.rlFooter);
        relativeLayout.setVisibility(View.GONE);

        refreshList();

        fab.setVisibility(View.GONE);
//        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") || LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval"))
//        {
//            fab.setVisibility(View.GONE);
//        }
//        else
//        {
//            fab.setVisibility(View.VISIBLE);
//        }

        actionUrl = "Order/getPraOrderItemList/";
        getData = new getData();
        getData.execute(actionUrl);
//        checkData = new CheckData();
//        checkData.execute( actionUrl );
    }

    protected void onCancelRequest(){
        //if(checkData != null) checkData.cancel(true);
        if(getData != null) getData.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCancelRequest();
    }

//    private class CheckData extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            jsonObject = new JSONObject();
//            return LibInspira.executePost(getContext(), urls[0], jsonObject);
//        }
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            Log.d("resultQuery", result);
//            try
//            {
//                String tempData= "";
//                JSONArray jsonarray = new JSONArray(result);
//                if(jsonarray.length() > 0){
//                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
//                        JSONObject obj = jsonarray.getJSONObject(i);
//                        if(!obj.has("query")){
//                            String nomor = (obj.getString("nomor"));
//                            String kode = (obj.getString("kode"));
//                            String tanggal = (obj.getString("tanggal"));
//                            String kodecustomer = (obj.getString("kodeCustomer"));
//                            String namacustomer = (obj.getString("namaCustomer"));
//                            String keterangan = (obj.getString("keterangan"));
//                            String status = (obj.getString("status_disetujui"));
//
//                            if(nomor.equals("null")) nomor = "";
//                            if(kode.equals("null")) kode = "";
//                            if(tanggal.equals("null")) tanggal = "";
//                            if(kodecustomer.equals("null")) kodecustomer = "";
//                            if(namacustomer.equals("null")) namacustomer = "";
//                            if(keterangan.equals("null")) keterangan = "";
//                            if(status.equals("null")) status = "";
//
//                            tempData = tempData + nomor + "~" + kode + "~" + tanggal + "~" + kodecustomer + "~" + namacustomer + "~" + keterangan + "~" + status +"|";
//                        }
//                    }
//
//                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.praOrder_list_item, "")))
//                    {
//                        LibInspira.setShared(
//                                global.datapreferences,
//                                global.data.praOrder_list_item,
//                                tempData
//                        );
//                        refreshList();
//                    }
//                }
//                LibInspira.hideLoading();
//                //tvInformation.animate().translationYBy(-80);
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//                LibInspira.hideLoading();
//                //tvInformation.animate().translationYBy(-80);
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            LibInspira.showLoading(getContext(), "Adding new data", "Loading...");
//            //tvInformation.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

//        if(id==R.id.fab)
//        {
//            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderHeaderFragment());
//        }
    }

    protected void refreshList()
    {
        itemadapter.clear();
        list.clear();
        String strData =  LibInspira.getShared(
                global.temppreferences,
                global.temp.praorder_item,
                "");
        if (strData.equals("")){
            return;
        }
        String data = strData;
        String[] pieces = data.trim().split("\\|");
        if((pieces.length==1 && pieces[0].equals("")))
        {
            //do nothing
        }
        else
        {
            for(int i=0 ; i < pieces.length ; i++){
                Log.d("Index", data);
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");
                    //Log.d("pieces: ", pieces[i]);
                    try {
//                        data[0] = obj.getString("nomor");
//                        data[1] = obj.getString("kodeBarang");
//                        data[2] = obj.getString("kodeBar");
//                        data[3] = obj.getString("namaBarang");
//                        data[4] = obj.getString("nomorSatuan");
//                        data[5] = obj.getString("satuan");
//                        data[6] = obj.getString("jumlah");


                        for(String k : parts)
                        {
                            if(k.equals("null")) k = "";
                        }

                        ItemAdapter dataItem = new ItemAdapter();
                        dataItem.setNomor(parts[0]);
                        dataItem.setKodeBarang(parts[1]);
                        dataItem.setKodeBar(parts[2]);
                        dataItem.setNamaBarang(parts[3]);
                        //parts[4] nomor satuan
                        dataItem.setSatuan(parts[5]);
                        dataItem.setJumlah(LibInspira.delimeter(parts[6]));

                        list.add(dataItem);

                        itemadapter.add(dataItem);
                        itemadapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                        LibInspira.ShowShortToast(getContext(), "The current data is invalid. Please add new data.");
                        strData = "";
                        refreshList();
                    }
                }
            }
        }
    }

    public class ItemAdapter {
        private String nomor;
        private String kodeBarang;
        private String kodeBar;
        private String namaBarang;
        private String satuan;
        private String Jumlah;

        public ItemAdapter() {}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public String getKodeBarang() {
            return kodeBarang;
        }

        public void setKodeBarang(String kodeBarang) {
            this.kodeBarang = kodeBarang;
        }

        public String getKodeBar() {
            return kodeBar;
        }

        public void setKodeBar(String kodeBar) {
            this.kodeBar = kodeBar;
        }

        public String getNamaBarang() {
            return namaBarang;
        }

        public void setNamaBarang(String namaBarang) {
            this.namaBarang = namaBarang;
        }

        public void setSatuan(String satuan) {
            this.satuan = satuan;
        }

        public String getSatuan() {
            return satuan;
        }

        public void setJumlah(String jumlah) {
            Jumlah = jumlah;
        }

        public String getJumlah() {
            return Jumlah;
        }
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
            TextView tvNomor,tvKodeBarang,tvKodeBar,tvNamaBarang,tvSatuan,tvJumlah;
            ImageView imgDel;
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

            holder.tvNomor = (TextView)row.findViewById(R.id.tvNomor);
            holder.tvKodeBarang = (TextView)row.findViewById(R.id.tvKodeBarang);
            holder.tvNamaBarang = (TextView)row.findViewById(R.id.tvNamaBarang);
            holder.tvKodeBar = (TextView)row.findViewById(R.id.tvKodeBarcode);
            holder.tvSatuan = (TextView)row.findViewById(R.id.tvSatuan);
            holder.tvJumlah = (TextView)row.findViewById(R.id.tvJumlah);

            //holder.tvNomor.setVisibility(View.GONE);

            row.setTag(holder);
            setupItem(holder);

            final Holder finalHolder = holder;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //kalau di klik msal belum approve ngedit
                }
            });

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvNomor.setText(holder.adapterItem.getNomor().toUpperCase());
            holder.tvKodeBarang.setText(holder.adapterItem.getKodeBarang().toUpperCase());
            holder.tvNamaBarang.setText(holder.adapterItem.getNamaBarang().toUpperCase());
            holder.tvKodeBar.setText(holder.adapterItem.getKodeBar().toUpperCase());
            holder.tvSatuan.setText(holder.adapterItem.getSatuan().toUpperCase());
            holder.tvJumlah.setText(LibInspira.delimeter(holder.adapterItem.getJumlah().toUpperCase()));
        }
    }

    private class getData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.praorder_selected_list_nomor, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return LibInspira.executePost_local(getContext(), urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("resultQuery", result);
            try
            {
                String tempData= "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            int sizeData = 7;
                            String[] data = new String[sizeData];
                            data[0] = obj.getString("nomor");
                            data[1] = obj.getString("kodeBarang");
                            data[2] = obj.getString("nomorBarang");
                            data[3] = obj.getString("namaBarang");
                            data[4] = obj.getString("nomorSatuan");
                            data[5] = obj.getString("satuan");
                            data[6] = obj.getString("jumlah");

                            for(int z = 0;z<sizeData;z++)
                            {
                                if(data[z].equals("null")) data[z] = "";
                            }
                            tempData = tempData + data[0] + "~" + data[1] + "~" + data[2] + "~"
                                    + data[3] + "~" + data[4] + "~" + data[5] + "~" + data[6] + "|";
                        }
                    }

//                    if(!tempData.equals(LibInspira.getShared(global.temppreferences, global.temp.salesorder_summary, "")))
//                    {
                    LibInspira.setShared(
                            global.temppreferences,
                            global.temp.praorder_item,
                            tempData
                    );
//                    }
                }
                LibInspira.hideLoading();
                refreshList();
                //LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new PraOrderApprovalFragment());
                //tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.hideLoading();
                //tvInformation.animate().translationYBy(-80);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "Getting summary data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }
}

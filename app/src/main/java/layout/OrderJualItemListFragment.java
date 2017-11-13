/**
 * Created by Arta on 8-nov-17.
 */

/******************************************************************************
 Author           :
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

public class OrderJualItemListFragment extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    private Button btnBack, btnNext;
    protected String actionUrl;
    //private CheckData checkData;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;
    private getData getData;

    public OrderJualItemListFragment() {
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
        getActivity().setTitle("Detail OrderJual");
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

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_orderjual_summary_item, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        relativeLayout = (RelativeLayout) getView().findViewById(R.id.rlFooter);
        relativeLayout.setVisibility(View.GONE);

        refreshList();

        fab.setVisibility(View.GONE);

        actionUrl = "Order/getOrderJualItemList/";
        getData = new getData();
        getData.execute(actionUrl);

    }

    protected void onCancelRequest(){
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

    }

    protected void refreshList()
    {
        itemadapter.clear();
        list.clear();
        String strData =  LibInspira.getShared(
                global.temppreferences,
                global.temp.orderjual_item,
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
//                        data[1] = obj.getString("nomorBarang");
//                        data[2] = obj.getString("kodeBarang");
//                        data[3] = obj.getString("namaBarang");
//                        data[4] = obj.getString("jumlah");
//                        data[5] = obj.getString("nomorSatuan");
//                        data[6] = obj.getString("namaSatuan");
//                        data[7] = obj.getString("harga");
//                        data[8] = obj.getString("diskon");
//                        data[9] = obj.getString("netto");
//                        data[10] = obj.getString("subtotal");
//                        data[11] = obj.getString("stokTerkini");


                        for(int k = 0;k<parts.length;k++)
                        {
                            if(parts[k].equals("null")) parts[k] = "";
                        }

                        ItemAdapter dataItem = new ItemAdapter();
                        dataItem.setNomor(parts[0]);
                        dataItem.setKodeBarang(parts[2]);
                        dataItem.setNamaBarang(parts[3]);
                        dataItem.setSatuan(parts[6]);
                        dataItem.setJumlah(LibInspira.delimeter(parts[4]));
                        dataItem.setHarga(parts[7]);
                        dataItem.setDiskon(parts[8]);
                        dataItem.setNetto(parts[9]);
                        dataItem.setSubtotal(parts[10]);
                        dataItem.setStokTerkini(parts[11]);

                        list.add(dataItem);

                        itemadapter.add(dataItem);
                        itemadapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                        LibInspira.ShowShortToast(getContext(), "The current data is invalid. Please add new data.");
                        LibInspira.setShared(
                                global.temppreferences,
                                global.temp.orderjual_item,
                                "");
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
        private String namaBarang;
        private String Jumlah;
        private String satuan;
        private String harga;
        private String diskon;
        private String netto;
        private String subtotal;
        private String stokTerkini;

        public ItemAdapter() {}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public String getKodeBarang() {
            return kodeBarang;
        }
        public void setKodeBarang(String kodeBarang) {
            this.kodeBarang = kodeBarang;
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

        public void setHarga(String harga) {
            this.harga = harga;
        }
        public String getHarga() {
            return harga;
        }

        public void setDiskon(String diskon) {
            this.diskon = diskon;
        }
        public String getDiskon() {
            return diskon;
        }

        public void setNetto(String netto) {
            this.netto = netto;
        }
        public String getNetto() {
            return netto;
        }

        public void setSubtotal(String subtotal) {
            this.subtotal = subtotal;
        }
        public String getSubtotal() {
            return subtotal;
        }

        public void setStokTerkini(String stokTerkini) {
            this.stokTerkini = stokTerkini;
        }
        public String getStokTerkini() {
            return stokTerkini;
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
            TextView tvNomor,tvKodeBarang,tvNamaBarang,tvSatuan,tvJumlah;
            TextView tvHarga,tvDiskon,tvNetto, tvSubtotal, tvStokTerkini;
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

            holder.tvNomor = (TextView)row.findViewById(R.id.tvNomor); // default di xml visibility GONE
            holder.tvKodeBarang = (TextView)row.findViewById(R.id.tvKodeBarang);
            holder.tvNamaBarang = (TextView)row.findViewById(R.id.tvNamaBarang);
            holder.tvSatuan = (TextView)row.findViewById(R.id.tvSatuan);
            holder.tvJumlah = (TextView)row.findViewById(R.id.tvJumlah);

            holder.tvHarga = (TextView)row.findViewById(R.id.tvHarga);
            holder.tvDiskon = (TextView)row.findViewById(R.id.tvDiskon);
            holder.tvNetto = (TextView)row.findViewById(R.id.tvNetto);
            holder.tvSubtotal = (TextView)row.findViewById(R.id.tvSubtotal);
            holder.tvStokTerkini = (TextView)row.findViewById(R.id.tvStokTerkini);


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
            holder.tvSatuan.setText(holder.adapterItem.getSatuan().toUpperCase());
            holder.tvJumlah.setText(LibInspira.delimeter(holder.adapterItem.getJumlah().toUpperCase()));

            holder.tvHarga.setText(LibInspira.delimeter(holder.adapterItem.getHarga().toUpperCase()));
            holder.tvDiskon.setText(LibInspira.delimeter(holder.adapterItem.getDiskon().toUpperCase()));
            holder.tvNetto.setText(LibInspira.delimeter(holder.adapterItem.getNetto().toUpperCase()));
            holder.tvSubtotal.setText(LibInspira.delimeter(holder.adapterItem.getSubtotal().toUpperCase()));
            holder.tvStokTerkini.setText(LibInspira.delimeter(holder.adapterItem.getStokTerkini().toUpperCase()));
        }
    }

    private class getData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_nomor, ""));
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
                            int sizeData = 12;
                            String[] data = new String[sizeData];
                            data[0] = obj.getString("nomor");
                            data[1] = obj.getString("nomorBarang");
                            data[2] = obj.getString("kodeBarang");
                            data[3] = obj.getString("namaBarang");
                            data[4] = obj.getString("jumlah");
                            data[5] = obj.getString("nomorSatuan");
                            data[6] = obj.getString("namaSatuan");
                            data[7] = obj.getString("harga");
                            data[8] = obj.getString("diskon");
                            data[9] = obj.getString("netto");
                            data[10] = obj.getString("subtotal");
                            data[11] = obj.getString("stokTerkini");

                            for(int z = 0;z<sizeData;z++)
                            {
                                if(data[z].equals("null")) data[z] = "";
                            }
                            tempData = tempData + data[0] + "~" + data[1] + "~" + data[2] + "~"
                                    + data[3] + "~" + data[4] + "~" + data[5] + "~" + data[6]
                                    + "~" + data[7] + "~" + data[8] + "~" + data[9] + "~" + data[10]
                                    + "~" + data[11] + "~" + 0 + "|";

                            //#NEWEDIT : 0 UNTUK NORMAL 1 DEL, 2 EDIT
                        }
                    }

//                    if(!tempData.equals(LibInspira.getShared(global.temppreferences, global.temp.salesorder_summary, "")))
//                    {
                    LibInspira.setShared(
                            global.temppreferences,
                            global.temp.orderjual_item,
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
            LibInspira.showLoading(getContext(), "Getting list data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }
}

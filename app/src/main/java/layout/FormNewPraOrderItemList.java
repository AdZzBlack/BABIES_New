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
import android.widget.ImageButton;
import android.widget.ListView;
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

public class FormNewPraOrderItemList extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    protected ItemListAdapter itemadapter;
    protected ArrayList<ItemAdapter> list;
    protected String jenisDetail;  //added by Tonny @16-Sep-2017
    private Button btnBack, btnSave;
    private FloatingActionButton fab;
    protected String strData;

    public FormNewPraOrderItemList() {
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
        View v = inflater.inflate(R.layout.fragment_praorder_form_item_list, container, false);
        getActivity().setTitle("PraOrder - List Item");

//        if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") &&
//                !LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){
//            getActivity().setTitle("PraOrder - List Item");
//        }
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

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_praorder_summary_item, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        btnBack = (Button) getView().findViewById(R.id.btnBack);
        btnSave = (Button) getView().findViewById(R.id.btnSave);


        //added by Tonny @16-Sep-2017 jika approval atau disapproval, maka hide btnBack dan btnNext
        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") ||
                LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){
            btnBack.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }else{
            fab.setOnClickListener(this);
            btnBack.setOnClickListener(this);
            btnSave.setOnClickListener(this);
        }

        if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("edit"))
        {
            //ganti save jadi finish
            btnSave.setText("Finish");
        }

        refreshList();
        //getStrData();
        Log.d("onActivityCreated: ", "list item created");
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
//            search();
        }
        else if(id==R.id.fab)
        {
            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("edit"))
            {
                LibInspira.setShared(global.temppreferences, global.temp.praorder_submenu, "new_from_edit");
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("add_new"))
            {
                LibInspira.setShared(global.temppreferences, global.temp.praorder_submenu, "new_from_add_new");
            }

            LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, "");
            LibInspira.setShared(global.temppreferences, global.temp.praorder_nomor_item_add, "");
            LibInspira.setShared(global.temppreferences, global.temp.praorder_nama_barang_add, "");
            LibInspira.setShared(global.temppreferences, global.temp.praorder_kode_barang_add, "");
            LibInspira.setShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "");
            LibInspira.setShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "");
            LibInspira.setShared(global.temppreferences, global.temp.praorder_satuan_add, "");
            LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, "");

            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewPraOrderItem());
        }
        else if(id==R.id.btnBack)
        {
            LibInspira.BackFragment(getActivity().getSupportFragmentManager());
        }
        else if(id==R.id.btnSave)
        {
            //sendData(); // by pass insert
            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("add_new"))
            {
                LibInspira.alertBoxYesNo("Menambah baru", "Apakah anda yakin ingin Menambah data baru?", getActivity(), new Runnable() {
                    public void run() {
                    //YES
                    // cek if item is null ga bs insert
                    if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "").equals(""))
                    {
                        sendData();
                    }
                    else
                    {
                        LibInspira.ShowLongToast(getActivity(),"Data Item Masih Kosong");
                    }

                    }
                }, new Runnable() {
                    public void run() {
                        //NO
                    }
                });
                //yes no kasih msg
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("edit"))
            {
                //FINISH
                LibInspira.alertBoxYesNo("Selesai mengubah?", "Apakah anda telah selesai mengubah?", getActivity(), new Runnable() {
                    public void run() {
                        //YES
                        // back to menu sales order
                        LibInspira.BackFragmentCount(getFragmentManager(), 3);
                    }
                }, new Runnable() {
                    public void run() {
                        //NO
                    }
                });
            }
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "").equals("")){
//                LibInspira.ShowShortToast(getContext(), "No item selected. Please add item to proceed");
//            }else{
//                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderDetailJasaListFragment());
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void refreshList()
    {
        itemadapter.clear();
        list.clear();
        //getStrData();  //added by Tonny @07-Sep-2017
        strData = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "");
        if (!strData.equals("")){
            //return;

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
                        //kode nama jumlah satuan
                        String[] parts = pieces[i].trim().split("\\~");
                        Log.d("pieces: ", pieces[i]);
                        try {

                            for(int k=0;k<parts.length;k++)
                            {
                                if(parts[k].equals("null"))
                                {
                                    parts[k] = "";
                                }
                            }

//                            data[0] = obj.getString("nomor");
//                            data[1] = obj.getString("kodeBarang");
//                            data[2] = obj.getString("nomorBarang");
//                            data[3] = obj.getString("namaBarang");
//                            data[4] = obj.getString("nomorSatuan");
//                            data[5] = obj.getString("satuan");
//                            data[6] = obj.getString("jumlah");


                            ItemAdapter dataItem = new ItemAdapter();
                            dataItem.setIndex(i);

                            dataItem.setNomor(parts[0]);
                            dataItem.setKodeBarang(parts[1]);
                            dataItem.setNomorBarang(parts[2]);
                            dataItem.setNamaBarang(parts[3]);
                            dataItem.setNomorSatuan(parts[4]);
                            dataItem.setSatuan(parts[5]);
                            dataItem.setJumlah(LibInspira.delimeter(parts[6]));

                            list.add(dataItem);

                            itemadapter.add(dataItem);
                            itemadapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                            LibInspira.ShowShortToast(getContext(), "The current data is invalid. Please add new data.");
                            LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, "");
                            strData = "";
                            refreshList();
                        }
                    }
                }
            }
        }
    }

    protected void deleteSelectedItem(int _index){
        String newdata = "";
        //getStrData();
        if(!strData.equals(""))
        {
            String[] pieces = strData.trim().split("\\|");
            for(int i=0 ; i < pieces.length ; i++){
//                String string = pieces[i];
//                String[] parts = string.trim().split("\\~");
                if(i != _index)
                {
                    newdata = newdata + pieces[i] + "|";
                }
            }
        }
        setStrData(newdata);
        refreshList();
    }



//    protected void getStrData(){
//        strData = LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "");
//        //added by Tonny @16-Sep-2017 jika approval atau disapproval, maka hide ibtnDelete
//        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") ||
//                LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){
//            String ActionUrl = "Order/getSalesOrderItemList/";
//            if(jenisDetail.equals("jasa")){
//                ActionUrl = "Order/getSalesOrderJasaList/";
//            }
//            GetList getList = new GetList();
//            getList.execute(ActionUrl);
//        }else{
//            refreshList();
//        }
//    }

    protected class GetList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
                //"nomor" == nomor header
                jsonObject.put("nomor", LibInspira.getShared(global.temppreferences, global.temp.salesorder_selected_list_nomor, ""));
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
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            //nomorbarang~kodebarang~namabarang~satuan~price~qty~fee~disc~subtotal~notes
                            String nomorbarang = (obj.getString("nomorbarang"));
                            String kodebarang = (obj.getString("kodebarang"));
                            String namabarang = (obj.getString("namabarang"));
                            String satuan = (obj.getString("satuan"));
                            String price = (obj.getString("price"));
                            String qty = (obj.getString("qty"));
                            String fee = (obj.getString("fee"));
                            String disc = (obj.getString("disc"));
                            String subtotal = (obj.getString("subtotal"));
                            String notes = (obj.getString("notes"));

                            if(nomorbarang.equals("")) nomorbarang = "null";
                            if(kodebarang.equals("")) kodebarang = "null";
                            if(namabarang.equals("")) namabarang = "null";
                            if(satuan.equals("")) satuan = "null";
                            if(price.equals("")) price = "null";
                            if(qty.equals("")) qty = "null";
                            if(fee.equals("")) fee = "null";
                            if(disc.equals("")) disc = "null";
                            if(subtotal.equals("")) subtotal= "null";
                            if(notes.equals("")) notes = "null";

                            tempData = tempData + nomorbarang + "~" + kodebarang + "~" + namabarang + "~"
                                    + satuan + "~" + price + "~" + qty + "~" + fee + "~" + disc + "~" + subtotal + "~" + notes + "|";
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "")))
                    {
                        //added by Tonny @16-Sep-2017
                        setStrData(tempData);
                        refreshList();
                    }
                }
                //tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                //tvInformation.animate().translationYBy(-80);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    protected void setStrData(String newdata){
        LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, newdata);
        strData = newdata;
    }

    protected void setEditData(String index,String nomor, String nama, String kode,String nomorBarang , String nomorSatuan, String satuan, String jumlah){
        LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, index);
        LibInspira.setShared(global.temppreferences, global.temp.praorder_nomor_item_add, nomor);
        LibInspira.setShared(global.temppreferences, global.temp.praorder_nama_barang_add, nama);
        LibInspira.setShared(global.temppreferences, global.temp.praorder_kode_barang_add, kode);
        LibInspira.setShared(global.temppreferences, global.temp.praorder_nomor_barang_add, nomorBarang);
        LibInspira.setShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, nomorSatuan);
        LibInspira.setShared(global.temppreferences, global.temp.praorder_satuan_add, satuan);
        LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, jumlah);

        Log.d("editData",index+" "+nomor+" "+nama+" "+kode+" "+nomorBarang+" "+nomorSatuan+" "+satuan+" "+jumlah);

        //LibInspira.setShared(global.temppreferences, global.temp.praorder_menu, "edit");
        LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewPraOrderItem());
    }

    public class ItemAdapter {

        private int index;
        private String nomor = "0"; // klo add new ga perlu nomor, jadi di default dummy, klo edit bru di ganti
        private String namaBarang;
        private String kodeBarang;
        private String nomorBarang;
        private String nomorSatuan;
        private String satuan;
        private String jumlah;

        public ItemAdapter() {}

        public int getIndex() {return index;}
        public void setIndex(int _param) {this.index = _param;}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public void setNamaBarang(String namaBarang) {
            this.namaBarang = namaBarang;
        }
        public String getNamaBarang() {
            return namaBarang;
        }

        public void setKodeBarang(String kodeBarang) {
            this.kodeBarang = kodeBarang;
        }
        public String getKodeBarang() {
            return kodeBarang;
        }

        public String getNomorBarang() {
            return nomorBarang;
        }
        public void setNomorBarang(String nomorBarang) {
            this.nomorBarang = nomorBarang;
        }

        public void setNomorSatuan(String nomorSatuan) {
            this.nomorSatuan = nomorSatuan;
        }
        public String getNomorSatuan() {
            return nomorSatuan;
        }

        public void setSatuan(String satuan) {
            this.satuan = satuan;
        }
        public String getSatuan() {
            return satuan;
        }

        public void setJumlah(String jumlah) {
            this.jumlah = jumlah;
        }
        public String getJumlah() {
            return jumlah;
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
            TextView tvKode, tvNama, tvSatuan, tvJumlah ;
            ImageButton ibtnDelete;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Holder holder = null;

            if(row==null)
            {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            holder = new Holder();
            holder.adapterItem = items.get(position);

            holder.tvNama = (TextView)row.findViewById(R.id.tvNamaBarang);
            holder.tvKode = (TextView)row.findViewById(R.id.tvKodeBarang);
            holder.tvSatuan = (TextView)row.findViewById(R.id.tvSatuan);
            holder.tvJumlah = (TextView) row.findViewById(R.id.tvJumlah);
            holder.ibtnDelete = (ImageButton) row.findViewById(R.id.ibtnDelete);

            row.setTag(holder);
            setupItem(holder);

            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("add_new"))
            {
                holder.ibtnDelete.setVisibility(View.VISIBLE);
                holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // lakukan method delete
                        LibInspira.alertBoxYesNo("Delete item", "Apakah anda ingin menghapus item?", getActivity(), new Runnable() {
                            public void run() {
                                //YES
                                deleteSelectedItem(items.get(position).getIndex());
                            }
                        }, new Runnable() {
                            public void run() {
                                //NO
                            }
                        });
                    }
                });

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do klo item list di click edit waktu add new
                        LibInspira.setShared(global.temppreferences, global.temp.praorder_submenu, "edit_from_add_new");
                        setEditData(items.get(position).getIndex()+"",
                                items.get(position).getNomor(),
                                items.get(position).getNamaBarang(),
                                items.get(position).getKodeBarang(),
                                items.get(position).getNomorBarang(),
                                items.get(position).getNomorSatuan(),
                                items.get(position).getSatuan(),
                                items.get(position).getJumlah());
                    }
                });
            }
            else if( LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("edit"))
            {
                holder.ibtnDelete.setVisibility(View.VISIBLE);
                holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // lakukan method delete
                        LibInspira.alertBoxYesNo("Delete item", "Apakah anda ingin menghapus item?", getActivity(), new Runnable() {
                            public void run() {
                                //YES
                                LibInspira.setShared(global.temppreferences, global.temp.praorder_delete_item_index, items.get(position).getIndex()+"");
                                LibInspira.setShared(global.temppreferences, global.temp.praorder_delete_item_nomor, items.get(position).getNomor()+"");
                                deleteItem();
                            }
                        }, new Runnable() {
                            public void run() {
                                //NO
                            }
                        });
                    }
                });

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do klo item list di click edit
                        LibInspira.setShared(global.temppreferences, global.temp.praorder_submenu, "edit_from_edit");
                        setEditData(items.get(position).getIndex()+"",
                                items.get(position).getNomor(),
                                items.get(position).getNamaBarang(),
                                items.get(position).getKodeBarang(),
                                items.get(position).getNomorBarang(),
                                items.get(position).getNomorSatuan(),
                                items.get(position).getSatuan(),
                                items.get(position).getJumlah());
                    }
                });

            }



//            //added by Tonny @16-Sep-2017 jika approval atau disapproval, maka hide ibtnDelete dan hilangkan listener click
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") ||
//                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){
//                holder.ibtnDelete.setVisibility(View.GONE);
//            }else{
//                final Holder finalHolder = holder;
//                row.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        setEditData(String.valueOf(finalHolder.adapterItem.getIndex()),
//                                finalHolder.adapterItem.getNomor(),
//                                finalHolder.adapterItem.getNama(),
//                                finalHolder.adapterItem.getKode(),
//                                finalHolder.adapterItem.getNomorReal(),
//                                finalHolder.adapterItem.getNamaReal(),
//                                finalHolder.adapterItem.getKodeReal(),
//                                finalHolder.adapterItem.getSatuan(),
//                        );
//                    }
//                });
//                holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        deleteSelectedItem(finalHolder.adapterItem.getIndex());
//                    }
//                });
//            }
            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvNama.setText(holder.adapterItem.getNamaBarang().toUpperCase());
            holder.tvKode.setText(holder.adapterItem.getKodeBarang().toUpperCase());
            holder.tvJumlah.setText(holder.adapterItem.getJumlah().toUpperCase());
            holder.tvSatuan.setText(holder.adapterItem.getSatuan().toUpperCase());
        }
    }

    //untuk menjalankan perintah send data new ke web service
    private void sendData(){
        String actionUrl = "Order/insertPraorder/";
        new InsertingData().execute(actionUrl);
    }

    //class yang digunakan untuk insert data
    private class InsertingData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                jsonObject.put("nomorCustomer", LibInspira.getShared(global.temppreferences, global.temp.praorder_customer_nomor, ""));
                jsonObject.put("nomorSales", LibInspira.getShared(global.temppreferences, global.temp.praorder_sales_nomor, ""));
                jsonObject.put("nomorJenisHarga", LibInspira.getShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, ""));
                jsonObject.put("keterangan", LibInspira.getShared(global.temppreferences, global.temp.praorder_keterangan, ""));
                jsonObject.put("tanggal", LibInspira.getShared(global.temppreferences, global.temp.praorder_date, ""));

                jsonObject.put("nomorCabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                jsonObject.put("namaCabang", LibInspira.getShared(global.userpreferences, global.user.kodecabang, ""));
                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));



                //-------------------------------------------------------------------------------------------------------//
                //---------------------------------------------DETAIL----------------------------------------------------//
                // untuk new dulu
                jsonObject.put("dataitemdetail", LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, ""));  //mengirimkan data item
                Log.d("detailitemdetail", LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // ## jngan lupa di kembaliin
            return LibInspira.executePost_local(getContext(), urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("resultQuery", result);
            try {
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            LibInspira.hideLoading();
                            LibInspira.ShowLongToast(getContext(), "Data has been successfully added");
                            LibInspira.clearShared(global.temppreferences); //hapus cache jika data berhasil ditambahkan
                            LibInspira.BackFragmentCount(getFragmentManager(), 3);  //kembali ke menu depan sales order
                        }
                        else
                        {
                            LibInspira.ShowShortToast(getContext(), "Adding new data failed err:query");
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.ShowShortToast(getContext(), "Adding new data failed err:network");
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "Inserting Data", "Loading");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    private void deleteItem()
    {
        String actionUrl = "Order/deletePraorderItem/";
        new DeleteItemData().execute(actionUrl);
    }

    //class yang digunakan untuk delete item data button tong sampah
    private class DeleteItemData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                // kalau beda berarti di edit
//                if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_header_edit,"").equals(
//                        LibInspira.getShared(global.temppreferences, global.temp.praorder_summary,"") ))
//                {
                jsonObject.put("nomorItem", LibInspira.getShared(global.temppreferences, global.temp.praorder_delete_item_nomor, ""));
                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                //}

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // ## jngan lupa di kembaliin
            return LibInspira.executePost_local(getContext(), urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("resultQuery", result);
            try {
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            LibInspira.hideLoading();
                            LibInspira.ShowLongToast(getContext(), "Data has been successfully DELETED");
                            deleteSelectedItem(Integer.parseInt(LibInspira.getShared(global.temppreferences, global.temp.praorder_delete_item_index,"")));

                            //reset
                            LibInspira.setShared(global.temppreferences, global.temp.praorder_delete_item_nomor,"");
                            LibInspira.setShared(global.temppreferences, global.temp.praorder_delete_item_index,"");

                        }
                        else
                        {
                            LibInspira.ShowShortToast(getContext(), "DELETE data failed err:query/DB");
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.ShowShortToast(getContext(), "DELETE data failed err:network");
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "DELETING Data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

}

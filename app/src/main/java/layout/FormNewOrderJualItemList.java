package layout;

/**
 * Created by Arta on 09-Nov-17.
 */

/******************************************************************************
 Author           :
 Description      : untuk menampilkan detail item dalam bentuk list
 History          :

 ******************************************************************************/

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

public class FormNewOrderJualItemList extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    protected ItemListAdapter itemadapter;
    protected ArrayList<ItemAdapter> list;
    protected String jenisDetail;  //added by Tonny @16-Sep-2017
    private Button btnBack, btnNext, btnRefresh;
    private FloatingActionButton fab;
    protected String strData;

    public FormNewOrderJualItemList() {
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
        View v = inflater.inflate(R.layout.fragment_order_jual_form_item_list, container, false);
        getActivity().setTitle("orderjual - List Item");

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
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        list = new ArrayList<ItemAdapter>();

        //layoutnya samain aja
        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_orderjual_summary_item, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        btnBack = (Button) getView().findViewById(R.id.btnBack);
        btnNext = (Button) getView().findViewById(R.id.btnNext);
        //btnRefresh = (Button) getView().findViewById(R.id.btnRefresh);


//            btnBack.setVisibility(View.GONE);
//            btnSave.setVisibility(View.GONE);
//            fab.setVisibility(View.GONE);
        fab.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        fab.setVisibility(View.GONE);

        if (!LibInspira.getShared(global.temppreferences, global.temp.orderjual_current_praorder_nomor, "")
                .equals(LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, ""))) {
            // ### kalau data tidak sama dengan data sebelum maka, load data lagi dari awal, klo sama load dr sharedpreferences
            if (LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new")) {

                getData(); // load data list dari tdjualpraorder
            } else if (LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit")) {
                //load data dari sharedpreferences = refreshlist
            }
        }

        refreshList();

        //Log.d("onActivityCreated: ", "list item created");
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
            if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit"))
            {
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_submenu, "new_from_edit");
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new"))
            {
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_submenu, "new_from_add_new");
            }

            //reset value nya
            resetSharedAddItem();

            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualItem());
        }
//        else if(id == R.id.btnRefresh)
//        {
//           LibInspira.alertBoxYesNo("Refresh List", "Apakah anda ingin melakukan refresh list?", getActivity(), new Runnable() {
//                    public void run() {
//                        //YES
//                        getData();
//                        refreshList();
//                    }
//           }, new Runnable() {
//                    public void run() {
//                        //NO
//                    }
//                });
//
//        }
        else if(id==R.id.btnBack)
        {
            LibInspira.BackFragment(getActivity().getSupportFragmentManager());

        }
        else if(id==R.id.btnNext)
        {
            //sendData(); // by pass insert
            if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new"))
            {
                if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_item_add, "").equals(""))
                {
                    //sendData();
                    //ke fragment summary orderjual
                    //LibInspira.ShowShortToast(getActivity(),"summary oj");
                    LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualSummary());
                }
                else
                {
                    LibInspira.ShowLongToast(getActivity(),"Data Item Masih Kosong");
                }

//                LibInspira.alertBoxYesNo("Menambah baru", "Apakah anda yakin ingin Menambah data baru?", getActivity(), new Runnable() {
//                    public void run() {
//                        //YES
//                        // cek if item is null ga bs insert
//                        if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "").equals(""))
//                        {
//                            sendData();
//                        }
//                        else
//                        {
//                            LibInspira.ShowLongToast(getActivity(),"Data Item Masih Kosong");
//                        }
//
//                    }
//                }, new Runnable() {
//                    public void run() {
//                        //NO
//                    }
//                });
//                //yes no kasih msg
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit"))
            {
                //lanjut ke summary
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualSummary());
            }
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "").equals("")){
//                LibInspira.ShowShortToast(getContext(), "No item selected. Please add item to proceed");
//            }else{
//                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderDetailJasaListFragment());
//            }
        }
    }

    protected void resetSharedAddItem()
    {
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_index_edit, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_item_add , "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nama_barang_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_barang_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_kode_barang_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_jumlah_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_satuan_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nama_satuan_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_harga_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_netto_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_stok_terkini_add, "");
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
        strData = LibInspira.getShared(global.temppreferences, global.temp.orderjual_item_add, "");
        Log.d("ojil-re","asd"+strData);
        if (!strData.equals("")){
            //return;

            String data = strData;
            String[] pieces = data.trim().split("\\|");
            if(pieces.length == 1 && pieces[0].equals(""))
            {
                //do nothing
                Log.d("ojil-re","if");
            }
            else
            {
                Log.d("ojil-re","else");
                for(int i=0 ; i < pieces.length ; i++)
                {
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

                            //#NEWEDIT
                            if(!parts[12].equals("1")) // status delete
                            {

                                ItemAdapter dataItem = new ItemAdapter();
                                dataItem.setIndex(i);

                                // ############
//                                data[0] = obj.getString("nomor");
//                                data[1] = obj.getString("nomorBarang");
//                                data[2] = obj.getString("kodeBarang");
//                                data[3] = obj.getString("namaBarang");
//                                data[4] = obj.getString("jumlah");
//                                data[5] = obj.getString("nomorSatuan");
//                                data[6] = obj.getString("namaSatuan");
//                                data[7] = obj.getString("harga");
//                                data[8] = obj.getString("diskon");
//                                data[9] = obj.getString("netto");
//                                data[10] = obj.getString("subtotal");
//                                data[11] = obj.getString("stokTerkini");

                                dataItem.setNomor(parts[0]);
                                dataItem.setNomorBarang(parts[1]);
                                dataItem.setKodeBarang(parts[2]);
                                dataItem.setNamaBarang(parts[3]);
                                dataItem.setJumlah(LibInspira.delimeter(parts[4]));
                                dataItem.setNomorSatuan(parts[5]);
                                dataItem.setNamaSatuan(parts[6]);
                                dataItem.setHarga(parts[7].replaceAll(",", ""));
                                dataItem.setDiskon(parts[8]);
                                dataItem.setNetto(parts[9].replaceAll(",", ""));
                                dataItem.setSubtotal(parts[10].replaceAll(",", ""));
                                dataItem.setStokTerkini(parts[11]);

                                //#NEWEDIT
                                dataItem.setStatus(parts[12]);
//                            if (LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit")) {
//
//                            }

                                list.add(dataItem);

                                itemadapter.add(dataItem);
                                itemadapter.notifyDataSetChanged();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            LibInspira.ShowShortToast(getContext(), "The current data is invalid. Please add new data.");
                            LibInspira.setShared(global.temppreferences, global.temp.orderjual_item_add, "");
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
                else
                {
                    if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit")) {
                        String temp = pieces[i].substring(0, pieces[i].length() - 1) + "1";
                        Log.d("NEWEDIT", temp);
                        newdata = newdata + temp + "|";
                    }
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


    protected void setStrData(String newdata){
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_item_add, newdata);
        strData = newdata;
    }


    //edit klo dari add new
    protected void setEditData(String index, String nomor, String namaBarang, String kodeBarang,
                               String nomorBarang , String nomorSatuan, String namaSatuan, String jumlah,
                               String harga, String diskon, String netto, String subtotal, String stok){

       // LibInspira.setShared(global.temppreferences, global.temp.orderjual_menu, "edit");


        LibInspira.setShared(global.temppreferences, global.temp.orderjual_index_edit, index);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_item_add , nomor);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nama_barang_add, namaBarang);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_barang_add, nomorBarang);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_kode_barang_add, kodeBarang);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_jumlah_add, jumlah);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_satuan_add, nomorSatuan);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nama_satuan_add, namaSatuan);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_harga_add, harga);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_add, diskon);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_netto_add, netto);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal_add, subtotal);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_stok_terkini_add, stok);

        Log.d("editData",index+" "+nomor+" "+namaBarang+" "+kodeBarang+" "+nomorBarang+" "+
                nomorSatuan+" "+namaSatuan+" "+jumlah+" "+harga+" "+diskon+" "+netto+" "+subtotal+" "+stok);

        LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualItem());
    }

    //edit klo dr edit //#NEWEDIT
    protected void setEditData(String index, String nomor, String namaBarang, String kodeBarang,
                               String nomorBarang , String nomorSatuan, String namaSatuan, String jumlah,
                               String harga, String diskon, String netto, String subtotal, String stok, String status){

        // LibInspira.setShared(global.temppreferences, global.temp.orderjual_menu, "edit");


        LibInspira.setShared(global.temppreferences, global.temp.orderjual_index_edit, index);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_item_add , nomor);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nama_barang_add, namaBarang);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_barang_add, nomorBarang);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_kode_barang_add, kodeBarang);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_jumlah_add, jumlah);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nomor_satuan_add, nomorSatuan);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_nama_satuan_add, namaSatuan);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_harga_add, harga);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_add, diskon);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_netto_add, netto);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal_add, subtotal);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_stok_terkini_add, stok);
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_status_item_add, status);

        Log.d("editData",index+" "+nomor+" "+namaBarang+" "+kodeBarang+" "+nomorBarang+" "+
                nomorSatuan+" "+namaSatuan+" "+jumlah+" "+harga+" "+diskon+" "+netto+" "+subtotal+" "+stok+" status"+status);

        LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualItem());
    }

    public class ItemAdapter {

        private int index;
        private String nomor = "0"; // klo add new ga perlu nomor, jadi di default dummy, klo edit bru di ganti
        private String nomorBarang;
        private String namaBarang;
        private String kodeBarang;
        private String jumlah;
        private String nomorSatuan;
        private String namaSatuan;
        private String harga;
        private String diskon;
        private String netto;
        private String subtotal;
        private String stokTerkini;

        private String status; // 0 normal - 1 delete - 2 edit;

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

        public void setNamaSatuan(String namaSatuan) {
            this.namaSatuan = namaSatuan;
        }
        public String getNamaSatuan() {
            return namaSatuan;
        }

        public void setJumlah(String jumlah) {
            this.jumlah = jumlah;
        }
        public String getJumlah() {
            return jumlah;
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

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
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
            ImageButton ibtnDelete;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Holder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            holder = new Holder();
            holder.adapterItem = items.get(position);

            holder.tvNomor = (TextView) row.findViewById(R.id.tvNomor); // default di xml visibility GONE
            holder.tvKodeBarang = (TextView) row.findViewById(R.id.tvKodeBarang);
            holder.tvNamaBarang = (TextView) row.findViewById(R.id.tvNamaBarang);
            holder.tvSatuan = (TextView) row.findViewById(R.id.tvSatuan);
            holder.tvJumlah = (TextView) row.findViewById(R.id.tvJumlah);

            holder.tvHarga = (TextView) row.findViewById(R.id.tvHarga);
            holder.tvDiskon = (TextView) row.findViewById(R.id.tvDiskon);
            holder.tvNetto = (TextView) row.findViewById(R.id.tvNetto);
            holder.tvSubtotal = (TextView) row.findViewById(R.id.tvSubtotal);
            holder.tvStokTerkini = (TextView) row.findViewById(R.id.tvStokTerkini);

            holder.ibtnDelete = (ImageButton) row.findViewById(R.id.ibtnDelete);

            row.setTag(holder);
            setupItem(holder);

            if (LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new")) {
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
                        LibInspira.setShared(global.temppreferences, global.temp.orderjual_submenu, "edit_from_add_new");
                        setEditData(items.get(position).getIndex() + "",
                                items.get(position).getNomor(),
                                items.get(position).getNamaBarang(),
                                items.get(position).getKodeBarang(),
                                items.get(position).getNomorBarang(),
                                items.get(position).getNomorSatuan(),
                                items.get(position).getNamaSatuan(),
                                items.get(position).getJumlah(),
                                items.get(position).getHarga(),
                                items.get(position).getDiskon(),
                                items.get(position).getNetto(),
                                items.get(position).getSubtotal(),
                                items.get(position).getStokTerkini());
                    }
                });
            } else if (LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit")) {
                holder.ibtnDelete.setVisibility(View.VISIBLE);
                holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // lakukan method delete
                        LibInspira.alertBoxYesNo("Delete item", "Apakah anda ingin menghapus item?", getActivity(), new Runnable() {
                            public void run() {
                                //YES
                                //#NEWEDIT
                                //deleteItem();
                                deleteSelectedItem(items.get(position).getIndex());
                                LibInspira.setShared(global.temppreferences, global.temp.orderjual_delete_item_index, items.get(position).getIndex() + "");
                                LibInspira.setShared(global.temppreferences, global.temp.orderjual_delete_item_nomor, items.get(position).getNomor() + "");
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
                        //#NEWEDIT
                        LibInspira.setShared(global.temppreferences, global.temp.orderjual_submenu, "edit_from_edit");
                        setEditData(items.get(position).getIndex() + "",
                                items.get(position).getNomor(),
                                items.get(position).getNamaBarang(),
                                items.get(position).getKodeBarang(),
                                items.get(position).getNomorBarang(),
                                items.get(position).getNomorSatuan(),
                                items.get(position).getNamaSatuan(),
                                items.get(position).getJumlah(),
                                items.get(position).getHarga(),
                                items.get(position).getDiskon(),
                                items.get(position).getNetto(),
                                items.get(position).getSubtotal(),
                                items.get(position).getStokTerkini(),
                                items.get(position).getStatus()
                        );
                    }
                });
            }

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvNomor.setText(holder.adapterItem.getNomor().toUpperCase());
            holder.tvKodeBarang.setText(holder.adapterItem.getKodeBarang().toUpperCase());
            holder.tvNamaBarang.setText(holder.adapterItem.getNamaBarang().toUpperCase());
            holder.tvSatuan.setText(holder.adapterItem.getNamaSatuan().toUpperCase());
            holder.tvJumlah.setText(LibInspira.delimeter(holder.adapterItem.getJumlah().toUpperCase()));

            holder.tvHarga.setText(LibInspira.delimeter(holder.adapterItem.getHarga().toUpperCase(),true));
            holder.tvDiskon.setText(LibInspira.delimeter(holder.adapterItem.getDiskon().toUpperCase())+" %");
            holder.tvNetto.setText(LibInspira.delimeter(holder.adapterItem.getNetto().toUpperCase()));
            holder.tvSubtotal.setText(LibInspira.delimeter(holder.adapterItem.getSubtotal().toUpperCase()));
            holder.tvStokTerkini.setText(LibInspira.delimeter(holder.adapterItem.getStokTerkini().toUpperCase()));
        }
    }

    //### buat fugnsi baru untuk edit, load data dari tdjualorder langusng

    private void getData()
    {
        String actionUrl = "Order/getOrderJualItemFromPraorder/";
        new getData().execute(actionUrl);
    }
    // get data list berdasar nomot thpraorder
    private class getData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //#SERVER_LOCAL
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
                if(jsonarray.length() > 0)
                {
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query"))
                        {
                            int sizeData = 12;
                            String[] data = new String[sizeData];
                            data[0] = obj.getString("nomor");
                            data[1] = obj.getString("nomorBarang");
                            data[2] = obj.getString("kodeBarang");
                            data[3] = obj.getString("namaBarang");
                            data[4] = obj.getString("jumlah");
                            data[5] = obj.getString("nomorSatuan");
                            data[6] = obj.getString("namaSatuan");
                            //data[7] = obj.getString("nomorJenisHarga");
                            //data[8] = obj.getString("namaJenisHarga");
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
                                    + "~" + data[11]+ "~" +0+ "|";
                            //#NEWEDIT
                            Log.d("hasil",tempData);
                        }
                        else
                        {
                            LibInspira.ShowShortToast(getContext(),"err : DB loading data");
                        }
                    }
                    //set current praorder nomor
                    if(!tempData.equals(""))
                    {
                        LibInspira.setShared(global.temppreferences, global.temp.orderjual_current_praorder_nomor,
                                LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, ""));
                    }

//                    if(!tempData.equals(LibInspira.getShared(global.temppreferences, global.temp.salesorder_summary, "")))
//                    {
                    LibInspira.setShared(
                            global.temppreferences,
                            global.temp.orderjual_item_add,
                            tempData
                    );
                    //refreshList();
//                    }
                }
                else
                {
                    LibInspira.ShowShortToast(getContext(),"err : loading data");
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

    private void deleteItem()
    {
        String actionUrl = "Order/deleteOrderJualItem/";
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
                jsonObject.put("nomorItem", LibInspira.getShared(global.temppreferences, global.temp.orderjual_delete_item_nomor, ""));
                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                //}

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // ## jngan lupa di kembaliin
            //#SERVER_LOCAL
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
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
                            deleteSelectedItem(Integer.parseInt(LibInspira.getShared(global.temppreferences, global.temp.orderjual_delete_item_index,"")));

                            //reset
                            LibInspira.setShared(global.temppreferences, global.temp.orderjual_delete_item_nomor,"");
                            LibInspira.setShared(global.temppreferences, global.temp.orderjual_delete_item_index,"");

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

package layout;

/**
 * Created by Arta on 10-Nov-17.
 */

/******************************************************************************
 Author           :
 Description      : untuk menampilkan detail item pada order jual
 History          :

 ******************************************************************************/

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inspira.babies.GlobalVar;
import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.jsonObject;

public class FormNewOrderJualItem extends Fragment implements View.OnClickListener{

    protected TextView tvKodeBarang,tvNamaBarang,tvSatuan, tvNetto, tvSubtotal, tvStokTerkini, tvDiskonNom;
    protected EditText etJumlah,etHarga,etDiskon;
    protected Button btnAdd;
    Context con;
    String strData = "";

    public FormNewOrderJualItem() {
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
        View v = inflater.inflate(R.layout.fragment_orderjual_form_detail_item, container, false);
        getActivity().setTitle("Order Jual - New Item");
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

    //added by Tonny @15-Jul-2017
    //untuk mapping UI pada fragment, jangan dilakukan pada OnCreate, tapi dilakukan pada onActivityCreated
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        tvKodeBarang = (TextView) getView().findViewById(R.id.tvKodeBarang);
        tvNamaBarang = (TextView) getView().findViewById(R.id.tvNamaBarang);
        tvSatuan = (TextView) getView().findViewById(R.id.tvSatuan);
        tvNetto = (TextView) getView().findViewById(R.id.tvNetto);
        tvSubtotal = (TextView) getView().findViewById(R.id.tvSubtotal);
        tvStokTerkini = (TextView) getView().findViewById(R.id.tvStokTerkini);
        tvDiskonNom = (TextView) getView().findViewById(R.id.tvDiscNominal);

        etJumlah = (EditText) getView().findViewById(R.id.etJumlah);
        etHarga = (EditText) getView().findViewById(R.id.etHarga);
        etDiskon = (EditText) getView().findViewById(R.id.etDiskon);

        btnAdd = (Button) getView().findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_index_edit, "").equals(""))
        {
            btnAdd.setText("ADD");
        }
        else
        {
            btnAdd.setText("EDIT");
        }

        //tvKodeBarang.setOnClickListener(this);

        refreshData();
        init();
    }

    //added by Tonny @02-Sep-2017  untuk inisialisasi textwatcher pada komponen
    protected void init(){
        etHarga.setHint("0");
        etJumlah.setHint("0");
        etDiskon.setHint("0");

        if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_jumlah_add, "").equals("")) {
            etJumlah.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_jumlah_add, ""));
        }
        if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_add, "").equals("")) {
            etDiskon.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_add, "")));
        }
        if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_harga_add, "").equals("")) {
            etHarga.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.orderjual_harga_add, "")));
        }

        if(etHarga.getText().toString().equals("")) {
            etHarga.setText("0");
        }
        if(etDiskon.getText().toString().equals("")) {
            etDiskon.setText("0");
        }
        if(etJumlah.getText().toString().equals("")) {
            etJumlah.setText("0");
        }

        etHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etHarga, this, true, false);
                nullStringtoZero(etHarga,this);
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_harga_add, etHarga.getText().toString().replace(",", ""));
                calc();
                //refreshData();
            }
        });


        etDiskon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etDiskon, this, true, false);
                nullStringtoZero(etDiskon,this);
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_add, etDiskon.getText().toString().replace(",", ""));
                //refreshData();
                calc();
            }
        });

        etJumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etJumlah, this, true, false);
                nullStringtoZero(etJumlah,this);
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_jumlah_add, etJumlah.getText().toString().replace(",", ""));
                //refreshData();
                calc();
            }
        });
    }

    protected void refreshData()
    {
        tvKodeBarang.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_kode_barang_add, ""));
        tvNamaBarang.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_nama_barang_add, ""));
        tvSatuan.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_nama_satuan_add, ""));
        tvNetto.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.orderjual_netto_add, "")));
        tvSubtotal.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.orderjual_subtotal_add, "")));
        tvStokTerkini.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.orderjual_stok_terkini_add, "")));

        calc();
    }

    private void calc()
    {
        //Log.d("oditem",LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_add, "0"));
        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_jumlah_add, "").equals(""))
        {LibInspira.setShared(global.temppreferences, global.temp.orderjual_jumlah_add, "0");}
        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_harga_add, "").equals(""))
        {LibInspira.setShared(global.temppreferences, global.temp.orderjual_harga_add, "0");}
        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_add, "").equals(""))
        {LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_add, "0");}


        Double qty = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.orderjual_jumlah_add, "0"));
        Double price = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.orderjual_harga_add, "0"));
        Double disc = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_add, "0"));

        Double discNominalPerItem = price * disc / 100;
        Double netto = price - discNominalPerItem;
        Double subtotal = netto * qty;

        tvDiskonNom.setText(LibInspira.delimeter(String.valueOf(discNominalPerItem)));
        tvNetto.setText(LibInspira.delimeter(String.valueOf(netto)));
        tvSubtotal.setText(LibInspira.delimeter(String.valueOf(subtotal)));
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal_add, subtotal.toString());
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_netto_add, subtotal.toString());
    }

    protected void nullStringtoZero(EditText et, TextWatcher tw)
    {
        //set default value et jadi 0 jika tidak di isi apa2
        et.removeTextChangedListener(tw);
        if(et.getText().toString().equals(""))
        {
            et.setText("0");
            et.setSelection(et.getText().length());
        }
        et.addTextChangedListener(tw);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(GlobalVar.buttoneffect);
        int id = view.getId();

        LibInspira.setShared(global.sharedpreferences, global.shared.position, "orderjual");
//        if(id == R.id.tvKodeBarang)
//        {
//            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseJenisFragment());
//        }

        if (id==R.id.btnAdd) //modified by Tonny @01-Sep-2017
        {
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_jumlah_add, etJumlah.getText().toString());
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_harga_add, etHarga.getText().toString());
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_add, etDiskon.getText().toString());

            if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_kode_barang_add, "").equals("")){
                LibInspira.ShowShortToast(getContext(), "There is no item to add.");
                return;
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new"))
            {
                //MODE ADD
                //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

                if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_submenu, "").equals("new_from_add_new")) {
//                    strData = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "") + //praorder di bagian depan
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add, "") + "~" + // kalau new nomor diabaikan
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add, "") + "|";
//
//                    Log.d("strData add", strData);
//
//                    LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, "");
//                    LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, strData);
//                    LibInspira.BackFragment(getActivity().getSupportFragmentManager());
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_submenu, "").equals("edit_from_add_new"))
                {
                    //edit klo dari add new
                    editStrItem();
                }
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit"))
            {
//                //MODE EDIT
//                if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_submenu, "").equals("edit_from_edit"))
//                {
//                    // masuk sini kalau edit item dari yang summary, bukan dari yang add new
//                    // panggil fungsi edit item
//                    editPraorderItemData();
//                }
//                else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_submenu, "").equals("new_from_edit"))
//                {
//                    // add new dari data yang sebelum nya sdh ada
//                    //data nya cmn satu
//                    //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());
//
//                    strData = "";
//                    strData = LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add,"") + "~" + // kalau new nomor diabaikan
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add,"") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "")+ "~" +
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,"") + "|";
//
//                    LibInspira.setShared(global.temppreferences, global.temp.praorder_item_edit_new, strData);
//
//                    sendItemWithPrevHeaderData();
//                }

                //LibInspira.setShared(global.temppreferences, global.temp.praorder_item_edit, item_edit);
            }

        }
    }

    private void editStrItem()
    {
        String[] pieces = LibInspira.getShared(global.temppreferences, global.temp.orderjual_item_add, "").trim().split("\\|");
        for(int i=0 ; i < pieces.length ; i++){
            if(i != Integer.parseInt(LibInspira.getShared(global.temppreferences, global.temp.orderjual_index_edit, "")))
            {
                strData = strData + pieces[i] + "|";
            }
            else
            {
                //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

                strData = strData + //praorder di bagian depan
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_nomor_item_add , "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_nomor_barang_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_kode_barang_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_nama_barang_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_jumlah_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_nomor_satuan_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_nama_satuan_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_nomor_jenis_harga_add, "0")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_nama_jenis_harga_add, "0")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_harga_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_netto_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_subtotal_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_stok_terkini_add, "") + "|";

                Log.d("strData edit", strData);
            }
        }

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_index_edit, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_item_add, strData);

        LibInspira.BackFragment(getActivity().getSupportFragmentManager());
    }

    private void editPraorderItemData()
    {
        String actionUrl = "Order/updatePraorderItem/";
        new EditPraorderItemData().execute(actionUrl);
    }
    //class yang digunakan edit data item predorder yang sdh di db
    private class EditPraorderItemData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                // kalau beda berarti di edit
//                if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_header_edit,"").equals(
//                        LibInspira.getShared(global.temppreferences, global.temp.praorder_summary,"") ))
//                {

                jsonObject.put("nomorItem", LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add, ""));

                jsonObject.put("nomorBarang", LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add,""));
                jsonObject.put("nomorSatuan", LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, ""));
                jsonObject.put("jumlah", LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,""));
                //Log.d("sumedit",LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, ""));

                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                //Log.d("strData edit", strData);

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
                            LibInspira.ShowShortToast(con, "Pra Order Item has been successfully EDITED");

                            editStrItem();

                            //setupStart();
                            //LibInspira.clearShared(global.temppreferences); //hapus cache jika data berhasil ditambahkan
                            //LibInspira.BackFragmentCount(getFragmentManager(), 2);  //kembali ke menu depan sales order
                        }
                        else
                        {
                            LibInspira.ShowShortToast(con, "EDIT Pra Order Item failed err:query/DB");
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.ShowShortToast(con, "EDIT Pra Order Item failed err:network");
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "EDITING Pra Order Item", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }


    private void sendItemWithPrevHeaderData(){
        String actionUrl = "Order/insertItemPraorder/";
        new InsertingData().execute(actionUrl);
    }

    //class yang digunakan untuk insert data item new dai fitur edit
    private class InsertingData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.praorder_header_nomor, ""));
                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));

                //-------------------------------------------------------------------------------------------------------//
                //---------------------------------------------DETAIL----------------------------------------------------//
                // untuk new dulu
                jsonObject.put("dataitemdetail",  LibInspira.getShared(global.temppreferences, global.temp.praorder_item_edit_new, ""));  //mengirimkan data item
                Log.d("detailitemdetail", LibInspira.getShared(global.temppreferences, global.temp.praorder_item_edit_new, ""));
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

                            LibInspira.setShared(global.temppreferences, global.temp.praorder_item_edit_new, "");

                            String temp;
                            temp = LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add,"") + "~" + // kalau new nomor diabaikan
                                    LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add,"") + "~" +
                                    LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
                                    LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
                                    LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
                                    LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "")+ "~" +
                                    LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,"") + "|" +
                                    LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "");//praorder sebelumnya

                            Log.d("insert_edit_str", temp);

                            LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, "");
                            LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, temp);

                            LibInspira.BackFragment(getActivity().getSupportFragmentManager());
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
}

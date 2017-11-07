/******************************************************************************
 Author           : ADI
 Description      : untuk menampilkan detail item pada sales order
 History          :

 ******************************************************************************/
package layout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class FormNewPraOrderItem extends Fragment implements View.OnClickListener{

    protected TextView tvKodeBarang,tvNamaBarang,tvSatuan;
    protected EditText etJumlah;
    protected Button btnAdd;
    Context con;
    String strData = "";

    public FormNewPraOrderItem() {
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
        View v = inflater.inflate(R.layout.fragment_praorder_form_detail_item, container, false);
        getActivity().setTitle("PraOrder - New Item");
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

        etJumlah = (EditText) getView().findViewById(R.id.etJumlah);

        btnAdd = (Button) getView().findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        if(LibInspira.getShared(global.temppreferences, global.temp.praorder_index_edit, "").equals(""))
        {
            btnAdd.setText("ADD");
        }
        else
        {
            btnAdd.setText("EDIT");
        }

        tvKodeBarang.setOnClickListener(this);

        refreshData();
    }

    //added by Tonny @02-Sep-2017  untuk inisialisasi textwatcher pada komponen
//    protected void init(){
//        etPrice.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                LibInspira.formatNumberEditText(etPrice, this, true, false);
//                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_price, etPrice.getText().toString().replace(",", ""));
//                tvNetto.setText(etPrice.getText());
//                refreshData();
//            }
//        });
//
//        etFee.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                LibInspira.formatNumberEditText(etFee, this, true, false);
//                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_fee, etFee.getText().toString().replace(",", ""));
//                refreshData();
//            }
//        });
//
//        etDisc.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_disc, etDisc.getText().toString().replace(",", ""));
//                refreshData();
//            }
//        });
//
//        etQty.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_qty, etQty.getText().toString().replace(",", ""));
//                refreshData();
//            }
//        });
//
//        etPrice.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "0"));
//        etFee.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "0"));
//        etDisc.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "0"));
//        etQty.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "0"));
//
//        refreshData();
//    }

    protected void refreshData()
    {
//        tvItemReal.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nama_real, ""));
//        tvCodeReal.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_kode_real, ""));
//        tvItem.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nama, ""));
//        tvCode.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_kode, ""));

        tvKodeBarang.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add, ""));
        tvNamaBarang.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, ""));
        if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add, "").equals("")) {
            etJumlah.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add, ""));
        }
        tvSatuan.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, ""));

//        if(etPrice.getText().toString().equals("")){
//            etPrice.setText("0");
////        }else if(etQty.getText().toString().equals("")){
////            etQty.setText("0");
////        }else if(etFee.getText().toString().equals("")){
////            etFee.setText("0");
////        }else if(etDisc.getText().toString().equals("")){
////            etDisc.setText("0");
//        }

//        if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nama, "").equals(""))
//        {
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_price, "0");
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_fee, "0");
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_disc, "0");
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_qty, "0");
//
//            Double qty = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "0"));
//            Double price = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "0"));
//            Double fee = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "0"));
//            Double disc = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "0"));
//
//            Double discNominalPerItem = price * disc / 100;
//            Double netto = price - discNominalPerItem + fee;
//            Double subtotal = netto * qty;
//
//            //added by Tonny @05-Sep-2017
//            LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_subtotal, subtotal.toString());

//            tvDisc.setText(LibInspira.delimeter(String.valueOf(discNominalPerItem)));
//            tvNetto.setText(LibInspira.delimeter(String.valueOf(netto)));
//            tvSubtotal.setText(LibInspira.delimeter(String.valueOf(subtotal)));
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(GlobalVar.buttoneffect);
        int id = view.getId();

        LibInspira.setShared(global.sharedpreferences, global.shared.position, "praorder");
        if(id == R.id.tvKodeBarang)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseJenisFragment());
        }

        else if (id==R.id.btnAdd) //modified by Tonny @01-Sep-2017
        {
            LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add, "").equals("")){
                LibInspira.ShowShortToast(getContext(), "There is no item to add.");
                return;
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("add_new"))
            {
                //MODE ADD
                //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

                strData = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "") + //praorder di bagian depan
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add,"") + "~" + // kalau new nomor diabaikan
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add,"") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,"") + "|";

                Log.d("strData add", strData);

                LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, "");
                LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, strData);
                LibInspira.BackFragment(getActivity().getSupportFragmentManager());
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("edit"))
            {
                //MODE EDIT

                //String item_edit = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_edit, "");

                if(LibInspira.getShared(global.temppreferences, global.temp.praorder_submenu, "").equals("edit_from_edit"))
                {
                    // masuk sini kalau edit item dari yang summary, bukan dari yang add new
                    // panggil fungsi edit item
                    editPraorderItemData();
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_submenu, "").equals("new_from_edit"))
                {
                    // add new dari data yang sebelum nya sdh ada
                    //data nya cmn satu
                    //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());


                    strData = "";
                    strData = LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add,"") + "~" + // kalau new nomor diabaikan
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add,"") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "")+ "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,"") + "|";

                    LibInspira.setShared(global.temppreferences, global.temp.praorder_item_edit_new, strData);


                    sendItemWithPrevHeaderData();
                }
                else
                {
                    //edit klo dari add new
                    editStrItem();
                }

                //LibInspira.setShared(global.temppreferences, global.temp.praorder_item_edit, item_edit);
            }

        }
    }

    private void editStrItem()
    {
        String[] pieces = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "").trim().split("\\|");
        for(int i=0 ; i < pieces.length ; i++){
            if(i != Integer.parseInt(LibInspira.getShared(global.temppreferences, global.temp.praorder_index_edit, "")))
            {
                strData = strData + pieces[i] + "|";
            }
            else
            {
                //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

                strData = strData + //praorder di bagian depan
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add, "0") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add,"") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "")+ "~" +
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,"") + "|";

                Log.d("strData edit", strData);
            }
        }

        LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, "");
        LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, strData);

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

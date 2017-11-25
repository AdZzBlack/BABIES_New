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
                LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString().replace(",", ""));
                nullStringtoZero(etJumlah,this);
            }
        });

        refreshData();
    }


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

        else if (id==R.id.btnAdd)
        {
            LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add, "").equals("")){
                LibInspira.ShowShortToast(getContext(), "There is no item to add.");
                return;
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add, "0").equals("0"))
            {
                LibInspira.ShowShortToast(getContext(), "Jumlah tidak boleh 0");
                return;
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("add_new"))
            {
                //MODE ADD
                //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

                if(LibInspira.getShared(global.temppreferences, global.temp.praorder_submenu, "").equals("new_from_add_new")) {
                    strData = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "") + //praorder di bagian depan
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add, "") + "~" + // kalau new nomor diabaikan
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add, "") + "~" + "1" + "|";

                    Log.d("strData add", strData);

                    LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, "");
                    LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, strData);
                    LibInspira.BackFragment(getActivity().getSupportFragmentManager());
                }

                else if( LibInspira.getShared(global.temppreferences, global.temp.praorder_submenu, "").equals("edit_from_add_new"))
                {
                    //edit klo dari add new
                    editStrItem();
                }
            }
            else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("edit"))
            {
                //MODE EDIT

                //String item_edit = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_edit, "");

                if(LibInspira.getShared(global.temppreferences, global.temp.praorder_submenu, "").equals("edit_from_edit"))
                {
                    // masuk sini kalau edit item dari yang summary, bukan dari yang add new
                    // panggil fungsi edit item
                    editStrItem();
                    //editPraorderItemData();
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.praorder_submenu, "").equals("new_from_edit"))
                {
                    // add new dari data yang sebelum nya sdh ada
                    //data nya cmn satu
                    //LibInspira.setShared(global.temppreferences, global.temp.praorder_jumlah_add, etJumlah.getText().toString());

                    strData = "";
                    strData = LibInspira.getShared(global.temppreferences, global.temp.praorder_item_add, "") +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_item_add,"") + "~" + // kalau new nomor diabaikan
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_kode_barang_add,"") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_barang_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nama_barang_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_nomor_satuan_add, "") + "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_satuan_add, "")+ "~" +
                            LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,"") + "~" + "1" + "|";

                    Log.d("strData", "add from edit" + strData);
                    //LibInspira.setShared(global.temppreferences, global.temp.praorder_item_edit_new, strData);

                    LibInspira.setShared(global.temppreferences, global.temp.praorder_index_edit, "");
                    LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add, strData);
                    LibInspira.BackFragment(getActivity().getSupportFragmentManager());
                    //sendItemWithPrevHeaderData();
                }

                //LibInspira.setShared(global.temppreferences, global.temp.praorder_item_edit, item_edit);
            }

        }
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
                        LibInspira.getShared(global.temppreferences, global.temp.praorder_jumlah_add,"")+ "~" +
                        2 + "|";

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

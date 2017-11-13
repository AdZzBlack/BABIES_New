package layout;

/**
 * Created by Arta on 10-Nov-17.
 */

/******************************************************************************
 Author           : Tonny
 Description      : untuk menampilkan preview salesorder sebelum melakukan insert data
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

import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.jsonObject;

public class FormNewOrderJualSummary extends Fragment implements View.OnClickListener{
    private TextView tvCustomer, tvValuta, tvDate, tvSubtotal, tvTotal, tvDiscNominal, tvPPNNominal;
    private TextView tvPraorder, tvKurs;
    private EditText etDisc, etPPN, etKeterangan;
    private Button btnSave;
    private InsertingData insertingData;

    public FormNewOrderJualSummary() {
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
        View v = inflater.inflate(R.layout.fragment_order_jual_form_summary, container, false);
        getActivity().setTitle("Order Jual Summary");
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
        tvDate = (TextView) getView().findViewById(R.id.tvDate);
        tvCustomer = (TextView) getView().findViewById(R.id.tvCustomer);
        tvPraorder = (TextView) getView().findViewById(R.id.tvKodePraorder);
        tvValuta = (TextView) getView().findViewById(R.id.tvValuta);
        tvKurs = (TextView) getView().findViewById(R.id.tvKurs);

        tvSubtotal = (TextView) getView().findViewById(R.id.tvSubtotal);
        tvDiscNominal = (TextView) getView().findViewById(R.id.tvDiscNominal);
        tvPPNNominal = (TextView) getView().findViewById(R.id.tvPPNNominal);
        tvTotal = (TextView) getView().findViewById(R.id.tvGrandTotal);
        btnSave = (Button) getView().findViewById(R.id.btnSave);

        etPPN = (EditText) getView().findViewById(R.id.etPPN);
        etDisc = (EditText) getView().findViewById(R.id.etDisc);
        etKeterangan = (EditText) getView().findViewById(R.id.etKeterangan);

        btnSave.setOnClickListener(this);



//        etKeterangan.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                LibInspira.setShared(global.temppreferences, global.temp.orderjual_keterangan, etKeterangan.getText().toString());
//           }
//        });

        etDisc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etDisc, this, true, false);
                tvDiscNominal.setText(LibInspira.delimeter(getNominalDiskon().toString()));
                tvPPNNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
                tvTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_persen, etDisc.getText().toString().replace(",", ""));
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_nominal, tvDiscNominal.getText().toString().replace(",", ""));
            }
        });

        etPPN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etPPN, this, true, false);
                tvPPNNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
                tvTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_persen, etPPN.getText().toString().replace(",", ""));
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_nominal, tvPPNNominal.getText().toString().replace(",", ""));
            }
        });

        tvSubtotal.setText(LibInspira.delimeter(getSubtotal().toString()));
        tvTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));

        tvPraorder.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_kode, ""));
        tvKurs.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_kurs, ""));
        tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, ""));
        tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nama, ""));
        tvValuta.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_valuta_nama, ""));

        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new"))
        {
            etDisc.setText("0");
            etPPN.setText("0");

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_persen, "0");
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_nominal, "0");
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_persen, "0");
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_nominal, "0");
        }
        else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit"))
        {
            btnSave.setText("Finish");
            etPPN.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_ppn_persen, "0"));
            etDisc.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_persen, "0"));
            etKeterangan.setText( LibInspira.getShared(global.temppreferences, global.temp.orderjual_keterangan,""));
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btnSave)
        {
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal, getSubtotal().toString());
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_total, getGrandTotal().toString());
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_keterangan, etKeterangan.getText().toString());

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_totalrp, LibInspira.delimeter(
                    String.valueOf(
                            getGrandTotal() * Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.orderjual_kurs, "0"))
                    )).replaceAll(",",""));

//            Log.d("ojsumedit","rp "+LibInspira.delimeter(
//                    String.valueOf(
//                            getGrandTotal() * Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.orderjual_kurs, "0"))
//                    )).replaceAll(",",""));
//            Log.d("ojsumedit","sub "+LibInspira.getShared(global.temppreferences, global.temp.orderjual_subtotal, "").replaceAll(",",""));
//            Log.d("ojsumedit","total "+LibInspira.getShared(global.temppreferences, global.temp.orderjual_total, "0").replaceAll(",",""));

            if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new")) {
                LibInspira.alertBoxYesNo("Save Order Jual", "Apakah Data ingin di simpan?", getActivity(), new Runnable() {
                    public void run() {

                        sendData();
                    }
                }, new Runnable() {
                    public void run() {
                    }
                });
            }else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit"))
            {
                LibInspira.alertBoxYesNo("Selesai melakukan edit", "Apakah selesai mengubah data?", getActivity(), new Runnable() {
                    public void run() {
                    //editHeaderData();
                        //#NEWEDIT
                        updateAllData();
                    }
                }, new Runnable() {
                    public void run() {
                    }
                });
            }
        }
        else if(id==R.id.btnBack)
        {
            LibInspira.BackFragment(getActivity().getSupportFragmentManager());
        }
    }

    //untuk mendapatkan nominal diskon
    private Double getNominalDiskon(){
        Double discNominal = 0.0;
        if (tvSubtotal.getText().toString().equals("")){
            tvSubtotal.setText("0.0");
        }else if (etDisc.getText().toString().equals("")){
            etDisc.setText("0.0");
        }else {
            if (!tvSubtotal.getText().toString().equals("")) {
                Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
                Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
                discNominal = disc * subtotal / 100;
            }
        }
        return discNominal;
    }

    //untuk mendapatkan nominal ppn (setelah diskon)
    private Double getNominalPPN(){
        Double ppnNominal = 0.0;
        if (tvSubtotal.getText().toString().equals("")){
            tvSubtotal.setText("0.0");
        }else if(etPPN.getText().toString().equals("")){
            etPPN.setText("0.0");
        }else if (etDisc.getText().toString().equals("")){
            etDisc.setText("0.0");
        }else {
            Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
            Double ppn = Double.parseDouble(etPPN.getText().toString().replace(",", ""));
            Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
            Double discNominal = disc * subtotal / 100;
            ppnNominal = ppn * (subtotal - discNominal) / 100;
        }
        return ppnNominal;
    }
    //untuk mendapatkan nominal subtotal dari item dan pekerjaan
    private Double getSubtotal(){
        String data = LibInspira.getShared(global.temppreferences, global.temp.orderjual_item_add, "");
        Double dblSubtotal = 0.0; // subtotal dari sum(subtotal item_list_add)
        //Double dblItemSubtotal = 0.0;
        Log.d("data", data);
        String[] pieces = data.trim().split("\\|");
        Log.d("pieces length", Integer.toString(pieces.length));
        if((pieces.length >= 1 && !pieces[0].equals(""))){
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("~");
                    //#NEWEDIT
                    if(!parts[12].equals("1")) {
                        String subtotal = parts[10];
                        if (subtotal.equals("")) subtotal = "0";
                        //dblItemSubtotal = dblItemSubtotal + Double.parseDouble(subtotal);
                        dblSubtotal = dblSubtotal + Double.parseDouble(subtotal);
                        Log.d("subtotal item [" + i + "]", subtotal);
                    }
                }
            }
            //dblSubtotal = dblSubtotal*Double.parseDouble(tvKurs.getText().toString());
            //Log.d("ojsumedit","fsub"+dblSubtotal.toString());
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal, LibInspira.delimeter(dblSubtotal.toString()).replaceAll(",",""));
        }

        return dblSubtotal;
    }

    private Double getGrandTotal(){
        Double grandtotal = getSubtotal() - getNominalDiskon() + getNominalPPN();
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_total, LibInspira.delimeter(grandtotal.toString()).replaceAll(",",""));
        return grandtotal;
    }


    //added by Tonny @02-Sep-2017
    //untuk menjalankan perintah send data ke web service
    private void sendData(){
        String actionUrl = "Order/insertOrderjual/";
        insertingData = new InsertingData();
        insertingData.execute(actionUrl);
    }

    //added by Tonny @04-Sep-2017
    //class yang digunakan untuk insert data
    private class InsertingData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                jsonObject.put("nomorCustomer", LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nomor, ""));
                jsonObject.put("nomorValuta", LibInspira.getShared(global.temppreferences, global.temp.orderjual_valuta_nomor, ""));
                jsonObject.put("nomorPraorder", LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, ""));
                jsonObject.put("kurs", LibInspira.getShared(global.temppreferences, global.temp.orderjual_kurs, ""));
                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_subtotal, "").replaceAll(",",""));
                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.orderjual_total, "0").replaceAll(",",""));
                //jsonObject.put("totalrp", Double.toString(getGrandTotal() * Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.orderjual_kurs, "0"))));
                jsonObject.put("totalrp", LibInspira.getShared(global.temppreferences, global.temp.orderjual_totalrp, "0").replaceAll(",",""));
                jsonObject.put("ppnPersen", LibInspira.getShared(global.temppreferences, global.temp.orderjual_ppn_persen, "0"));
                jsonObject.put("ppnNominal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_ppn_nominal, "0"));
                jsonObject.put("diskonPersen", LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_persen, "0"));
                jsonObject.put("diskonNominal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_nominal, "0"));
                jsonObject.put("keterangan", LibInspira.getShared(global.temppreferences, global.temp.orderjual_keterangan, ""));
                jsonObject.put("tanggal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, ""));

                jsonObject.put("nomorCabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                jsonObject.put("kodeCabang", LibInspira.getShared(global.userpreferences, global.user.kodecabang, ""));
                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));

                //-------------------------------------------------------------------------------------------------------//
                //---------------------------------------------DETAIL----------------------------------------------------//
                jsonObject.put("dataitemdetail", LibInspira.getShared(global.temppreferences, global.temp.orderjual_item_add, ""));  //mengirimkan data item
                Log.d("detailitemdetail", LibInspira.getShared(global.temppreferences, global.temp.orderjual_item_add, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                            LibInspira.ShowShortToast(getContext(), "Data has been successfully added");
                            LibInspira.clearShared(global.temppreferences); //hapus cache jika data berhasil ditambahkan
                            LibInspira.BackFragmentCount(getFragmentManager(), 4);  //kembali ke menu depan sales order
                        }
                        else
                        {
                            LibInspira.ShowShortToast(getContext(), "Adding new data failed err:db");
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

    private void editHeaderData()
    {
        String actionUrl = "Order/updateOrderJualHeader/";
        new EditHeaderData().execute(actionUrl);
    }

    //class yang digunakan untuk insert data
    private class EditHeaderData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_nomor, ""));

                jsonObject.put("nomorCustomer", LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nomor, ""));
                jsonObject.put("nomorValuta", LibInspira.getShared(global.temppreferences, global.temp.orderjual_valuta_nomor, ""));
                jsonObject.put("nomorPraorder", LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, ""));
                jsonObject.put("kurs", LibInspira.getShared(global.temppreferences, global.temp.orderjual_kurs, ""));
                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_subtotal, "").replaceAll(",",""));
                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.orderjual_total, "0").replaceAll(",",""));
                jsonObject.put("totalrp", LibInspira.getShared(global.temppreferences, global.temp.orderjual_totalrp, "0").replaceAll(",",""));
                jsonObject.put("ppnPersen", LibInspira.getShared(global.temppreferences, global.temp.orderjual_ppn_persen, "0"));
                jsonObject.put("ppnNominal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_ppn_nominal, "0"));
                jsonObject.put("diskonPersen", LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_persen, "0"));
                jsonObject.put("diskonNominal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_nominal, "0"));

                jsonObject.put("keterangan", LibInspira.getShared(global.temppreferences, global.temp.orderjual_keterangan, ""));
                jsonObject.put("tanggal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, ""));

                jsonObject.put("nomorCabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                //jsonObject.put("namaCabang", LibInspira.getShared(global.userpreferences, global.user.kodecabang, ""));
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
                            LibInspira.ShowLongToast(getContext(), "Data has been successfully EDITED");
                            //setupStart();
                            //LibInspira.clearShared(global.temppreferences); //hapus cache jika data berhasil ditambahkan
                            LibInspira.BackFragmentCount(getFragmentManager(), 4);  //kembali ke menu depan sales order
                        }
                        else
                        {
                            LibInspira.ShowShortToast(getContext(), "EDIT data failed err:query/DB");
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.ShowShortToast(getContext(), "EDIT data failed err:network");
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "EDITING Data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }


    private void updateAllData()
    {
        String actionUrl = "Order/updateEditDeleteAllOrderJual/";
        new UpdateAllData().execute(actionUrl);
    }

    //class yang digunakan untuk insert data
    private class UpdateAllData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_nomor, ""));

                jsonObject.put("nomorCustomer", LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nomor, ""));
                jsonObject.put("nomorValuta", LibInspira.getShared(global.temppreferences, global.temp.orderjual_valuta_nomor, ""));
                jsonObject.put("nomorPraorder", LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, ""));
                jsonObject.put("kurs", LibInspira.getShared(global.temppreferences, global.temp.orderjual_kurs, ""));
                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_subtotal, "").replaceAll(",",""));
                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.orderjual_total, "0").replaceAll(",",""));
                jsonObject.put("totalrp", LibInspira.getShared(global.temppreferences, global.temp.orderjual_totalrp, "0").replaceAll(",",""));
                jsonObject.put("ppnPersen", LibInspira.getShared(global.temppreferences, global.temp.orderjual_ppn_persen, "0"));
                jsonObject.put("ppnNominal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_ppn_nominal, "0"));
                jsonObject.put("diskonPersen", LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_persen, "0"));
                jsonObject.put("diskonNominal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_diskon_nominal, "0"));

                jsonObject.put("keterangan", LibInspira.getShared(global.temppreferences, global.temp.orderjual_keterangan, ""));
                jsonObject.put("tanggal", LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, ""));

                jsonObject.put("nomorCabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                //jsonObject.put("namaCabang", LibInspira.getShared(global.userpreferences, global.user.kodecabang, ""));
                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                jsonObject.put("dataitemdetail", LibInspira.getShared(global.temppreferences, global.temp.orderjual_item_add, ""));  //mengirimkan data item
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
                            LibInspira.ShowLongToast(getContext(), "Data has been successfully EDITED");
                            //setupStart();
                            //LibInspira.clearShared(global.temppreferences); //hapus cache jika data berhasil ditambahkan
                            LibInspira.BackFragmentCount(getFragmentManager(), 4);  //kembali ke menu depan sales order
                        }
                        else
                        {
                            LibInspira.ShowShortToast(getContext(), "EDIT data failed err:query/DB");
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.ShowShortToast(getContext(), "EDIT data failed err:network");
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "EDITING Data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

}

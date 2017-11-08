package layout;

/**
 * Created by Arta on 08-Nov-17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.jsonObject;


public class OrderJualDetailHeaderFragment extends Fragment implements View.OnClickListener{
    //    private TextView tvCustomer, tvBroker, tvValuta, tvDate, tvSubtotal, tvGrandTotal, tvDiscNominal, tvPPNNominal;
//    private TextView tvPPN, tvDisc; //added by Tonny @17-Sep-2017  //untuk tampilan pada approval
//    private EditText etDisc, etPPN;
    private TextView tvKode,tvTanggal,tvCustomer,tvPraorder,tvValuta,tvKurs,
            tvKeterangan, tvSubtotal, tvDiskonPersen, tvDiskonNom, tvPPNpersen, tvPPNnom, tvTotal, tvTotalrp,
            tvStatus,tvSetujuOleh,tvSetujuPada;
    private Button btnEdit;
    private InsertingData insertingData;

    public OrderJualDetailHeaderFragment() {
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
        View v = inflater.inflate(R.layout.fragment_order_jual_detail_header, container, false);
        getActivity().setTitle("Detail Header");
//        Log.d("sumasd","on create");
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

        tvKode = (TextView) getView().findViewById(R.id.tvKode);
        tvTanggal= (TextView) getView().findViewById(R.id.tvTanggal);
        tvCustomer= (TextView) getView().findViewById(R.id.tvNamaKodeCustomer);

        tvPraorder = (TextView) getView().findViewById(R.id.tvKodePraorder);
        tvValuta = (TextView) getView().findViewById(R.id.tvValuta);
        tvKurs  = (TextView) getView().findViewById(R.id.tvKurs);
        tvSubtotal = (TextView) getView().findViewById(R.id.tvSubtotal);
        tvDiskonPersen = (TextView) getView().findViewById(R.id.tvDisc);
        tvDiskonNom = (TextView) getView().findViewById(R.id.tvDiscNominal);
        tvPPNpersen = (TextView) getView().findViewById(R.id.tvPPN);
        tvPPNnom = (TextView) getView().findViewById(R.id.tvPPNNominal);
        tvTotal = (TextView) getView().findViewById(R.id.tvTotal);
        tvTotalrp = (TextView) getView().findViewById(R.id.tvTotalrp);

        tvKeterangan= (TextView) getView().findViewById(R.id.tvKeterangan);
        tvStatus= (TextView) getView().findViewById(R.id.tvStatus);
        tvSetujuOleh = (TextView) getView().findViewById(R.id.tvSetujuOleh);
        tvSetujuPada = (TextView) getView().findViewById(R.id.tvSetujuPada);

        btnEdit = (Button) getView().findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);

        loadDataFromShared();

        //### reset submenu handle ketika back fragment dan, finish button di formNewPraorderItemList
        //LibInspira.setShared(global.temppreferences, global.temp.praorder_submenu, "");

        //Log.d("sumasd","activ created");


//        tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_date, ""));
//        tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_nama, ""));
//        tvBroker.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nama, ""));
//        tvValuta.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nama, ""));
//
//        if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") &&
//                !LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){
//            etPPN = (EditText) getView().findViewById(R.id.etPPN);
//            etDisc = (EditText) getView().findViewById(R.id.etDisc);
//            etDisc.setText("0");
//            etPPN.setText("0");
//            btnSave.setOnClickListener(this);
//            etDisc.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    LibInspira.formatNumberEditText(etDisc, this, true, false);
//                    tvDiscNominal.setText(LibInspira.delimeter(getNominalDiskon().toString()));
//                    tvPPNNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
//                    tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
//                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_disc, etDisc.getText().toString().replace(",", ""));
//                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_disc_nominal, tvDiscNominal.getText().toString().replace(",", ""));
//                }
//            });
//
//            etPPN.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    LibInspira.formatNumberEditText(etPPN, this, true, false);
//                    tvPPNNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
//                    tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
//                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_ppn, etPPN.getText().toString().replace(",", ""));
//                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_ppn_nominal, tvPPNNominal.getText().toString().replace(",", ""));
//                }
//            });
//
//            tvSubtotal.setText(LibInspira.delimeter(getSubtotal().toString()));
//            tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
//
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("nonppn"))
//            {
//                getView().findViewById(R.id.trPPN).setVisibility(View.GONE);
//            }
//
//        }else{  //added by Tonny @17-Sep-2017  jika untuk approval, beberapa property dihilangkan/diganti
//            tvPPN = (TextView) getView().findViewById(R.id.tvPPN);
//            tvDisc = (TextView) getView().findViewById(R.id.tvDisc);
//            btnSave.setVisibility(View.GONE);
//            tvSubtotal.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.salesorder_subtotal, "")));
//            tvGrandTotal.setText("Rp. " + LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.salesorder_total, "")));
//            tvDisc.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.salesorder_disc, "")));
//            tvDiscNominal.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.salesorder_disc_nominal, "")));
//            tvPPN.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.salesorder_ppn, "")));
//            tvPPNNominal.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.salesorder_ppn_nominal, "")));
//        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
//        if(id==R.id.btnEdit)
//        {
//            //BELUM DI BYPASS
//            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_selected_list_status, "").equals("1"))
//            {
//                //btnEdit.setVisibility(View.GONE);
//                LibInspira.ShowLongToast(getActivity(),"Tidak bisa diedit karena sudah di APPROVE");
//            }
//            else
//            {
//                //btnEdit.setVisibility(View.VISIBLE);
//                LibInspira.setShared(global.temppreferences, global.temp.praorder_menu, "edit");
//
//                if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_summary, "").equals(""))
//                {
////                    LibInspira.setShared(global.temppreferences, global.temp.praorder_header_edit,
////                            LibInspira.getShared(global.temppreferences, global.temp.praorder_summary, ""));
//                    trimDataShared(LibInspira.getShared(global.temppreferences, global.temp.praorder_summary, ""));
//                }
//                else{
//                    LibInspira.ShowShortToast(getActivity(),"error load data header");
//                }
//
//                // di isi list dr item list
//                if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_item, "").equals("")) {
//                    LibInspira.setShared(global.temppreferences, global.temp.praorder_item_add,
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_item, ""));
//                }
//                else{
//                    LibInspira.ShowShortToast(getActivity(),"error load data list items");
//                }
//
//                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewPraOrderHeader());
//            }
//        }
    }

    public void trimDataShared(String data)
    {
//        data[0] = obj.getString("nomor");
//        data[1] = obj.getString("kode");
//
//        data[2] = obj.getString("nomorCabang");
//        data[3] = obj.getString("namaCabang");
//        data[4] = obj.getString("kodeCabang");
//
//        data[5] = obj.getString("nomorCustomer");
//        data[6] = obj.getString("namaCustomer");
//        data[7] = obj.getString("kodeCustomer");
//
//        data[8] = obj.getString("nomorValuta");
//        data[9] = obj.getString("kodeValuta");
//        data[10] = obj.getString("simbolValuta");
//
//        data[11] = obj.getString("nomorPraorder");
//        data[12] = obj.getString("kodePraorder");
//
//        data[13] = obj.getString("tanggal");
//        data[14] = obj.getString("kurs");
//
//        data[15] = obj.getString("subtotal");
//
//        data[16] = obj.getString("diskonPersen");
//        data[17] = obj.getString("diskonNominal");
//        data[18] = obj.getString("ppnPersen");
//        data[19] = obj.getString("ppnNominal");
//
//        data[20] = obj.getString("total");
//        data[21] = obj.getString("totalrp");
//
//        data[22] = obj.getString("keterangan");
//        data[23] = obj.getString("status_disetujui");
//
//        data[24] = obj.getString("disetujui_oleh");
//        data[25] = obj.getString("disetujui_pada");


        if(!data.equals(""))
        {
            String[] parts = data.trim().split("\\~");

            LibInspira.setShared(global.temppreferences, global.temp.praorder_header_nomor, parts[0]);
            LibInspira.setShared(global.temppreferences, global.temp.praorder_header_kode, parts[6]);

            LibInspira.setShared(global.temppreferences, global.temp.praorder_customer_nomor, parts[8]);
            LibInspira.setShared(global.temppreferences, global.temp.praorder_customer_nama, parts[10]);

            LibInspira.setShared(global.temppreferences, global.temp.praorder_sales_nomor, parts[2]);
            LibInspira.setShared(global.temppreferences, global.temp.praorder_sales_nama, parts[3]);

            LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, parts[4]);
            LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nama, parts[5]);

            LibInspira.setShared(global.temppreferences, global.temp.praorder_date, parts[7].substring(0,10));
            LibInspira.setShared(global.temppreferences, global.temp.praorder_keterangan,parts[16] );
        }
    }

    private void loadDataFromShared()
    {
//        data[0] = obj.getString("nomor");
//        data[1] = obj.getString("kode");
//
//        data[2] = obj.getString("nomorCabang");
//        data[3] = obj.getString("namaCabang");
//        data[4] = obj.getString("kodeCabang");
//
//        data[5] = obj.getString("nomorCustomer");
//        data[6] = obj.getString("namaCustomer");
//        data[7] = obj.getString("kodeCustomer");
//
//        data[8] = obj.getString("nomorValuta");
//        data[9] = obj.getString("kodeValuta");
//        data[10] = obj.getString("simbolValuta");
//
//        data[11] = obj.getString("nomorPraorder");
//        data[12] = obj.getString("kodePraorder");
//
//        data[13] = obj.getString("tanggal");
//        data[14] = obj.getString("kurs");
//
//        data[15] = obj.getString("subtotal");
//
//        data[16] = obj.getString("diskonPersen");
//        data[17] = obj.getString("diskonNominal");
//        data[18] = obj.getString("ppnPersen");
//        data[19] = obj.getString("ppnNominal");
//
//        data[20] = obj.getString("total");
//        data[21] = obj.getString("totalrp");
//
//        data[22] = obj.getString("keterangan");
//        data[23] = obj.getString("status_disetujui");
//
//        data[24] = obj.getString("disetujui_oleh");
//        data[25] = obj.getString("disetujui_pada");

        //Log.d("sumasd","masuk");
        String data = LibInspira.getShared(global.temppreferences, global.temp.orderjual_summary, "");
        //Log.d("sumasd",data);
        //String[] pieces = data.trim().split("\\|");

        if(!data.equals(""))
        {
            String[] parts = data.trim().split("\\~");
            tvKode.setText(parts[1]);
            tvTanggal.setText(parts[13]);
            tvCustomer.setText(parts[7]+" - "+parts[6]);

            tvPraorder.setText(parts[12]);
            tvValuta.setText(parts[9]);
            tvKurs.setText(LibInspira.delimeter(parts[14]));
            tvSubtotal.setText(LibInspira.delimeter(parts[15]));

            tvDiskonPersen.setText(LibInspira.delimeter(parts[16]));
            tvDiskonNom.setText(LibInspira.delimeter(parts[17]));
            tvPPNpersen.setText(LibInspira.delimeter(parts[18]));
            tvPPNnom.setText(LibInspira.delimeter(parts[19]));

            tvTotal.setText(LibInspira.delimeter(parts[20]));
            tvTotalrp.setText(LibInspira.delimeter(parts[21]));

            tvKeterangan.setText(parts[22]);
            if(parts[23].equals("1"))
            {
                tvStatus.setText("APPROVE");
            }
            else if(parts[23].equals("0"))
            {
                tvStatus.setText("DISAPPROVE");
                tvSetujuOleh.setVisibility(View.INVISIBLE);
                tvSetujuPada.setVisibility(View.INVISIBLE);
            }
            else
            {
                tvStatus.setText(parts[23]);
            }

            tvSetujuOleh.setText(parts[24]);
            tvSetujuPada.setText(parts[25]);
        }
    }

    //    //untuk mendapatkan nominal diskon
//    private Double getNominalDiskon(){
//        Double discNominal = 0.0;
//        if (tvSubtotal.getText().toString().equals("")){
//            tvSubtotal.setText("0.0");
//        }else if (etDisc.getText().toString().equals("")){
//            etDisc.setText("0.0");
//        }else {
//            if (!tvSubtotal.getText().toString().equals("")) {
//                Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
//                Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
//                discNominal = disc * subtotal / 100;
//            }
//        }
//        return discNominal;
//    }
//
//    //untuk mendapatkan nominal ppn (setelah diskon)
//    private Double getNominalPPN(){
//        Double ppnNominal = 0.0;
//        if (tvSubtotal.getText().toString().equals("")){
//            tvSubtotal.setText("0.0");
//        }else if(etPPN.getText().toString().equals("")){
//            etPPN.setText("0.0");
//        }else if (etDisc.getText().toString().equals("")){
//            etDisc.setText("0.0");
//        }else {
//            Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
//            Double ppn = Double.parseDouble(etPPN.getText().toString().replace(",", ""));
//            Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
//            Double discNominal = disc * subtotal / 100;
//            ppnNominal = ppn * (subtotal - discNominal) / 100;
//        }
//        return ppnNominal;
//    }
    //untuk mendapatkan nominal subtotal dari item dan pekerjaan
    private Double getSubtotal(){
        String data = LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "");
        Double dblSubtotal = 0.0;
        Double dblItemSubtotal = 0.0;
        Double dblPekerjaanSubtotal = 0.0;
        Double dblFeeSubtotal = 0.0;
        Log.d("data", data);
        String[] pieces = data.trim().split("\\|");
        Log.d("pieces length", Integer.toString(pieces.length));
        if((pieces.length >= 1 && !pieces[0].equals(""))){
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("~");
                    String fee = parts[9];
                    String subtotal = parts[11];
                    if(fee.equals("")) fee = "0";
                    if(subtotal.equals("")) subtotal = "0";
                    dblFeeSubtotal = dblFeeSubtotal+ Double.parseDouble(fee);
                    dblItemSubtotal = dblItemSubtotal + Double.parseDouble(subtotal);
                    dblSubtotal = dblSubtotal + Double.parseDouble(subtotal);
                    Log.d("subtotal item [" + i + "]", subtotal);
                }
            }
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_subtotal, dblItemSubtotal.toString());
        }

        data = LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan, "");
        pieces = data.trim().split("\\|");
        if((pieces.length >= 1 && !pieces[0].equals(""))){
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("~");
                    String fee = parts[6];
                    String subtotal = parts[8];
                    if(fee.equals("")) fee = "0";
                    if(subtotal.equals("")) subtotal = "0";
                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, subtotal);
                    dblFeeSubtotal = dblFeeSubtotal+ Double.parseDouble(fee);
                    dblPekerjaanSubtotal = dblPekerjaanSubtotal + Double.parseDouble(subtotal);
                    dblSubtotal = dblSubtotal + Double.parseDouble(subtotal);
                }
            }
        }
        else
        {
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, "0");
        }
        LibInspira.setShared(global.temppreferences, global.temp.salesorder_subtotal_fee, dblFeeSubtotal.toString());
        return dblSubtotal;
    }

//    private Double getGrandTotal(){
//        Double grandtotal = getSubtotal() - getNominalDiskon() + getNominalPPN();
//        return grandtotal;
//    }


    //added by Tonny @02-Sep-2017
    //untuk menjalankan perintah send data ke web service
    private void sendData(){
        String actionUrl = "Order/insertNewOrderJual/";
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
                jsonObject.put("nomorcustomer", LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_nomor, ""));
                jsonObject.put("kodecustomer", LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_kode, ""));
                jsonObject.put("nomorbroker", LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nomor, ""));
                jsonObject.put("kodebroker", LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_kode, ""));
                jsonObject.put("nomorsales", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
                //jsonObject.put("kodesales", LibInspira.getShared(global.userpreferences, global.user.kode, ""));
                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.salesorder_subtotal, ""));
                jsonObject.put("subtotaljasa", LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, ""));
                jsonObject.put("subtotalbiaya", LibInspira.getShared(global.temppreferences, global.temp.salesorder_subtotal_fee, ""));
                jsonObject.put("disc", LibInspira.getShared(global.temppreferences, global.temp.salesorder_disc, "0"));
                jsonObject.put("discnominal", LibInspira.getShared(global.temppreferences, global.temp.salesorder_disc_nominal, "0"));
                jsonObject.put("dpp", LibInspira.getShared(global.temppreferences, global.temp.salesorder_total, ""));
                jsonObject.put("ppn", LibInspira.getShared(global.temppreferences, global.temp.salesorder_ppn, "0"));
                jsonObject.put("ppnnominal", LibInspira.getShared(global.temppreferences, global.temp.salesorder_ppn_nominal, "0"));
                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.salesorder_total, "0"));
                //jsonObject.put("totalrp", Double.toString(getGrandTotal() * Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_kurs, ""))));
                jsonObject.put("pembuat", LibInspira.getShared(global.userpreferences, global.user.nama, ""));
                jsonObject.put("nomorcabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                jsonObject.put("cabang", LibInspira.getShared(global.temppreferences, global.user.namacabang, ""));
                jsonObject.put("valuta", LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nama, ""));
                jsonObject.put("kurs", LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_kurs, ""));
                jsonObject.put("jenispenjualan", "Material");
                jsonObject.put("isbarangimport", LibInspira.getShared(global.temppreferences, global.temp.salesorder_import, ""));
                jsonObject.put("isppn", LibInspira.getShared(global.temppreferences, global.temp.salesorder_isPPN, ""));
                if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("proyek"))
                {
                    jsonObject.put("proyek", 1);
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("nonproyek"))
                {
                    jsonObject.put("proyek", 0);
                }
                jsonObject.put("user", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));


                //-------------------------------------------------------------------------------------------------------//
                //---------------------------------------------DETAIL----------------------------------------------------//
                jsonObject.put("dataitemdetail", LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, ""));  //mengirimkan data item
                jsonObject.put("datapekerjaandetail", LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan, ""));  //mengirimkan data pekerjaan
                Log.d("detailitemdetail", LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, ""));
                Log.d("detailpekerjaandetail", LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                            LibInspira.ShowShortToast(getContext(), "Data has been successfully added");
                            LibInspira.clearShared(global.temppreferences); //hapus cache jika data berhasil ditambahkan
                            LibInspira.BackFragmentCount(getFragmentManager(), 6);  //kembali ke menu depan sales order
                        }
                        else
                        {
                            LibInspira.ShowShortToast(getContext(), "Adding new data failed");
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.ShowShortToast(getContext(), "Adding new data failed");
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

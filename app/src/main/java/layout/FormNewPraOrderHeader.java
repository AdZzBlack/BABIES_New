/******************************************************************************
 Author           : ADI
 Description      : untuk mengisi header sales order
 History          :

 ******************************************************************************/
package layout;

import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.inspira.babies.GlobalVar;
import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.jsonObject;

//import android.app.Fragment;

public class FormNewPraOrderHeader extends Fragment implements View.OnClickListener{

    private TextView tvDate, tvCustomer, tvSales, tvNomorKode;
    EditText etKeterangan;
    private Button btnNext,btnSave;
    private DatePickerDialog dp;
//    private CheckBox chkBarangImport;
    private Spinner spJenisHarga;

    public FormNewPraOrderHeader() {
        // Required empty public constructor
    }

    public void setPrevData()
    {
        // untuk ngisi form klo mw di edit
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_praorder_form_header, container, false);
        getActivity().setTitle("Header PraOrder");
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
        tvNomorKode = (TextView) getView().findViewById(R.id.tvNomorKode);
        tvCustomer = (TextView) getView().findViewById(R.id.tvCustomer);
        tvSales = (TextView) getView().findViewById(R.id.tvSales);
        etKeterangan = (EditText) getView().findViewById(R.id.etKeterangan);
        tvDate = (TextView) getView().findViewById(R.id.tvDate); //added by Tonny @30-Aug-2017

        btnNext = (Button) getView().findViewById(R.id.btnNext);

        btnSave = (Button) getView().findViewById(R.id.btnSave);
        btnSave.setVisibility(View.GONE);
//        chkBarangImport = (CheckBox) getView().findViewById(R.id.chkBarangImport);
        spJenisHarga = (Spinner) getView().findViewById(R.id.spJenisHarga);
        new getJenisData().execute("Master/getJenisHarga/");
        setAdapterJenisHarga(LibInspira.getShared(
                global.datapreferences,
                global.data.jenis_harga, ""));

        spJenisHarga.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int nomorDB = position+1;// karena di DB mulai dari 1 bukan dari 0
                String nomor = ""+nomorDB;
                String nama = parentView.getItemAtPosition(position).toString(); //Log.d("spasd",nomor+nama);
                LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, nomor);
                LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nama,nama);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                //LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, "1");
            }

        });


        tvCustomer.setOnClickListener(this);
        //tvNomorKode.setOnClickListener(this);
        tvSales.setOnClickListener(this);
        //tvJenisHarga.setOnClickListener(this);
        tvDate.setOnClickListener(this);  //added by Tonny @30-Aug-2017
        btnNext.setOnClickListener(this);


        setupStart();
        // Declare DatePicker
        Calendar newCalendar = Calendar.getInstance();
        dp = new DatePickerDialog(getActivity(), R.style.dpTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String date = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date newdate = sdf.parse(date);
                    date = sdf.format(newdate);

                    tvDate.setText(date);
                    LibInspira.setShared(global.temppreferences, global.temp.praorder_date, tvDate.getText().toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        etKeterangan.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    LibInspira.setShared(global.temppreferences, global.temp.praorder_keterangan, etKeterangan.getText().toString());
                }
            });

//        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("proyek"))
//        {
//            getView().findViewById(R.id.trProyek).setVisibility(View.VISIBLE);
//            getView().findViewById(R.id.trJenis).setVisibility(View.GONE);
//            getView().findViewById(R.id.trImport).setVisibility(View.GONE);
//
//            LibInspira.setShared(global.temppreferences, global.temp.salesorder_jenis, "0");
//            LibInspira.setShared(global.temppreferences, global.temp.salesorder_import, "0");
//            LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan, "");
//        }
    }

    public void setupStart()
    {
        if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("add_new")){

            //LibInspira.getShared(global.userpreferences, global.user.cabang,"");// dapatkan nomormhcabang

            tvNomorKode.setText("generate kode");
            getView().findViewById(R.id.trNomorKode).setVisibility(View.GONE);

            etKeterangan.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_keterangan, ""));
            tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_customer_nama, "").toUpperCase());
            tvSales.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_sales_nama, "").toUpperCase());
            if(!LibInspira.getShared(global.datapreferences, global.data.jenis_harga, "").equals("")) {
                spJenisHarga.setSelection(((ArrayAdapter) spJenisHarga.getAdapter()).getPosition(0));
            }

            if (!LibInspira.getShared(global.temppreferences, global.temp.praorder_date, "").equals("")) {
                tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_date, ""));
            }
        }

        if(LibInspira.getShared(global.temppreferences, global.temp.praorder_menu, "").equals("edit"))
        {
            LibInspira.setShared(global.temppreferences, global.temp.praorder_submenu, "");

            getView().findViewById(R.id.trBtnSaveHeader).setVisibility(View.VISIBLE);
            btnSave.setOnClickListener(this);

            tvNomorKode.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_header_kode, ""));
            etKeterangan.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_keterangan, ""));
            tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_customer_nama, "").toUpperCase());
            tvSales.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_sales_nama, "").toUpperCase());

            //Integer.parseInt(LibInspira.getShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, ""));
            String tempJenis = LibInspira.getShared(global.temppreferences, global.temp.praorder_jenis_harga_nama, "");

            Log.d("newasd","as"+tempJenis);
            if(!tempJenis.equals("") && !LibInspira.getShared(global.datapreferences, global.data.jenis_harga, "").equals("")) {
                spJenisHarga.setSelection(((ArrayAdapter) spJenisHarga.getAdapter()).getPosition(tempJenis));
            }

            if (!LibInspira.getShared(global.temppreferences, global.temp.praorder_date, "").equals("")) {
                tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_date, ""));
            }

            //String data =  LibInspira.getShared(global.temppreferences, global.temp.praorder_header_edit,"");
            //trimDataShared(data);
        }
    }


//    private class HolderJenisHarga
//    {
//        public String nomor;
//        public String nama;
//
//        public String getNama() {
//            return nama;
//        }
//
//        public void setNama(String nama) {
//            this.nama = nama;
//        }
//
//        public String getNomor() {
//            return nomor;
//        }
//
//        public void setNomor(String nomor) {
//            this.nomor = nomor;
//        }
//    }

    public void setAdapterJenisHarga(String _strData)
    {
        //get dlu dari shared preferences
        List<String> jenisHargaList = new ArrayList<>();
        if (_strData.equals("")){
            return;
        }
        String data = _strData;
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
//                        data[1] = obj.getString("nama");


                        for(String k : parts)
                        {
                            if(k.equals("null")) k = "";
                        }

                        jenisHargaList.add(parts[1]);

                    }catch (Exception e){
                        e.printStackTrace();
                        LibInspira.ShowShortToast(getContext(), "The current data is invalid. Please add new data.");
                        setAdapterJenisHarga(LibInspira.getShared(
                                global.datapreferences,
                                global.data.jenis_harga, ""));
                    }
                }
            }
        }


        ArrayAdapter<String> JenisHargaAdapter = new ArrayAdapter<String>(getActivity(),R.layout.sp_text, jenisHargaList);
        spJenisHarga.setAdapter(JenisHargaAdapter);
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

        if(id==R.id.tvCustomer)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseCustomerFragment());
        }
        else if(id==R.id.tvDate) {
            dp.show();
        }
        else if(id == R.id.tvSales)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseSalesmanFragment());
        }
        else if(id == R.id.btnSave)
        {
            //  yang dilakukan kalau user edit dan save header
            LibInspira.alertBoxYesNo("Mengubah Data?", "Apakah anda yakin ingin mengubah data?", getActivity(), new Runnable() {
                public void run() {
                    //YES
                    editHeaderData();
                    //LibInspira.BackFragmentCount(getFragmentManager(), 3);
                }
            }, new Runnable() {
                public void run() {
                    //NO
                }
            });

        }
        else if(id==R.id.btnNext)
        {
            //##sementara di bypass dlu

            //LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewPraOrderItemList());

            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_customer_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.praorder_sales_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, "").equals("")
                    )
            {
                LibInspira.ShowShortToast(getContext(), "All Field Required");
            }
            else
            {
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewPraOrderItemList());
            }
        }
    }

    private class getJenisData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
//            try {
//                jsonObject = new JSONObject();
//                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.praorder_selected_list_nomor, ""));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            jsonObject = new JSONObject();
            return LibInspira.executePost(getActivity().getApplicationContext(), urls[0], jsonObject);
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
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            int sizeData = 2;
                            String[] data = new String[sizeData];
                            data[0] = obj.getString("nomor");
                            data[1] = obj.getString("nama");

                            for(int z = 0;z<sizeData;z++)
                            {
                                if(data[z].equals("null")) data[z] = "";
                            }
                            tempData = tempData + data[0] + "~" + data[1] + "|";
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.jenis_harga, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.jenis_harga,
                                tempData
                        );

                        setAdapterJenisHarga(LibInspira.getShared(
                                global.datapreferences,
                                global.data.jenis_harga, ""));
                    }
                }
                //LibInspira.hideLoading();

                //refreshList();
                //LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new PraOrderApprovalFragment());
                //tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                //LibInspira.hideLoading();
                //tvInformation.animate().translationYBy(-80);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //LibInspira.showLoading(getContext(), "Getting data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    private void editHeaderData()
    {
        String actionUrl = "Order/updatePraorderHeader/";
        new EditHeaderData().execute(actionUrl);
    }

    //class yang digunakan untuk insert data
    private class EditHeaderData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                // kalau beda berarti di edit
//                if(!LibInspira.getShared(global.temppreferences, global.temp.praorder_header_edit,"").equals(
//                        LibInspira.getShared(global.temppreferences, global.temp.praorder_summary,"") ))
//                {

                    jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.praorder_selected_list_nomor, ""));

                    jsonObject.put("tanggal", LibInspira.getShared(global.temppreferences, global.temp.praorder_date, ""));
                    jsonObject.put("nomorCustomer", LibInspira.getShared(global.temppreferences, global.temp.praorder_customer_nomor, ""));
                    jsonObject.put("nomorSales", LibInspira.getShared(global.temppreferences, global.temp.praorder_sales_nomor, ""));
                    jsonObject.put("nomorJenisHarga", LibInspira.getShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, ""));
                    jsonObject.put("keterangan", LibInspira.getShared(global.temppreferences, global.temp.praorder_keterangan, ""));

                    jsonObject.put("nomorCabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                    //jsonObject.put("namaCabang", LibInspira.getShared(global.userpreferences, global.user.kodecabang, ""));
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
                            LibInspira.ShowLongToast(getContext(), "Data has been successfully EDITED");
                            //setupStart();
                            //LibInspira.clearShared(global.temppreferences); //hapus cache jika data berhasil ditambahkan
                            LibInspira.BackFragmentCount(getFragmentManager(), 2);  //kembali ke menu depan sales order
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

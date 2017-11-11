package layout;

/**
 * Created by Arta on 09-Nov-17.
 */

/******************************************************************************
 Author           : ADI
 Description      : untuk mengisi header sales order
 History          :

 ******************************************************************************/

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

public class FormNewOrderJualHeader extends Fragment implements View.OnClickListener{

    private TextView tvNomorKode, tvDate, tvCustomer, tvPraorder;
    EditText etKurs;
    private Button btnNext,btnSave;
    private DatePickerDialog dp;
    private Spinner spJenisHarga, spValuta;

    public FormNewOrderJualHeader() {
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
        View v = inflater.inflate(R.layout.fragment_order_jual_form_header, container, false);
        getActivity().setTitle("Header OrderJual");
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
        tvDate = (TextView) getView().findViewById(R.id.tvDate);
        tvPraorder = (TextView) getView().findViewById(R.id.tvPraorder);
        etKurs = (EditText) getView().findViewById(R.id.etKurs);

        btnNext = (Button) getView().findViewById(R.id.btnNext);
        btnSave = (Button) getView().findViewById(R.id.btnSave);

        spValuta = (Spinner) getView().findViewById(R.id.spValuta);
        new getValutaData().execute("Master/getValuta/");
        setAdapterValuta(LibInspira.getShared(
                global.datapreferences,
                global.data.valuta, ""));


        spValuta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int nomorDB = position+1;// karena di DB mulai dari 1 bukan dari 0
                String nomor = ""+nomorDB;
                String nama = parentView.getItemAtPosition(position).toString(); //Log.d("spasd",nomor+nama);
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_valuta_nomor, nomor);
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_valuta_nama,nama);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                //LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, "1");
            }

        });


        tvCustomer.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvPraorder.setOnClickListener(this);

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
                    LibInspira.setShared(global.temppreferences, global.temp.orderjual_date, tvDate.getText().toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        etKurs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_kurs, etKurs.getText().toString());
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
        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("add_new")){

            //LibInspira.getShared(global.userpreferences, global.user.cabang,"");// dapatkan nomormhcabang
            tvNomorKode.setText("generate kode");
            getView().findViewById(R.id.trNomorKode).setVisibility(View.GONE);

            tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nama, "").toUpperCase());
            tvPraorder.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_kode, "").toUpperCase());
            if(!LibInspira.getShared(global.datapreferences, global.data.valuta, "").equals("")) {
                spValuta.setSelection(((ArrayAdapter) spValuta.getAdapter()).getPosition(0));
            }

            if (!LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, "").equals("")) {
                tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, ""));
            }
        }

        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_menu, "").equals("edit"))
        {
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_submenu, "");

            getView().findViewById(R.id.trBtnSaveHeader).setVisibility(View.VISIBLE);
            btnSave.setOnClickListener(this);

            tvNomorKode.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_header_kode, ""));
            tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nama, "").toUpperCase());
            tvPraorder.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_kode, "").toUpperCase());

            String tempValuta = LibInspira.getShared(global.temppreferences, global.temp.orderjual_valuta_nama, "");

            if(!tempValuta.equals("")) {
                spValuta.setSelection(((ArrayAdapter) spValuta.getAdapter()).getPosition(tempValuta));
            }

            if (!LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, "").equals("")) {
                tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.orderjual_date, ""));
            }
        }
    }


    public void setAdapterValuta(String _strData)
    {
        //get dlu dari shared preferences
        List<String> valutaList = new ArrayList<>();
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
//                        data[1] = obj.getString("kode"); IDR
//                        data[2] = obj.getString("simbol"); rp

                        for(String k : parts)
                        {
                            if(k.equals("null")) k = "";
                        }

                        //kode yang di pake
                        valutaList.add(parts[1]);

                    }catch (Exception e){
                        e.printStackTrace();
                        LibInspira.ShowShortToast(getContext(), "The current data is invalid. Please add new data.");
                        setAdapterValuta(LibInspira.getShared(
                                global.datapreferences,
                                global.data.valuta, ""));
                    }
                }
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.sp_text, valutaList);
        spValuta.setAdapter(adapter);
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

        if(id==R.id.tvCustomer)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseCustomerFragment());
        }
        else if(id==R.id.tvDate) {
            dp.show();
        }
        else if(id == R.id.tvPraorder)
        {
            //LibInspira.setShared(global.temppreferences, global.temp.orderjual_customer_nomor, "1667");
            if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nomor, "").equals(""))
            {
                //pindah ke fragment pilih pra order
                if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_current_praorder_nomor, "").equals(""))
                {
                     LibInspira.alertBoxYesNo("Mengganti praorder", "List item akan berubah sesuai dengan data dari praorder, apa anda yakin?", getActivity(), new Runnable() {
                        public void run() {
                            //YES
                            LibInspira.setShared(global.temppreferences, global.temp.orderjual_current_praorder_nomor, "");
                            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChoosePraorderFragment());
                        }
                    }, new Runnable() {
                        public void run() {
                            //NO
                        }
                    });
                }
                else
                {
                    // klo current kosong brarti brusan bikin baru
                    LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChoosePraorderFragment());
                }
            }
            else
            {
                LibInspira.ShowLongToast(getContext(),"Pilih Customer terlebih dahulu");
            }
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
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_kurs, etKurs.getText().toString());
            //##sementara di bypass dlu
            //LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewPraOrderItemList());
            if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.orderjual_valuta_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, "").equals("")
                    )
            {
                LibInspira.ShowShortToast(getContext(), "All Field Required");
            }
            else
            {
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualItemList());
            }
        }
    }

    private class getValutaData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
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
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            int sizeData = 3;
                            String[] data = new String[sizeData];
                            data[0] = obj.getString("nomor");
                            data[1] = obj.getString("kode");
                            data[2] = obj.getString("simbol");

                            for(int z = 0;z<sizeData;z++)
                            {
                                if(data[z].equals("null")) data[z] = "";
                            }
                            tempData = tempData + data[0] + "~" + data[1] + "~" + data[2] + "|";
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.valuta, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.valuta,
                                tempData
                        );

                        setAdapterValuta(LibInspira.getShared(
                                global.datapreferences,
                                global.data.valuta, ""));
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

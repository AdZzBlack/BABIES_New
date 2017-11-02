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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.inspira.babies.GlobalVar;
import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
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
    private Button btnNext;
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
        //tvJenisHarga = (TextView) getView().findViewById(R.id.tvJenisHarga);
        tvDate = (TextView) getView().findViewById(R.id.tvDate); //added by Tonny @30-Aug-2017

        btnNext = (Button) getView().findViewById(R.id.btnNext);
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
                String nama = parentView.getItemAtPosition(position).toString(); Log.d("spasd",nomor+nama);
                LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, nomor);
                LibInspira.setShared(global.temppreferences, global.temp.praorder_jenis_harga_nama,nama);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
//        spPerhitunganBarangCustom = (Spinner) getView().findViewById(R.id.spPerhitunganBarangCustom);


        tvCustomer.setOnClickListener(this);
        //tvNomorKode.setOnClickListener(this);
        tvSales.setOnClickListener(this);
        //tvJenisHarga.setOnClickListener(this);
        tvDate.setOnClickListener(this);  //added by Tonny @30-Aug-2017
        btnNext.setOnClickListener(this);
 //       chkBarangImport.setOnClickListener(this);  //added by Tonny @07-Sep-2017

//        tvValuta.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nama, "").toUpperCase());
//        tvBroker.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nama, "").toUpperCase());
//        tvProyek.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_proyek_nama, "").toUpperCase());

        //added by Tonny @08-Sep-2017 jika preferences import, maka centang chkBarangImport
//        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_import, "0").equals("1")){
//            chkBarangImport.setChecked(true);
//        }else{
//            chkBarangImport.setChecked(false);
//        }

        tvNomorKode.setText("generate kode");
        tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_customer_nama, "").toUpperCase());
        tvSales.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_sales_nama, "").toUpperCase());
        //tvJenisHarga.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_jenis_harga_nama, "").toUpperCase());

        if (!LibInspira.getShared(global.temppreferences, global.temp.praorder_date, "").equals("")){
            tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.praorder_date, ""));
        }
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


        ArrayAdapter<String> JenisHargaAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, jenisHargaList);
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
        else if(id==R.id.tvDate) {  //added by Tonny @30-Aug-2017
            dp.show();
        }
        else if(id == R.id.tvSales)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseSalesmanFragment());
        }
//        else if(id==R.id.chkBarangImport)
//        {
//            //jika user menekan button chkBarangImport, maka hapus cache / preferences dari salesorder item yang telah dibuat sebelumnya
//            LibInspira.setShared(global.temppreferences, global.temp.salesorder_item, "");
//        }
        else if(id==R.id.btnNext)
        {

            if(LibInspira.getShared(global.temppreferences, global.temp.praorder_customer_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.praorder_sales_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.praorder_jenis_harga_nomor, "").equals("")
                    )
            {
                LibInspira.ShowShortToast(getContext(), "All Field Required");
            }
            else
//            {
//                if (chkBarangImport.isChecked()){
//                    if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_import, "").equals("1"))
//                    {
//                        LibInspira.setShared(global.temppreferences, global.temp.salesorder_item, "");
//                        LibInspira.setShared(global.temppreferences, global.temp.salesorder_import, "1");
//                    }
//                }else{
//                    if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_import, "").equals("0"))
//                    {
//                        LibInspira.setShared(global.temppreferences, global.temp.salesorder_item, "");
//                        LibInspira.setShared(global.temppreferences, global.temp.salesorder_import, "0");
//                    }
//                }
//
//                if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_jenis, "").equals(spJenis.getSelectedItemPosition()))
//                {
//                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_item, "");
//                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_jenis, String.valueOf(spJenis.getSelectedItemPosition()));
//                }
//
//                LibInspira.setShared(global.temppreferences, global.temp.salesorder_perhitungan_barang_custom, String.valueOf(spPerhitunganBarangCustom.getSelectedItemPosition()));
//
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewPraOrderItemList());
//            }
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

//                    if(!tempData.equals(LibInspira.getShared(global.temppreferences, global.temp.salesorder_summary, "")))
//                    {
                    LibInspira.setShared(
                            global.datapreferences,
                            global.data.jenis_harga,
                            tempData
                    );
//                    }
                }
                //LibInspira.hideLoading();
                setAdapterJenisHarga(LibInspira.getShared(
                        global.datapreferences,
                        global.data.jenis_harga, ""));
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
}

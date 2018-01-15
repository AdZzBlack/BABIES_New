package layout;

/**
 * Created by Arta on 08-Nov-17.
 */

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
import android.widget.ListView;
import android.widget.RelativeLayout;
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

/******************************************************************************
 Author           :
 Description      : - untuk menampilkan order jual dalam bentuk list (yang ditampilkan data yang penting2 aja)
                    - untuk handle button add new order jual( lewat fab (floating action button) )
 History          :

 ******************************************************************************/

public class OrderJualListFragment extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    private Button btnBack, btnNext;
    protected String actionUrl;
    private CheckData checkData;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;
    private GetSummaryData getSummaryData;

    public OrderJualListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_choose, container, false);
        getActivity().setTitle("Order Jual List");
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

        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(this);

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_praorder_header, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        relativeLayout = (RelativeLayout) getView().findViewById(R.id.rlFooter);
        relativeLayout.setVisibility(View.GONE);

        refreshList();

        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") || LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval"))
        {
            fab.setVisibility(View.GONE);
        }
        else
        {
            fab.setVisibility(View.VISIBLE);
        }

        actionUrl = "Order/getOrderJualList/";
        checkData = new CheckData();
        checkData.execute( actionUrl );

        LibInspira.setShared(global.userpreferences,global.user.notification_go_to_fragment,"");
    }

    protected void onCancelRequest(){
        if(checkData != null) checkData.cancel(true);
        if(getSummaryData != null) getSummaryData.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCancelRequest();
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void resetShared()
    {
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_item_add, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_current_praorder_nomor, "");

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_praorder_nomor, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_praorder_kode, "");

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_customer_nomor, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_customer_nama, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_customer_kode, "");

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_valuta_nama, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_valuta_nomor, "");

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_date, "");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_kurs,"");

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_persen,"" );
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_nominal,"" );

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_persen,"");
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_nominal,"" );

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal,"" );
        LibInspira.setShared(global.temppreferences, global.temp.orderjual_total,"" );

        LibInspira.setShared(global.temppreferences, global.temp.orderjual_keterangan,"" );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.fab)
        {
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_menu,"add_new");
            //reset form
            resetShared(); // ditentukan setelah insert aja, data insert2 d hapus

            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualHeader());

        }
    }

    private class CheckData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
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
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            String nomor = (obj.getString("nomor"));
                            String kode = (obj.getString("kode"));
                            String tanggal = (obj.getString("tanggal"));
                            String kodecustomer = (obj.getString("kodeCustomer"));
                            String namacustomer = (obj.getString("namaCustomer"));
                            String keterangan = (obj.getString("keterangan"));
                            String status = (obj.getString("status_disetujui"));

                            if(nomor.equals("null")) nomor = "";
                            if(kode.equals("null")) kode = "";
                            if(tanggal.equals("null")) tanggal = "";
                            if(kodecustomer.equals("null")) kodecustomer = "";
                            if(namacustomer.equals("null")) namacustomer = "";
                            if(keterangan.equals("null")) keterangan = "";
                            if(status.equals("null")) status = "";

                            tempData = tempData + nomor + "~" + kode + "~" + tanggal + "~" + kodecustomer + "~" + namacustomer + "~" + keterangan + "~" + status +"|";
                        }
                    }

                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.orderJual_list_header, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.orderJual_list_header,
                                tempData
                        );
                        refreshList();
                    }
                }
                LibInspira.hideLoading();
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
            LibInspira.showLoading(getContext(), "Sync data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    private void refreshList()
    {
        itemadapter.clear();
        list.clear();

        String data = LibInspira.getShared(global.datapreferences, global.data.orderJual_list_header, "");
        String[] pieces = data.trim().split("\\|");
        if(pieces.length==1 && pieces[0].equals(""))
        {
            //do nothing
        }
        else
        {
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");
                    String nomor = parts[0];
                    String kode = parts[1];
                    String tanggal = parts[2];
                    String kodecustomer = parts[3];
                    String namacustomer = parts[4];
                    String keterangan = parts[5];
                    String status = parts[6];

                    ItemAdapter dataItem = new ItemAdapter();
                    dataItem.setNomor(nomor);  //added by Tonny @16-Sep-2017
                    dataItem.setKode(kode);
                    dataItem.setTanggal(tanggal);
                    dataItem.setKodeCustomer(kodecustomer);
                    dataItem.setNamaCustomer(namacustomer);
                    dataItem.setKeterangan(keterangan);
                    dataItem.setStatus(status);
                    list.add(dataItem);

                    itemadapter.add(dataItem);
                    itemadapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class ItemAdapter {

        private String nomor;  //added by Tonny @16-Sep-2017
        private String kode;
        private String tanggal;
        private String kodecustomer;
        private String namacustomer;
        private String keterangan;
        private String status;

        public ItemAdapter() {}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public String getKode() {return kode;}
        public void setKode(String _param) {this.kode = _param;}

        public String getTanggal() {return tanggal;}
        public void setTanggal(String _param) {this.tanggal = _param;}

        public String getKodeCustomer() {return kodecustomer;}
        public void setKodeCustomer(String _param) {this.kodecustomer = _param;}

        public String getNamaCustomer() {return namacustomer;}
        public void setNamaCustomer(String _param) {this.namacustomer = _param;}

        public void setKeterangan(String keterangan) {
            this.keterangan = keterangan;
        }

        public String getKeterangan() {
            return keterangan;
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
            TextView tvKode, tvTanggal,tvKodeCust, tvNamaCust, tvKeterangan, tvStatus;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Holder holder = null;

            if(row==null)
            {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            holder = new Holder();
            holder.adapterItem = items.get(position);

            holder.tvKode = (TextView)row.findViewById(R.id.tvKode);
            holder.tvTanggal = (TextView)row.findViewById(R.id.tvTanggal);
            holder.tvKodeCust = (TextView)row.findViewById(R.id.tvKodeCust);
            holder.tvNamaCust = (TextView)row.findViewById(R.id.tvNamaCustomer);
            holder.tvKeterangan = (TextView)row.findViewById(R.id.tvKeterangan);
            holder.tvStatus = (TextView)row.findViewById(R.id.tvStatus);

            row.setTag(holder);
            setupItem(holder);

            final Holder finalHolder = holder;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //LibInspira.ShowLongToast(context, "coba");

//                    resetShared();
//                    Log.d("SelectedPraOrder: ", finalHolder.adapterItem.getNomor()+" | "+finalHolder.adapterItem.getStatus());
                    LibInspira.setShared(global.temppreferences, global.temp.orderjual_selected_list_nomor, finalHolder.adapterItem.getNomor());
                    LibInspira.setShared(global.temppreferences, global.temp.orderjual_selected_list_status, finalHolder.adapterItem.getStatus());
                    actionUrl = "Order/getOrderJualDetilInfo/";
                    getSummaryData = new GetSummaryData();
                    getSummaryData.execute(actionUrl);
                }
            });

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvKode.setText(holder.adapterItem.getKode().toUpperCase());
            holder.tvTanggal.setText(holder.adapterItem.getTanggal().toUpperCase());
            holder.tvKodeCust.setText(holder.adapterItem.getKodeCustomer().toUpperCase());
            holder.tvNamaCust.setText(holder.adapterItem.getNamaCustomer().toUpperCase());
            holder.tvKeterangan.setText(holder.adapterItem.getKeterangan().toUpperCase());
            if(holder.adapterItem.getStatus().equals("1"))
            {
                holder.tvStatus.setText("APPROVE");
            }
            else if(holder.adapterItem.getStatus().equals("0"))
            {
                holder.tvStatus.setText("DISAPPROVE");
            }
            else
            {
                holder.tvStatus.setText(holder.adapterItem.getStatus());
            }

        }
    }

    private class GetSummaryData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_nomor, ""));
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
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            int size = 26;
                            String[] data = new String[size];
                            data[0] = obj.getString("nomor");
                            data[1] = obj.getString("kode");

                            data[2] = obj.getString("nomorCabang");
                            data[3] = obj.getString("namaCabang");
                            data[4] = obj.getString("kodeCabang");

                            data[5] = obj.getString("nomorCustomer");
                            data[6] = obj.getString("namaCustomer");
                            data[7] = obj.getString("kodeCustomer");

                            data[8] = obj.getString("nomorValuta");
                            data[9] = obj.getString("kodeValuta");
                            data[10] = obj.getString("simbolValuta");

                            data[11] = obj.getString("nomorPraorder");
                            data[12] = obj.getString("kodePraorder");

                            data[13] = obj.getString("tanggal");
                            data[14] = obj.getString("kurs");

                            data[15] = obj.getString("subtotal");

                            data[16] = obj.getString("diskonPersen");
                            data[17] = obj.getString("diskonNominal");
                            data[18] = obj.getString("ppnPersen");
                            data[19] = obj.getString("ppnNominal");

                            data[20] = obj.getString("total");
                            data[21] = obj.getString("totalrp");

                            data[22] = obj.getString("keterangan");
                            data[23] = obj.getString("status_disetujui");

                            data[24] = obj.getString("disetujui_oleh");
                            data[25] = obj.getString("disetujui_pada");

                            for(int z = 0;z<size;z++)
                            {
                                if(data[z].equals("null")) data[z] = "";
                            }
                            tempData = tempData + data[0] + "~" + data[1] + "~" + data[2] + "~" + data[3]
                                    + "~" + data[4] + "~" + data[5] + "~" + data[6]
                                    + "~" + data[7] + "~" + data[8] + "~" + data[9]
                                    + "~" + data[10] + "~" + data[11] + "~" + data[12]
                                    + "~" + data[13] + "~" + data[14] + "~" + data[15]
                                    + "~" + data[16] + "~" + data[17] + "~" + data[18]
                                    + "~" + data[19] + "~" + data[20] + "~" + data[21]
                                    + "~" + data[22] + "~" + data[23] + "~" + data[24]
                                    + "~" + data[25];
                        }
                    }

//                    if(!tempData.equals(LibInspira.getShared(global.temppreferences, global.temp.salesorder_summary, "")))
//                    {
                    LibInspira.setShared(
                            global.temppreferences,
                            global.temp.orderjual_summary,
                            tempData
                    );
//                    }
                }
                LibInspira.hideLoading();
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new OrderJualApprovalFragment());
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
            LibInspira.showLoading(getContext(), "Getting Detail Info data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }
}

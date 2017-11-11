package layout;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.jsonObject;

/**
 * Created by Arta on 09-nov-17.
 */

public class ChoosePraorderFragment extends Fragment implements View.OnClickListener{
    private EditText etSearch;
    private ImageButton ibtnSearch;

    private TextView tvInformation, tvNoData;
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    Context con;

    public ChoosePraorderFragment() {
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
        getActivity().setTitle("Praorder");
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
        list = new ArrayList<ItemAdapter>();

        ((RelativeLayout) getView().findViewById(R.id.rlSearch)).setVisibility(View.VISIBLE);
        tvInformation = (TextView) getView().findViewById(R.id.tvInformation);
        tvNoData = (TextView) getView().findViewById(R.id.tvNoData);
        etSearch = (EditText) getView().findViewById(R.id.etSearch);

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_praorder_header, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);


        etSearch.setHint("kode Praorder");
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        refreshList();

        String actionUrl = "Order/getPraorderHeaderbyCustomer/";
        new getData().execute( actionUrl );
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
            search();
        }
    }

    private void search()
    {
        itemadapter.clear();
        for(int ctr=0;ctr<list.size();ctr++)
        {
            if(etSearch.getText().equals(""))
            {
                itemadapter.add(list.get(ctr));
                itemadapter.notifyDataSetChanged();
            }
            else
            {
                if(LibInspira.contains(list.get(ctr).getKode(),etSearch.getText().toString() ))
                {
                    itemadapter.add(list.get(ctr));
                    itemadapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void refreshList()
    {
        itemadapter.clear();
        list.clear();

        String data = LibInspira.getShared(global.datapreferences, global.data.praorder_byCustomer, "");
        String[] pieces = data.trim().split("\\|");

       // Log.d("chpraor",pieces[0]);
        if(pieces.length == 1 && pieces[0].equals(""))
        {
            //Log.d("chpraor","query");
            tvNoData.setVisibility(View.VISIBLE);
        }
        else
        {
           // Log.d("chpraor","else");
            tvNoData.setVisibility(View.GONE);
            for(int i=0 ; i < pieces.length ; i++){
                Log.d("item", pieces[i] + "a");
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

    private class getData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomorCustomer", LibInspira.getShared(global.temppreferences, global.temp.orderjual_customer_nomor, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("praorasd","call");
            return LibInspira.executePost_local(con, urls[0], jsonObject);
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
//                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.praorder_byCustomer, "")))
//                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.praorder_byCustomer,
                                tempData
                        );
                        refreshList();
                    //}
                }
                tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                tvInformation.animate().translationYBy(-80);
                LibInspira.setShared(
                        global.datapreferences,
                        global.data.praorder_byCustomer,
                        ""
                );
                refreshList();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvInformation.setVisibility(View.VISIBLE);
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
            ItemListAdapter.Holder holder = null;

            if(row==null)
            {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            holder = new ItemListAdapter.Holder();
            holder.adapterItem = items.get(position);

            holder.tvKode = (TextView)row.findViewById(R.id.tvKode);
            holder.tvTanggal = (TextView)row.findViewById(R.id.tvTanggal);
            holder.tvKodeCust = (TextView)row.findViewById(R.id.tvKodeCust);
            holder.tvNamaCust = (TextView)row.findViewById(R.id.tvNamaCustomer);
            holder.tvKeterangan = (TextView)row.findViewById(R.id.tvKeterangan);
            holder.tvStatus = (TextView)row.findViewById(R.id.tvStatus);

            row.setTag(holder);
            setupItem(holder);

            final ItemListAdapter.Holder finalHolder = holder;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if(LibInspira.getShared(global.sharedpreferences, global.shared.position, "").equals("orderjual"))
                {
                    LibInspira.setShared(global.temppreferences, global.temp.orderjual_praorder_nomor, finalHolder.adapterItem.getNomor());
                    LibInspira.setShared(global.temppreferences, global.temp.orderjual_praorder_kode, finalHolder.adapterItem.getKode());
                    LibInspira.BackFragment(getActivity().getSupportFragmentManager());
                }
                }
            });

            return row;
        }

        private void setupItem(final ItemListAdapter.Holder holder) {
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
}

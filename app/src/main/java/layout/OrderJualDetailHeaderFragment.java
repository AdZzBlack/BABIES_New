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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btnEdit)
        {
            if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_status, "").equals("1"))
            {
                //btnEdit.setVisibility(View.GONE);
                LibInspira.ShowLongToast(getActivity(),"Tidak bisa diedit karena sudah di APPROVE");
            }
            else
            {
                //reset
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_item_add, "");
                //biar ga load lagi
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_current_praorder_nomor,
                        LibInspira.getShared(global.temppreferences, global.temp.orderjual_praorder_nomor, ""));

                //btnEdit.setVisibility(View.VISIBLE);
                LibInspira.setShared(global.temppreferences, global.temp.orderjual_menu, "edit");

                if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_summary, "").equals(""))
                {
//                    LibInspira.setShared(global.temppreferences, global.temp.praorder_header_edit,
//                            LibInspira.getShared(global.temppreferences, global.temp.praorder_summary, ""));
                    trimDataShared(LibInspira.getShared(global.temppreferences, global.temp.orderjual_summary, ""));
                }
                else{
                    LibInspira.ShowShortToast(getActivity(),"error load data header");
                }

                // di isi list dr item list
                if(!LibInspira.getShared(global.temppreferences, global.temp.orderjual_item, "").equals("")) {
                    LibInspira.setShared(global.temppreferences, global.temp.orderjual_item_add,
                            LibInspira.getShared(global.temppreferences, global.temp.orderjual_item, ""));
                }
                else{
                    LibInspira.ShowShortToast(getActivity(),"error load data list items");
                }

                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormNewOrderJualHeader());
            }
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

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_header_nomor, parts[0]);
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_header_kode, parts[1]);

                                        //2,3,4 nomor,nama,kode cabang

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_customer_nomor, parts[5]);
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_customer_nama, parts[6]);
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_customer_kode, parts[7]);

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_valuta_nomor, parts[8]);
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_valuta_nama, parts[9]); //kode

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_praorder_kode, parts[12]);
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_praorder_nomor, parts[11]);

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_date, parts[13].substring(0,10));
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_kurs,LibInspira.delimeter(parts[14],true) );

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_persen,parts[16] );
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_diskon_nominal,parts[17] );

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_persen,parts[18] );
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_ppn_nominal,parts[19] );

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_subtotal,parts[15] );
            LibInspira.setShared(global.temppreferences, global.temp.orderjual_total,parts[20] );

            LibInspira.setShared(global.temppreferences, global.temp.orderjual_keterangan,parts[22] );
        }
    }
}

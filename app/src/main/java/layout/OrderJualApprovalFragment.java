/******************************************************************************
 Author           :
 Description      : untuk menampilkan menu approval Order jual, yaitu detail dari header,
                    dan list item dari nomor header tesebut
 History          :

 ******************************************************************************/
package layout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inspira.babies.IndexExternal.global;
import static com.inspira.babies.IndexExternal.jsonObject;

public class OrderJualApprovalFragment extends Fragment implements View.OnClickListener{
    private Button btnApprove, btnDisapprove;
    private SetApprovalOrderjual setApproval;
    //private boolean isApproving;
    int flagApproval; // 0 diubah dari approve ke diapprove || // 1 diubah dari disapprove ke approve
    private final String TITLE_CHANGE_STATUS ="Change Status";
    private final String MSG_CHANGE_STATUS_APPROVE ="Apakah ingin menggati status menjadi APPROVE ?";
    private final String MSG_CHANGE_STATUS_DISAPPROVE ="Apakah ingin menggati status menjadi DISAPPROVE ?";

    //## KURANG GANTI DI FILE PHPNYA KLO APPROVE DISAPPROVE NGAPAIN

    public OrderJualApprovalFragment() {
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
        View v = inflater.inflate(R.layout.fragment_order_jual_tab_approval, container, false);
        getActivity().setTitle("Approval Orderjual");
        return v;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OrderJualDetailHeaderFragment tab0 = new OrderJualDetailHeaderFragment();
                    return tab0;
                case 1:
                    OrderJualItemListFragment tab1 = new OrderJualItemListFragment();
                    //tab1.jenisDetail = "item";
                    return tab1;
//                case 2:
//                    FormSalesOrderDetailJasaListFragment tab2 = new FormSalesOrderDetailJasaListFragment();
//                    tab2.jenisDetail = "jasa";
//                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
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
    public void onActivityCreated(final Bundle bundle){
        super.onActivityCreated(bundle);

        Log.d("appvasd","activ create");

        btnApprove = (Button) getView().findViewById(R.id.btnApprove);
        btnDisapprove = (Button) getView().findViewById(R.id.btnDisapprove);

        if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_status, "").equals("0")){  //jika approve, hide dan remove listener pada btnDisapprove
            btnApprove.setVisibility(View.VISIBLE);
            btnApprove.setOnClickListener(this);
            btnDisapprove.setVisibility(View.GONE);
            btnDisapprove.setOnClickListener(null);
        }else if(LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_status, "").equals("1")){  //jika approve, hide dan remove listener pada btnApprove
            btnApprove.setVisibility(View.GONE);
            btnApprove.setOnClickListener(null);
            btnDisapprove.setVisibility(View.VISIBLE);
            btnDisapprove.setOnClickListener(this);
        }

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
//                    case 0:
//                        SummarySalesOrderFragment summary = new SummarySalesOrderFragment();
//                        summary.onActivityCreated(null);
                    case 1:
                        //refresh fragemnt biar load data
                        //jalankan fungsi aja klo ga bs recreate
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Detail Header");
        tabLayout.getTabAt(1).setText("Item");

        tabLayout.setScrollPosition(0,0f,true);
        viewPager.setCurrentItem(0);
        //tabLayout.getTabAt(2).setText("Pekerjaan");
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnApprove){
            LibInspira.alertBoxYesNo(TITLE_CHANGE_STATUS, MSG_CHANGE_STATUS_APPROVE, getActivity(), new Runnable() {
                public void run() {
                    //YES
                    //BELUM DIEDIT
                    flagApproval = 1;
                    String actionUrl = "Order/setApproveOrderjual/";
                    setApproval = new SetApprovalOrderjual();
                    setApproval.execute(actionUrl);
                }
            }, new Runnable() {
                public void run() {
                    //NO
                }
            });
        }else if(id == R.id.btnDisapprove){
            LibInspira.alertBoxYesNo(TITLE_CHANGE_STATUS, MSG_CHANGE_STATUS_DISAPPROVE, getActivity(), new Runnable() {
                public void run() {
                    //YES
                    flagApproval = 0;
                    String actionUrl = "Order/setDisapproveOrderjual/";
                    setApproval = new SetApprovalOrderjual();
                    setApproval.execute(actionUrl);
                }
            }, new Runnable() {
                public void run() {
                    //NO
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(setApproval != null) setApproval.cancel(true);
    }

    private class SetApprovalOrderjual extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put("nomorHeader", LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_nomor, ""));
                jsonObject.put("nomorAdmin", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                Log.d("appvasd",LibInspira.getShared(global.temppreferences, global.temp.orderjual_selected_list_nomor, "")+" "+
                        LibInspira.getShared(global.userpreferences, global.user.nomor, "")
                );
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
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("error") && !obj.has("query"))
                        {
                            LibInspira.hideLoading();
                            if(flagApproval == 1) {
                                LibInspira.ShowShortToast(getContext(), "Data Change into APPROVE");
                                LibInspira.setShared(global.temppreferences, global.temp.orderjual_selected_list_status, "1");
                                LibInspira.BackFragment(getFragmentManager());
//                                btnApprove.setVisibility(View.VISIBLE);
//                                btnApprove.setOnClickListener(PraOrderApprovalFragment.this);
//                                btnDisapprove.setVisibility(View.GONE);
//                                btnDisapprove.setOnClickListener(null);
                            }
                            else
                            {
                                LibInspira.ShowShortToast(getContext(), "Data Change into DISAPPROVE");
                                LibInspira.setShared(global.temppreferences, global.temp.orderjual_selected_list_status, "0");
                                LibInspira.BackFragment(getFragmentManager());
//                                btnApprove.setVisibility(View.GONE);
//                                btnApprove.setOnClickListener(null);
//                                btnDisapprove.setVisibility(View.VISIBLE);
//                                btnDisapprove.setOnClickListener(PraOrderApprovalFragment.this);
                            }
                        }else{
                            LibInspira.hideLoading();
                            LibInspira.alertbox("Change Status data", obj.getString("error"), getActivity(), new Runnable(){
                                public void run() {
                                    LibInspira.BackFragment(getFragmentManager());
                                }
                            }, null);
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.hideLoading();
                LibInspira.alertbox("Change Status data", e.getMessage(), getActivity(), new Runnable(){
                    public void run() {
                        LibInspira.BackFragment(getFragmentManager());
                    }
                }, null);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "Change data", "Loading...");
        }
    }
}

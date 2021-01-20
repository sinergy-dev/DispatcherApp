package com.example.appdispatcher.ui.detail2;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appdispatcher.Adapter.JobAcceptedAdapter;
import com.example.appdispatcher.Adapter.JobDetailAdapter;
import com.example.appdispatcher.FabActivity;
import com.example.appdispatcher.R;
import com.example.appdispatcher.ui.detail.AcceptedViewModel;
import com.example.appdispatcher.ui.detail.ScrollingActivityDetailTask;
import com.example.appdispatcher.util.server;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detail2Fragment extends Fragment{

    public static final String ID_JOB = "id_job";
    public static final String GET_ID_JOB = "get_id_job";
    public List<Detail2ViewModel> dList = new ArrayList<>();
    JobDetailAdapter dAdapter;
    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;
    RelativeLayout rvAccepted, rvNotFound;
    BottomNavigationView navigation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail2_fragment, container, false);

        RecyclerView recyclerViewPendingJobList = view.findViewById(R.id.recyclerViewPending);
        LinearLayoutManager layoutManagerPendingJobList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewPendingJobList.setLayoutManager(layoutManagerPendingJobList);
        dList.clear();
        fillDatJobPendingList();
        dAdapter = new JobDetailAdapter(this, dList);
        recyclerViewPendingJobList.setAdapter(dAdapter);
        navigation = getActivity().findViewById(R.id.nav_view);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        nestedScrollView = view.findViewById(R.id.nested_accept);
        rvNotFound = view.findViewById(R.id.RvNotFound);

        rvAccepted = view.findViewById(R.id.relativeLayoutAccepted);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshAccepted);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);

                dList.clear();
                nestedScrollView.setVisibility(View.GONE);
                shimmerFrameLayout.startShimmerAnimation();
                fillDatJobPendingList();

            }
        });
        
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item3 = menu.findItem(R.id.item_filter);
        MenuItem item2 = menu.findItem(R.id.item_search);
        MenuItem item4 = menu.findItem(R.id.item_filter_date);
        item4.setVisible(false);
        item3.setVisible(false);
        item2.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_notif) {
            Intent intent = new Intent(getContext(), FabActivity.class);
            intent.putExtra(GET_ID_JOB, "notif");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillDatJobPendingList() {
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, server.getJob_withToken , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jObj = response;
                nestedScrollView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                try {
                    JSONArray jray = jObj.getJSONArray("job");
                    if (response.length() > 0) {
                        if (dList != null) {
                            dList.clear();
                        } else {
                            dList = new ArrayList<>();
                        }
                        for (int i = 0; i < jray.length(); i++) {
                            JSONObject cat = jray.getJSONObject(i);
                            Detail2ViewModel itemCategory = new Detail2ViewModel();
//                            cat.getJSONObject("working_engineer").getString("id_engineer");

//                            if (cat.getString("job_status").equals("Ready") && cat.getJSONObject("working_engineer").getString("id_engineer").equals(jObj.getString("id_engineer"))) {

                            itemCategory.setCategory(cat.getJSONObject("category").getString("category_name"));
                            itemCategory.setJudul(cat.getString("job_name"));
                            itemCategory.setId_job(cat.getString("id"));
                            itemCategory.setFoto(cat.getJSONObject("category").getString("category_image_url"));
                            itemCategory.setCustomer(cat.getJSONObject("customer").getString("customer_name"));
                            itemCategory.setLocation(cat.getJSONObject("location").getString("long_location"));
                            itemCategory.setStatus(cat.getString("job_status"));

                            dList.add(itemCategory);
//                            }


                        }

                        if (dList.size() > 0) {
                            Log.i("tes leng plist", String.valueOf(dList.size()));
                            rvNotFound.setVisibility(View.GONE);
                            rvAccepted.setBackgroundColor(getResources().getColor(R.color.colorBackgroundTwo));
                        } else {
                            rvNotFound.setVisibility(View.VISIBLE);
                        }

                        dAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Accept", "applicaion/json");
                // Barer di bawah ini akan di simpan local masing-masing device engineer

//                headers.put("Authorization", "Bearer 14a1105cf64a44f47dd6d53f6b3beb79b65c1e929a6ee94a5c7ad30528d02c3e");
                SharedPreferences mSetting = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
                headers.put("Authorization", mSetting.getString("Token", "missing"));
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(strReq);
    }
}
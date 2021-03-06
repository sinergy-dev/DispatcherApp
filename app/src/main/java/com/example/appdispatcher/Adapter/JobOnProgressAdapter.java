package com.example.appdispatcher.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdispatcher.R;
import com.example.appdispatcher.ui.detail.OnProgressFragment;
import com.example.appdispatcher.ui.detail.OnProgressViewModel;

import java.util.List;

public class JobOnProgressAdapter extends RecyclerView.Adapter<JobOnProgressAdapter.ViewHolder> {

    List<OnProgressViewModel> projList;
    ProJListAdapter ProJListAdapter;
    private OnProgressFragment context;

    public JobOnProgressAdapter(OnProgressFragment context, List<OnProgressViewModel> projList) {
        super();
        this.projList = projList;
        this.context = context;
        ProJListAdapter = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_job_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnProgressViewModel progressjoblist = projList.get(position);
        holder.tvJudul.setText(progressjoblist.judul);
        holder.tvLocation.setText(progressjoblist.location);
        holder.tvcustomer.setText(progressjoblist.customer);
        holder.tvId_job.setText(progressjoblist.id_job);
        holder.tvCategory.setText(progressjoblist.category);
        Glide.with(context).load(progressjoblist.getFoto()).into(holder.ivFoto);
    }

    @Override
    public int getItemCount() {
        if (projList != null)
            return projList.size();
        return 0;
    }

    public OnProgressViewModel getItem(int pos) {
        return projList.get(pos);
    }

    public interface ProJListAdapter {
        void doClick(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvJudul, tvLocation, tvcustomer, tvId_job, tvCategory;
        RelativeLayout headsub;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.imageViewlist);
            tvJudul = itemView.findViewById(R.id.textViewJudul);
            tvLocation = itemView.findViewById(R.id.textViewLocation);
            tvcustomer = itemView.findViewById(R.id.textViewCustomer);
            tvId_job = itemView.findViewById(R.id.TvIdJob);
            headsub = itemView.findViewById(R.id.head_sub);
            tvCategory = itemView.findViewById(R.id.textViewCategory);

            headsub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProJListAdapter.doClick(getAdapterPosition());
                }
            });
        }
    }
}

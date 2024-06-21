package com.surbhi.surbhivarday;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.surbhi.surbhivarday.databinding.AddNetworkDialogBinding;
import com.surbhi.surbhivarday.databinding.NetworkListAdapterItemBinding;

import io.realm.Realm;
import io.realm.RealmResults;

public class NetworkListAdapter extends RecyclerView.Adapter<NetworkListAdapter.MyViewHolder> {
    private LayoutInflater mInflater;
    private Realm mRealm;
    private RealmResults<DataModel> mResults;
    private OnDataSetChangedListener onDataSetChangedListener;

    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

    public NetworkListAdapter(Context context, Realm realm, RealmResults<DataModel> results, OnDataSetChangedListener onDataSetChangedListener) {
        mRealm = realm;
        mInflater = LayoutInflater.from(context);
        setResults(results);
        this.onDataSetChangedListener = onDataSetChangedListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.network_list_adapter_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (!mResults.isEmpty()) {
            holder.binding.tvAlias.setText(mResults.get(position).getAlias());
            holder.binding.tvName.setText(mResults.get(position).getName());
            holder.binding.tvPrivacyUrl.setText(mResults.get(position).getPrivacy_url());
        }

        holder.binding.btnEdit.setOnClickListener(v -> {
            if (mResults.get(position) != null)
                showEditDialog(mResults.get(position), position);
        });

        holder.binding.btnDelete.setOnClickListener(v -> {
            showDeleteDialog(mResults, position);
        });

    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void setResults(RealmResults<DataModel> results) {
        mResults = results;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        NetworkListAdapterItemBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NetworkListAdapterItemBinding.bind(itemView);
        }
    }

    private void showEditDialog(DataModel dataModel, int position) {
        AddNetworkDialogBinding binding;
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        View view = LayoutInflater.from(mInflater.getContext()).inflate(R.layout.add_network_dialog, null);
        binding = AddNetworkDialogBinding.bind(view);
        builder.setView(binding.getRoot());

        binding.tvTitle.setText(mInflater.getContext().getString(R.string.edit_ad_network));
        binding.etAlias.setText(dataModel.getAlias());
        binding.etName.setText(dataModel.getName());
        binding.etPrivacyUrl.setText(dataModel.getPrivacy_url());

        AlertDialog dialog = builder.create();

        binding.btnSave.setOnClickListener(v -> {
            String alias = binding.etAlias.getText().toString().trim();
            String name = binding.etName.getText().toString().trim();
            String privacyUrl = binding.etPrivacyUrl.getText().toString().trim();

            if (alias.isEmpty()) {
                binding.etAlias.setError(mInflater.getContext().getString(R.string.empty_alias));
                return;
            }
            if (name.isEmpty()) {
                binding.etName.setError(mInflater.getContext().getString(R.string.empty_name));
                return;
            }
            if (privacyUrl.isEmpty()) {
                binding.etPrivacyUrl.setError(mInflater.getContext().getString(R.string.empty_privacy_url));
                return;
            }

            mRealm.executeTransaction(r -> {
                dataModel.setAlias(alias);
                dataModel.setName(name);
                dataModel.setPrivacy_url(privacyUrl);
            });
            dialog.dismiss();
            notifyItemChanged(position);
        });
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeleteDialog(RealmResults<DataModel> mResults, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setTitle(R.string.delete).setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, ((dialog, which) -> {
                    mRealm.executeTransaction(r -> mResults.get(position).deleteFromRealm());
                    notifyDataSetChanged();
                    if (mResults.size() == 0 && onDataSetChangedListener != null) {
                        onDataSetChangedListener.onDataSetChanged();
                    }
                })).setNegativeButton(R.string.no, ((dialog, which) -> {
                    dialog.dismiss();
                }));
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

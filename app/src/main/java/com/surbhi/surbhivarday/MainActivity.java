package com.surbhi.surbhivarday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.google.android.material.snackbar.Snackbar;
import com.surbhi.surbhivarday.databinding.ActivityMainBinding;
import com.surbhi.surbhivarday.databinding.AddNetworkDialogBinding;
import com.surbhi.surbhivarday.databinding.NetworkListAdapterBinding;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Realm realm;
    final DataModel dataModel = new DataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realm -> {
        });
        binding.btnAdd.setOnClickListener(v -> {
            showAddDialog();
        });

        binding.btnView.setOnClickListener(v -> {
            showData();
        });

    }

    private void showAddDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        AddNetworkDialogBinding dialogBinding = AddNetworkDialogBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnSave.setOnClickListener(v -> {
            if (Objects.requireNonNull(dialogBinding.etAlias.getText()).toString().trim().isEmpty()) {
                dialogBinding.etAlias.setError(getResources().getString(R.string.empty_alias));
            } else if (Objects.requireNonNull(dialogBinding.etName.getText()).toString().trim().isEmpty()) {
                dialogBinding.etName.setError(getResources().getString(R.string.empty_name));
            } else if (Objects.requireNonNull(dialogBinding.etPrivacyUrl.getText()).toString().trim().isEmpty()) {
                dialogBinding.etPrivacyUrl.setError(getResources().getString(R.string.empty_privacy_url));
            } else {
                dialog.dismiss();

                long current_id = System.currentTimeMillis() / 1000;

                dataModel.setId(current_id);
                dataModel.setAlias(dialogBinding.etAlias.getText().toString());
                dataModel.setName(dialogBinding.etName.getText().toString());
                dataModel.setPrivacy_url(dialogBinding.etPrivacyUrl.getText().toString());

                realm.executeTransactionAsync(realmT -> {
                    realmT.insertOrUpdate(dataModel);
                }, () -> {
                    Snackbar snackbar = Snackbar.make(binding.getRoot(), getResources().getString(R.string.ad_network_added_successfully), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }, error -> {
                    Snackbar snackbar = Snackbar.make(binding.getRoot(), getResources().getString(R.string.add_ad_network_failed), Snackbar.LENGTH_LONG);
                    snackbar.show();
                });

            }

        });


        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    private void showData() {
        RealmResults<DataModel> dataModelList = realm.where(DataModel.class).findAll();
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        NetworkListAdapterBinding adapterBinding = NetworkListAdapterBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(adapterBinding.getRoot());

        adapterBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (dataModelList.isEmpty()){
            adapterBinding.tvNoDataFound.setVisibility(View.VISIBLE);
            adapterBinding.recyclerView.setVisibility(View.GONE);
        }else {
            adapterBinding.tvNoDataFound.setVisibility(View.GONE);
            adapterBinding.recyclerView.setVisibility(View.VISIBLE);
            NetworkListAdapter adapter = new NetworkListAdapter(MainActivity.this, realm, dataModelList, () -> {
                adapterBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                adapterBinding.recyclerView.setVisibility(View.GONE);
            });
            adapterBinding.recyclerView.setAdapter(adapter);
        }


        dialog.show();
    }

}
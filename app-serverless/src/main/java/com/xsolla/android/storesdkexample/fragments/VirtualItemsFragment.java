package com.xsolla.android.storesdkexample.fragments;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.payments.XPayments;
import com.xsolla.android.serverlessexample.BuildConfig;
import com.xsolla.android.serverlessexample.R;
import com.xsolla.android.storesdkexample.adapter.VirtualItemsAdapter;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.fragments.base.CatalogFragment;
import com.xsolla.android.storesdkexample.listener.CreatePaystationIntentListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VirtualItemsFragment extends CatalogFragment {

    private static final int RC_PAYSTATION = 1;

    private VirtualItemsAdapter virtualItemsAdapter;
    private RecyclerView recyclerView;

    @Override
    public int getLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    public void initUI() {
        recyclerView = rootView.findViewById(R.id.items_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        setupToolbar("Virtual Items");
        getItems();
    }


    private void getItems() {
        Store.getVirtualItems(new Store.VirtualItemsCallback() {
            @Override
            public void onSuccess(@NotNull List<Store.VirtualItem> virtualItems) {
                virtualItemsAdapter = new VirtualItemsAdapter(
                        getContext(),
                        virtualItems,
                        new CreatePaystationIntentListener() {

                            @Override
                            public void onIntentCreated(Intent intent, String externalId) {
                                startActivityForResult(intent, RC_PAYSTATION);
                                XPayments.checkTransactionStatus(getContext(), BuildConfig.PROJECT_ID, externalId);
                            }

                            @Override
                            public void onFailure(String message) {
                                showSnack(message);
                            }
                        });
                recyclerView.setAdapter(virtualItemsAdapter);
            }

            @Override
            public void onFailure(@NotNull String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PAYSTATION) {
            XPayments.Result result = XPayments.Result.fromResultIntent(data);
        }
    }
}

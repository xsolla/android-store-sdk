package com.xsolla.android.storesdkexample.fragments.base;

import androidx.appcompat.widget.Toolbar;

import com.xsolla.android.storesdkexample.R;

public abstract class CatalogFragment extends BaseFragment {

    protected Toolbar toolbar;

    protected void setupToolbar(String title) {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> popFragment());
    }

}

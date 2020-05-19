package com.xsolla.android.storesdkexample.fragments;

import android.widget.Button;
import android.widget.TextView;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.api.XLoginCallback;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.util.ViewUtils;

public class RegisterFragment extends BaseFragment {

    private TextView usernameInput;
    private TextView emailInput;
    private TextView passwordInput;
    private Button registerButton;

    @Override
    public int getLayout() {
        return R.layout.fragment_register;
    }

    @Override
    public void initUI() {
        usernameInput = rootView.findViewById(R.id.username_input);
        emailInput = rootView.findViewById(R.id.email_input);
        passwordInput = rootView.findViewById(R.id.password_input);
        registerButton = rootView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            ViewUtils.disable(registerButton);
            registerUser();
        });
    }

    private void registerUser() {
        hideKeyboard();
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        XLogin.register(username, email, password, new XLoginCallback<Void>() {
            @Override
            protected void onSuccess(Void response) {
                showSnack("Registration success. Please check your email");
                openFragment(new AuthFragment());
                ViewUtils.enable(registerButton);
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
                ViewUtils.enable(registerButton);
            }
        });
    }

}
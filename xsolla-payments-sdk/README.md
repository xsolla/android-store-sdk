# Xsolla Payments Android SDK

**Xsolla Payments Android SDK** allows partners to monetize their product by providing users with a convenient UI to pay for in-game purchases in the game store Create a  [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) with Xsolla to get started.

## Install
The library is available in JCenter. To start using it, add the following line to the dependencies section of your `build.gradle` file:

```groovy
implementation 'com.xsolla.android:payments:0.13.0'
```

# Usage

## Configure Return URL
Add the following strings to project's strings resources file to specify Return URL configured in Publisher Account 
```xml
<string name="xsolla_payments_redirect_scheme">https</string>
<string name="xsolla_payments_redirect_host">example.com</string>
<string name="xsolla_payments_redirect_path_prefix">/payment</string>
```
There you should set Return URL split into 3 parts. (The example is for `https://example.com/payment`)

## Create Pay Station intent

```java
Intent intent = XPayments.createIntentBuilder(getContext())
            .accessToken(new AccessToken(token))
            .isSandbox(BuildConfig.IS_SANDBOX)
            .build();
```

## Start activity using created intent

```java
startActivityForResult(intent, RC_PAYSTATION);
```

## Parse activity result

```java
@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_PAYSTATION) {
        XPayments.Result result = XPayments.Result.fromResultIntent(data);
    }
}
```

See example in [DetailFragment](https://github.com/xsolla/store-android-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/DetailFragment.java) from Sample App.
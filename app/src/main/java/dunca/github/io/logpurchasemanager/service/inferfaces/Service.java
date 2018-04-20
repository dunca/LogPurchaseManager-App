package dunca.github.io.logpurchasemanager.service.inferfaces;

import android.app.ProgressDialog;
import android.content.Context;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.service.Callback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public abstract class Service {
    private static final String SERVER_URL = "http://10.24.145.100";
    protected static final Retrofit RETROFIT;

    static {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create());
        RETROFIT = retrofitBuilder.baseUrl(SERVER_URL).build();
    }

    protected <T> void sendRequest(Call<T> callObject, Callback<T> callback) {
        Context context = callback.getContext();

        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);

        progressDialog.setTitle(context.getString(R.string.service_call_dialog_title));
        progressDialog.setMessage(context.getString(R.string.service_call_dialog_content));

        progressDialog.show();

        callObject.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                callback.onResponse(response);
                cancelProgressDialog();
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailure(t);
                cancelProgressDialog();
            }

            private void cancelProgressDialog() {
                progressDialog.cancel();
            }
        });
    }
}

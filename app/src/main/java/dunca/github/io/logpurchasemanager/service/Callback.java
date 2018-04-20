package dunca.github.io.logpurchasemanager.service;

import android.content.Context;

import retrofit2.Response;

public abstract class Callback<T> {
    private Context mContext;

    public Callback(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public abstract void onResponse(Response<T> response);

    public abstract void onFailure(Throwable throwable);
}

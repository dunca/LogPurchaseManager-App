package dunca.github.io.logpurchasemanager.service.inferfaces;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public abstract class Service {
    private static final String SERVER_URL = "http://10.24.145.98";
    protected static final Retrofit RETROFIT;

    static {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create());
        RETROFIT = retrofitBuilder.baseUrl(SERVER_URL).build();
    }
}

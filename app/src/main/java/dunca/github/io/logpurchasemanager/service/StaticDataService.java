package dunca.github.io.logpurchasemanager.service;

import dunca.github.io.logpurchasemanager.service.inferfaces.Service;
import io.github.dunca.logpurchasemanager.shared.model.custom.StaticData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;

public final class StaticDataService extends Service {
    private static StaticDataService instance;
    private static StaticDataServiceImpl service = RETROFIT.create(StaticDataServiceImpl.class);

    private StaticDataService() {

    }

    public void getStaticData(Callback<StaticData> callback) {
        service.getStaticData().enqueue(callback);
    }

    private synchronized static StaticDataService getInstance() {
        if (instance == null) {
            instance = new StaticDataService();
        }

        return instance;
    }

    interface StaticDataServiceImpl {
        @GET("staticdata")
        Call<StaticData> getStaticData();
    }
}

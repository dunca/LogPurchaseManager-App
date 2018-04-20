package dunca.github.io.logpurchasemanager.service;

import java.util.List;

import dunca.github.io.logpurchasemanager.service.inferfaces.Service;
import io.github.dunca.logpurchasemanager.shared.model.custom.FullAcquisition;
import io.github.dunca.logpurchasemanager.shared.model.custom.FullAggregation;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class AcquisitionService extends Service {
    private AcquisitionServiceImpl mService = RETROFIT.create(AcquisitionServiceImpl.class);
    private static AcquisitionService sInstance;

    private AcquisitionService() {

    }

    public synchronized static AcquisitionService getInstance() {
        if (sInstance == null) {
            sInstance = new AcquisitionService();
        }

        return sInstance;
    }

    public void postFullAcquisitionList(Callback<List<FullAcquisition>> callback,
                                        List<FullAcquisition> fullAcquisitionList) {
        sendRequest(mService.postFullAcquisitionList(fullAcquisitionList), callback);
    }

    public void postAggregation(Callback<FullAggregation> callback, FullAggregation aggregation) {
        sendRequest(mService.patchAggregation(aggregation), callback);
    }

    interface AcquisitionServiceImpl {
        @POST("fullacquisition")
        Call<List<FullAcquisition>> postFullAcquisitionList(@Body List<FullAcquisition> fullAcquisitionList);

        @POST("aggregation")
        Call<FullAggregation> patchAggregation(@Body FullAggregation aggregation);
    }
}

package dunca.github.io.logpurchasemanager.service;

import dunca.github.io.logpurchasemanager.service.inferfaces.Service;
import io.github.dunca.logpurchasemanager.shared.model.custom.AcquisitionData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class AcquisitionDataService extends Service {
    private static AcquisitionDataService sInstance;
    private AcquisitionDataServiceImpl mService = RETROFIT.create(AcquisitionDataServiceImpl.class);

    private AcquisitionDataService() {

    }

    public synchronized static AcquisitionDataService getInstance() {
        if (sInstance == null) {
            sInstance = new AcquisitionDataService();
        }

        return sInstance;
    }

    public void postAcquisitionData(Callback<AcquisitionData> callback, AcquisitionData acquisitionData) {
        sendRequest(mService.postAcquisitionData(acquisitionData), callback);
    }

    interface AcquisitionDataServiceImpl {
        @POST("acquisition_data")
        Call<AcquisitionData> postAcquisitionData(@Body AcquisitionData acquisitionData);
    }
}

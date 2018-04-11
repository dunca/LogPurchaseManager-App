package dunca.github.io.logpurchasemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;

public class AcquisitionListActivity extends AppCompatActivity {
    private FloatingActionButton mFab;
    private List<Acquisition> mAcquisitionList;
    private RecyclerView mRvAcquisitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquisition_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setupOnClickActions();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        mFab = findViewById(R.id.fab);
        mRvAcquisitions = findViewById(R.id.rvAcquisitionList);
    }

    private void setupOnClickActions() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity(null);
            }
        });
    }

    private void startMainActivity(Acquisition acquisition) {
        Intent intent = new Intent(this, MainTabbedActivity.class);

        int id;

        if (acquisition != null) {
            id = acquisition.getId();
        } else {
            id = MethodParameterConstants.NO_ELEMENT_INDEX;
        }

        intent.putExtra(MainTabbedActivity.EXTRA_ACQUISITION_ID, id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        mAcquisitionList = DatabaseHelper.getLatestInstance().getAcquisitionDao().queryForAll();

        setupAcquisitionRecyclerView();

        super.onResume();
    }

    private void setupAcquisitionRecyclerView() {
        mRvAcquisitions.setLayoutManager(new LinearLayoutManager(this));
        mRvAcquisitions.setAdapter(new AcquisitionListRecyclerViewAdapter());
    }

    class AcquisitionListRecyclerViewAdapter extends RecyclerView.Adapter<AcquisitionItemViewHolder> {
        private final SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        @NonNull
        @Override
        public AcquisitionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(
                    R.layout.activity_acquisition_list_acquisition_list_item, parent, false);

            return new AcquisitionItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AcquisitionItemViewHolder holder, int position) {
            Acquisition acquisition = mAcquisitionList.get(position);

            holder.mTvSerialNumber.setText(acquisition.getSerialNumber());
            holder.mTvAcquirer.setText(acquisition.getAcquirer().getUsername());
            holder.mTvSupplier.setText(acquisition.getSupplier().getName());

            Date acquisitionDate = acquisition.getReceptionDate();
            holder.mTvAcquisitionDate.setText(isoDateFormatter.format(acquisitionDate));
        }

        @Override
        public int getItemCount() {
            return mAcquisitionList.size();
        }
    }

    class AcquisitionItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvSerialNumber;
        private TextView mTvAcquirer;
        private TextView mTvSupplier;
        private TextView mTvAcquisitionDate;

        AcquisitionItemViewHolder(View itemView) {
            super(itemView);

            mTvSerialNumber = itemView.findViewById(R.id.tvSerialNumber);
            mTvAcquirer = itemView.findViewById(R.id.tvAcquirer);
            mTvSupplier = itemView.findViewById(R.id.tvSupplierName);
            mTvAcquisitionDate = itemView.findViewById(R.id.tvAcquisitionDate);

            itemView.setOnClickListener(v -> {
                startMainActivity(mAcquisitionList.get(getAdapterPosition()));
            });
        }
    }
}

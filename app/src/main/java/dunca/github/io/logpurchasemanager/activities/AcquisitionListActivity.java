package dunca.github.io.logpurchasemanager.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;

public class AcquisitionListActivity extends AppCompatActivity {
    private static final DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private FloatingActionButton mFab;

    private List<Acquisition> mAcquisitionList;

    private RecyclerView mRvAcquisitions;
    private TextView mTvNoAcquisitions;

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
        mTvNoAcquisitions = findViewById(R.id.tvNoAcquisitions);
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
            id = MethodParameterConstants.INVALID_INDEX;
        }

        intent.putExtra(MainTabbedActivity.EXTRA_ACQUISITION_ID, id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAcquisitionList = DatabaseHelper.getLatestInstance().getAcquisitionDao().queryForAll();

        if (mAcquisitionList.isEmpty()) {
            mTvNoAcquisitions.setVisibility(View.VISIBLE);
            mRvAcquisitions.setVisibility(View.GONE);
        } else {
            mTvNoAcquisitions.setVisibility(View.GONE);
            mRvAcquisitions.setVisibility(View.VISIBLE);
            setupAcquisitionRecyclerView();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acquisition_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_filter_list) {
            openAcquisitionListFilteringDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAcquisitionListFilteringDialog() {
        AlertDialog.Builder listFilteringDialogBuilder = new AlertDialog.Builder(this);

        View listFilteringDialogView = getLayoutInflater().inflate(R.layout.activity_acquisition_list_filtering_dialog, null);

        TextView tvDate = listFilteringDialogView.findViewById(R.id.tvDate);
        EditText etFilterSerialNumber = listFilteringDialogView.findViewById(R.id.etFilterSerialNumber);
        EditText etFilterAcquisitor = listFilteringDialogView.findViewById(R.id.etFilterAcquisitor);
        EditText etFilterSupplier = listFilteringDialogView.findViewById(R.id.etFilterSupplier);
        CheckBox cbFilterByDate = listFilteringDialogView.findViewById(R.id.cbFilterByDate);

        updateDateInTextView(tvDate, new Date());

        tvDate.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener datePickListener = (v, year, month, dayOfMonth) -> {
                Date pickedDate = new Date(year - 1900, month, dayOfMonth);
                updateDateInTextView(tvDate, pickedDate);
                // TODO do something with the date
            };

            Calendar currentCalendar = Calendar.getInstance();

            new DatePickerDialog(AcquisitionListActivity.this, datePickListener, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH),
                    currentCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        listFilteringDialogBuilder.setView(listFilteringDialogView);
        listFilteringDialogBuilder.setTitle("Filter the acquisition list");

        listFilteringDialogBuilder.setPositiveButton("OK", (dialog, buttonId) -> {
            // TODO filter the list
        });

        listFilteringDialogBuilder.setNegativeButton("Cancel", (dialog, buttonId) -> {

        });

        listFilteringDialogBuilder.show();
    }

    private void updateDateInTextView(TextView textView, Date date) {
        textView.setText(ISO_DATE_FORMAT.format(date));
    }
}

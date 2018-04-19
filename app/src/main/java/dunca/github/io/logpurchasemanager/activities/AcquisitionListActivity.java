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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import io.github.dunca.logpurchasemanager.shared.model.Acquisition;
import io.github.dunca.logpurchasemanager.shared.model.constants.CommonFieldNames;

public class AcquisitionListActivity extends AppCompatActivity {
    private static final DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private FloatingActionButton mFab;

    private List<Acquisition> mOriginalAcquisitionList;
    private List<Acquisition> mAcquisitionList;

    private RecyclerView mRvAcquisitions;
    private TextView mTvNoAcquisitions;
    private AcquisitionListRecyclerViewAdapter mAdapter;

    private String mLastSerialNumberFilteringPart;
    private String mLastAcquirerUsernameFilteringPart;
    private String mLastSupplierNameFilteringPart;
    private String mLastFilteringDate;
    private boolean mLastFilterByDateCheckboxState;
    private boolean mFilteringCancelled;

    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquisition_list);

        mDbHelper = DatabaseHelper.getLatestInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setupOnClickActions();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        mFab = findViewById(R.id.fab);
        mRvAcquisitions = findViewById(R.id.rvAcquisitionList);
        mTvNoAcquisitions = findViewById(R.id.tvEmptyListPlaceholder);
    }

    private void setupOnClickActions() {
        mFab.setOnClickListener(view -> startMainActivity(null));
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

        mOriginalAcquisitionList =mDbHelper.getAcquisitionDao().queryForAll();
        mAcquisitionList = new ArrayList<>(mOriginalAcquisitionList);

        if (!mOriginalAcquisitionList.isEmpty()) {
            setupAcquisitionRecyclerView();
        }

        showListPlaceholderIfNecessary();
    }

    private void showListPlaceholderIfNecessary() {
        if (mAcquisitionList.isEmpty()) {
            mTvNoAcquisitions.setVisibility(View.VISIBLE);
            mRvAcquisitions.setVisibility(View.GONE);
        } else {
            mTvNoAcquisitions.setVisibility(View.GONE);
            mRvAcquisitions.setVisibility(View.VISIBLE);
        }
    }

    private void setupAcquisitionRecyclerView() {
        mRvAcquisitions.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AcquisitionListRecyclerViewAdapter();
        mRvAcquisitions.setAdapter(mAdapter);
    }

    class AcquisitionListRecyclerViewAdapter extends RecyclerView.Adapter<AcquisitionItemViewHolder> {
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

            if (!acquisitionHasItems(acquisition.getId())) {
                holder.mBtnPrint.setVisibility(View.GONE);
            }

            holder.mTvSerialNumber.setText(acquisition.getSerialNumber());
            holder.mTvAcquirer.setText(acquisition.getAcquirer().getUsername());
            holder.mTvSupplier.setText(acquisition.getSupplier().getName());

            Date acquisitionDate = acquisition.getReceptionDate();
            holder.mTvAcquisitionDate.setText(ISO_DATE_FORMAT.format(acquisitionDate));
        }

        @Override
        public int getItemCount() {
            return mAcquisitionList.size();
        }

        void useOriginalList() {
            mAcquisitionList = new ArrayList<>(mOriginalAcquisitionList);
            notifyDataSetChanged();
        }
    }

    class AcquisitionItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvSerialNumber;
        private TextView mTvAcquirer;
        private TextView mTvSupplier;
        private TextView mTvAcquisitionDate;
        private ImageButton mBtnPrint;

        AcquisitionItemViewHolder(View itemView) {
            super(itemView);

            mTvSerialNumber = itemView.findViewById(R.id.tvSerialNumber);
            mTvAcquirer = itemView.findViewById(R.id.tvAcquirer);
            mTvSupplier = itemView.findViewById(R.id.tvSupplierName);
            mTvAcquisitionDate = itemView.findViewById(R.id.tvAcquisitionDate);
            mBtnPrint = itemView.findViewById(R.id.btnPrint);

            itemView.setOnClickListener(v -> {
                startMainActivity(mOriginalAcquisitionList.get(getAdapterPosition()));
            });

            mBtnPrint.setOnClickListener(v -> startPrintingActivity());
        }

        private void startPrintingActivity() {
            Acquisition acquisition = mAcquisitionList.get(getAdapterPosition());

            Intent intent = new Intent(AcquisitionListActivity.this, PrintingActivity.class);
            intent.putExtra(PrintingActivity.EXTRA_ACQUISITION_ID, acquisition.getId());
            startActivity(intent);
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

        EditText etFilterAcquirerUsername = listFilteringDialogView.findViewById(R.id.etFilterAcquirerUsername);

        EditText etFilterSupplierName = listFilteringDialogView.findViewById(R.id.etFilterSupplierName);

        CheckBox cbFilterByDate = listFilteringDialogView.findViewById(R.id.cbFilterByDate);

        if (mFilteringCancelled || TextUtils.isEmpty(mLastFilteringDate)) {
            updateDateInTextView(tvDate, new Date());
        } else if (!mFilteringCancelled) {
            tvDate.setText(mLastFilteringDate);
        }

        if (!mFilteringCancelled) {
            etFilterSerialNumber.setText(mLastSerialNumberFilteringPart);
            etFilterAcquirerUsername.setText(mLastAcquirerUsernameFilteringPart);
            etFilterSupplierName.setText(mLastSupplierNameFilteringPart);
            cbFilterByDate.setChecked(mLastFilterByDateCheckboxState);
        }

        mFilteringCancelled = false;

        tvDate.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener datePickListener = (v, year, month, dayOfMonth) -> {
                Date pickedDate = new Date(year - 1900, month, dayOfMonth);
                updateDateInTextView(tvDate, pickedDate);
            };

            Calendar currentCalendar = Calendar.getInstance();

            new DatePickerDialog(AcquisitionListActivity.this, datePickListener, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH),
                    currentCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        listFilteringDialogBuilder.setView(listFilteringDialogView);
        listFilteringDialogBuilder.setTitle(R.string.activity_acquisition_filtering_dialog_title);

        listFilteringDialogBuilder.setPositiveButton(R.string.ok, (dialog, buttonId) -> {
            String serialNumber = etFilterSerialNumber.getText().toString();
            mLastSerialNumberFilteringPart = serialNumber;

            String acquirer = etFilterAcquirerUsername.getText().toString();
            mLastAcquirerUsernameFilteringPart = acquirer;

            String supplier = etFilterSupplierName.getText().toString();
            mLastSupplierNameFilteringPart = supplier;

            String date = tvDate.getText().toString();
            mLastFilteringDate = date;

            mLastFilterByDateCheckboxState = cbFilterByDate.isChecked();

            if (serialNumber.isEmpty() && acquirer.isEmpty() && supplier.isEmpty()
                    && !cbFilterByDate.isChecked()) {
                clearListFiltering();
                return;
            }

            List<Acquisition> filteredAcquisitionList;

            filteredAcquisitionList = filterBySerialNumber(serialNumber, mOriginalAcquisitionList);
            filteredAcquisitionList = filterByAcquirer(acquirer, filteredAcquisitionList);
            filteredAcquisitionList = filterBySupplier(supplier, filteredAcquisitionList);

            if (cbFilterByDate.isChecked()) {
                filteredAcquisitionList = filterByDate(date, filteredAcquisitionList);
            }

            mAcquisitionList.clear();
            mAcquisitionList.addAll(filteredAcquisitionList);

            mAdapter.notifyDataSetChanged();

            showListPlaceholderIfNecessary();
        });

        listFilteringDialogBuilder.setNegativeButton(R.string.cancel, (dialog, buttonId) -> {
            mFilteringCancelled = true;
            clearListFiltering();
        });

        listFilteringDialogBuilder.show();
    }

    private void clearListFiltering() {
        mAdapter.useOriginalList();
        showListPlaceholderIfNecessary();
    }

    private List<Acquisition> filterBySerialNumber(String serialNumberPart, List<Acquisition> sourceAcquisitionList) {
        List<Acquisition> filteredAcquisitionList = new ArrayList<>();

        for (Acquisition acquisition : sourceAcquisitionList) {
            if (acquisition.getSerialNumber().toLowerCase().startsWith(serialNumberPart)) {
                filteredAcquisitionList.add(acquisition);
            }
        }

        return filteredAcquisitionList;
    }

    private List<Acquisition> filterByAcquirer(String acquirerUsernamePart, List<Acquisition> sourceAcquisitionList) {
        List<Acquisition> filteredAcquisitionList = new ArrayList<>();

        for (Acquisition acquisition : sourceAcquisitionList) {
            if (acquisition.getAcquirer().getUsername().toLowerCase().startsWith(acquirerUsernamePart.toLowerCase())) {
                filteredAcquisitionList.add(acquisition);
            }
        }

        return filteredAcquisitionList;
    }

    private List<Acquisition> filterBySupplier(String supplierNamePart, List<Acquisition> sourceAcquisitionList) {
        List<Acquisition> filteredAcquisitionList = new ArrayList<>();

        for (Acquisition acquisition : sourceAcquisitionList) {
            if (acquisition.getSupplier().getName().toLowerCase().startsWith(supplierNamePart.toLowerCase())) {
                filteredAcquisitionList.add(acquisition);
            }
        }

        return filteredAcquisitionList;
    }

    private List<Acquisition> filterByDate(String exactDate, List<Acquisition> sourceAcquisitionList) {
        List<Acquisition> filteredAcquisitionList = new ArrayList<>();

        for (Acquisition acquisition : sourceAcquisitionList) {
            String receptionDate = ISO_DATE_FORMAT.format(acquisition.getReceptionDate());
            if (receptionDate.equals(exactDate)) {
                filteredAcquisitionList.add(acquisition);
            }
        }

        return filteredAcquisitionList;
    }

    private void updateDateInTextView(TextView textView, Date date) {
        textView.setText(ISO_DATE_FORMAT.format(date));
    }

    private boolean acquisitionHasItems(int acquisitionId) {
        try {
            return mDbHelper.getAcquisitionItemDao().queryBuilder().where()
                    .eq(CommonFieldNames.ACQUISITION_ID, acquisitionId)
                    .countOf() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

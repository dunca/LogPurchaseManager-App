package dunca.github.io.logpurchasemanager.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dunca.github.io.logpurchasemanager.R;

public class PrintingActivity extends AppCompatActivity {
    private final String companyInfo = "J.F Furnir S.R.L." +
            "\nRO-500053 Brașov, Șos. Cristianului 46" +
            "\nTel.: +40 (268) 406 700   Fax: +40 (268) 406 745\n\n";

    private final String valueTypeTemplete = "Listă la %s\n";
    private final String info1Template = "Nr. Recepție: %s\n";
    private final String dateTemplate = "Dată: %s\n\n";

    private final String supplierInfoTemplate = "Vânzător: %s" +
            "\nDenumire: %s" +
            "\nStradă: %s" +
            "\nCod poștal, Localitate: %s\n\n";

    private final String observationsTemplate = "Observații: %s\n\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
    }


}

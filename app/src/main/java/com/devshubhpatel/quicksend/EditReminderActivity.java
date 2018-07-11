package com.devshubhpatel.quicksend;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.blackbox_vision.datetimepickeredittext.view.DatePickerInputEditText;
import io.blackbox_vision.datetimepickeredittext.view.TimePickerInputEditText;

import static com.devshubhpatel.quicksend.InitClass.getUniqueId;
import static com.devshubhpatel.quicksend.MainActivity.realm;

public class EditReminderActivity extends AppCompatActivity {

    String TAG = "EditReminderActivity";
    Reminder reminder = null;
    Calendar calNow,calSelect;
    private static final int RESULT_PICK_CONTACT = 8550;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.ccp)
    CountryCodePicker countryCodePicker;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.datePickerInputEditText)
    DatePickerInputEditText etDate;
    @BindView(R.id.timePickerInputEditText)
    TimePickerInputEditText etTime;
    @BindView(R.id.card_group_choices)
    SingleSelectToggleGroup sstg;
    @BindView(R.id.btn_delete)
    Button btnDelete;

    PhoneNumberUtil phoneUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);
        ButterKnife.bind(this);
        phoneUtil = PhoneNumberUtil.getInstance();
        countryCodePicker.registerCarrierNumberEditText(etMobile);
        //etMobile.addTextChangedListener(twMob);
        etDate.setManager(getSupportFragmentManager());
        etTime.setManager(getSupportFragmentManager());

       /* TextWatcher twDateTime = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etDate.setError(null);
                etTime.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        etTime.addTextChangedListener(twDateTime);
        etDate.addTextChangedListener(twDateTime); */

        calNow = Calendar.getInstance();

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(calNow.getTime());

        etDate.setMinDate(formattedDate);


        long tmp = getIntent().getLongExtra("Reminder_ID", -1);


        if (tmp == -1) {
            reminder = new Reminder();
            reminder.set_id(getUniqueId(realm, Reminder.class));
            btnDelete.setVisibility(View.GONE);
        } else {
            reminder = realm.where(Reminder.class).equalTo("_id", tmp).findFirst();

            etTitle.setText(reminder.getrTitle());
            calSelect = Calendar.getInstance();
            calSelect.setTimeInMillis(reminder.getrTime());

            etDate.setDate(calSelect); etTime.setTime(calSelect);

            etDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(calSelect.getTime()));
            etTime.setText(new SimpleDateFormat("hh:mm a").format(calSelect.getTime()));

            countryCodePicker.setCountryForNameCode(reminder.getrCountry());
            etMobile.setText(reminder.getrMobile());

            etMessage.setText(reminder.getrMessage());

            sstg.check(reminder.getrType());

            btnDelete.setVisibility(View.VISIBLE);

        }

    }

    @OnClick(R.id.btn_delete)
    void onDelete() {
        new AlertDialog.Builder(this).setMessage(
                "Are you sure you want to delete this reminder ?")
                .setCancelable(false)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                realm.beginTransaction();
                                reminder.deleteFromRealm();
                                realm.commitTransaction();
                                Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                //dialog.cancel();
                            }
                        }).create().show();

    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        EditReminderActivity.this.finish();
    }

    @OnClick(R.id.btn_save)
    void onSave() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Enter Title");
            return;
        }
        if (etTitle.getText().length() > 40) {
            etTitle.setError("Invalid Title (Maximum 40 characters");
            return;
        }


        if (isValidMobile() && isValidDateTime()) {
            //TODO: save reminder
            try {

                realm.beginTransaction();
                reminder.setrTitle(etTitle.getText().toString());
                reminder.setrType(sstg.getCheckedId());

                reminder.setrMessage(etMessage.getText().toString());
                reminder.setrMobile(etMobile.getText().toString());
                reminder.setrCountry(countryCodePicker.getDefaultCountryName());
                reminder.setrTime(calSelect.getTimeInMillis());

                realm.copyToRealmOrUpdate(reminder);
                realm.commitTransaction();

                Toast.makeText(getApplicationContext(), "Saved !", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e){
                Log.e(TAG,"Error: "+e.getMessage());
                Snackbar.make(btnDelete,"Something went wrong !", BaseTransientBottomBar.LENGTH_LONG).show();
            }

        }
    }

    @OnClick(R.id.btn_select_contact)
    void selectContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();


            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = cursor.getString(phoneIndex).replace(" ", "").replace("(", "").replace(")", "").replace("-", "");


            Phonenumber.PhoneNumber numberProto = null;
            try {
                numberProto = phoneUtil.parse(phoneNo, "");
            } catch (NumberParseException e) {
                etMobile.setError("Invalid phone number.");
            }
            if (!phoneUtil.isValidNumber(numberProto)) {
                etMobile.setError("Invalid phone number.");
            }
            try {
                countryCodePicker.setCountryForPhoneCode(numberProto.getCountryCode());
                etMobile.setText("" + numberProto.getNationalNumber());
            } catch (NullPointerException npe) {
                countryCodePicker.setCountryForPhoneCode(91);
                etMobile.setError("Error !");
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
        }
    }

    private boolean isValidMobile() {
        String phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus() + etMobile.getText().toString();
        Log.i(TAG, phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) {
            etMobile.setError("Invalid phone number.");
            return false;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;
        try {
            numberProto = phoneUtil.parse(phoneNumber, "");

        } catch (NumberParseException e) {
            etMobile.setError("Invalid phone number.");
            return false;
        }
        if (!phoneUtil.isValidNumber(numberProto)) {
            etMobile.setError("Invalid phone number.");
        }
        return phoneUtil.isValidNumber(numberProto);
    }

    private boolean isValidDateTime(){
        if(etDate.getText().toString().isEmpty()){
            etDate.setError("Set Date");
            return false;
        }
        if(etTime.getText().toString().isEmpty()){
            etTime.setError("Set Time");
            return false;
        }

        calSelect = etDate.getDate();
        calSelect.setTimeInMillis(etTime.getTime().getTimeInMillis());

        Log.i(TAG, calNow.getTimeInMillis()+"   "+calSelect.getTimeInMillis());
        if(calSelect.before(calNow)){
            etTime.setError("Selected time is before current time");
            return false;
        }
        return true;
    }


    /*final TextWatcher twMob = new TextWatcher() {

        private static final int TOTAL_SYMBOLS = 11; // size of pattern 00000 00000
        private static final int TOTAL_DIGITS = 10; // max numbers of digits in pattern: 00000 x 2
        private static final int DIVIDER_MODULO = 6; // means divider position is every 5th symbol beginning with 1
        private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
        private static final char DIVIDER = ' ';

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                s.replace(0, s.length(), buildCorrecntString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
            }
        }

        private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
            boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
            for (int i = 0; i < s.length(); i++) { // chech that every element is right
                if (i > 0 && (i + 1) % dividerModulo == 0) {
                    isCorrect &= divider == s.charAt(i);
                } else {
                    isCorrect &= Character.isDigit(s.charAt(i));
                }
            }
            return isCorrect;
        }

        private String buildCorrecntString(char[] digits, int dividerPosition, char divider) {
            final StringBuilder formatted = new StringBuilder();

            for (int i = 0; i < digits.length; i++) {
                if (digits[i] != 0) {
                    formatted.append(digits[i]);
                    if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                        formatted.append(divider);
                    }
                }
            }

            return formatted.toString();
        }

        private char[] getDigitArray(final Editable s, final int size) {
            char[] digits = new char[size];
            int index = 0;
            for (int i = 0; i < s.length() && index < size; i++) {
                char current = s.charAt(i);
                if (Character.isDigit(current)) {
                    digits[index] = current;
                    index++;
                }
            }
            return digits;
        }
    }; */


}

package com.devshubhpatel.quicksend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by patel on 08-07-2017.
 */

public class SendFragment extends Fragment {

    View rootview;
    String TAG = "SendFragment";

    @BindView(R.id.ccp)
    CountryCodePicker countryCodePicker;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_send)
    Button btnSend;

    public SendFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_send, container, false);
        ButterKnife.bind(this, rootview);
        countryCodePicker.registerCarrierNumberEditText(etMobile);
     /*   final TextWatcher twMob = new TextWatcher() {

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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidMobile()) {
                    final String mobileNum = countryCodePicker.getSelectedCountryCode() + etMobile.getText().toString().replace(" ", "");
                    sendWhatsappMessage(etMessage.getText().toString(), mobileNum);
                }
            }
        });
        return rootview;
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

    private void sendWhatsappMessage(String message, String number) {
        Log.i(TAG, "Number : " + number);
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.putExtra("jid", number + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);

        } catch (PackageManager.NameNotFoundException e) {
            new AlertDialog.Builder(getActivity()).setMessage(
                    "Whatsapp not installed on this device.")
                    .setCancelable(false)
                    .setPositiveButton("Install",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp")));
                                    } catch (Exception e) {
                                        Log.e(TAG, "OpenPlaystore:" + e.getMessage());
                                        Snackbar.make(btnSend, "Google Playstore Error", Snackbar.LENGTH_SHORT).show();
                                    }

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
    }


}

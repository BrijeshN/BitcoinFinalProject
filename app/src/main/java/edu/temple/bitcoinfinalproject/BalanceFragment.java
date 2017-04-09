package edu.temple.bitcoinfinalproject;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class BalanceFragment extends Fragment {


    // View v
    View v;

    // Balance Interface
    BalanceInterface activity;

    // Json aray to store data
    JSONArray data;

    // Text view to display text
    TextView display;

    // Initial balance set to -1
    double balance = -1;

    // Api link provide by professor
    String balanceAPI = "http://btc.blockr.io/api/v1/address/balance/";

    // Block address from the api
    String blockAddress = "1F1tAaz5x1HUXrCNLbtMDqcw6o5GNn4xqX";



    // Edit text to get address from the user
    EditText getAddress;



    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (BalanceInterface) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public BalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_balance, container, false);

        Button button = (Button) v.findViewById(R.id.goButton);

        Resources res = getResources();
        String jsonString = res.getString(R.string.json);
        try {
            data = new JSONObject(jsonString).getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getAddress = (EditText) v.findViewById(R.id.bcAddress);
        display = (TextView) v.findViewById(R.id.balance);
        getAddress.setText(blockAddress);

        if (balance != -1) {
            display.setText(String.valueOf(balance));
        }

        getAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                blockAddress = s.toString();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockAddress = getAddress.getText().toString();

                try {
                    if (data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            if (data.getJSONObject(i).getString("bcAddress").equals(blockAddress)) {
                                balance = data.getJSONObject(i).getDouble("balance");
                                display.setText(String.valueOf(balance));
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ApiUrl apiUrl = new ApiUrl(balanceAPI + blockAddress);
                apiUrl.execute();

                try {
                    JSONObject json =  new JSONObject(apiUrl.get());
                }  catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    balance = new JSONObject(apiUrl.get()).getJSONObject("data")
                            .getDouble("balance");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                display.setText(String.valueOf(balance));

            }
        });

        Button scan = (Button) v.findViewById(R.id.scanQRCode);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.scanQR();
            }
        });

        return v;
    }

    public void setBlockAddress(String blockAddress){
        this.blockAddress = blockAddress;
    }

    public interface BalanceInterface {
        void scanQR();
    }



}

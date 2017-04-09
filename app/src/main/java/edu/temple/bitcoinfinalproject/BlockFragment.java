package edu.temple.bitcoinfinalproject;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */

public class BlockFragment extends Fragment implements Parcelable {

    // Block interface called activity
    BlockInterface activity;

    // Api link address to get blocks
    String apiBlockAddress = "http://btc.blockr.io/api/v1/block/info/";

    // Setting random block number
    String apiBlocknumber = "12345";

    // Text views for hash, vout_sum, confirmations from the api
    TextView hash, vout_sum, size, confirmations;

    // Edit text to get block number from the user
    EditText userBlocknumber;

    // Strings to store hash, voutSum, size and confirmations
    String storeHash, storeVoutSum, storeSize, storeConfirm;




    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (BlockInterface) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public BlockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_block, container, false);

        userBlocknumber = (EditText) v.findViewById(R.id.block_number);
        hash = (TextView) v.findViewById(R.id.hash_text);
        vout_sum = (TextView) v.findViewById(R.id.vout_sum_text);
        size = (TextView) v.findViewById(R.id.size_text);
        confirmations = (TextView) v.findViewById(R.id.confirmations_text);

        userBlocknumber.setText(apiBlocknumber);

        Bundle args = getArguments();
        if (args != null) {
            String newBlockNum = args.getString("apiBlocknumber");
            if (newBlockNum.length() != 0) {
                apiBlocknumber = newBlockNum;
                userBlocknumber.setText(apiBlocknumber);
                display(v);
            }
        }

        hash.setText(storeHash);
        size.setText(storeSize);
        confirmations.setText(storeConfirm);
        vout_sum.setText(storeVoutSum);

        userBlocknumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                apiBlocknumber = s.toString();
            }
        });

        Button button = (Button) v.findViewById(R.id.goButton);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                display(v);

            }
        });

        Button previous = (Button) v.findViewById(R.id.previous);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiUrl apiUrl = new ApiUrl(apiBlockAddress + String.valueOf(Integer.parseInt(apiBlocknumber) - 1));
                apiUrl.execute();

                try {
                    JSONObject json = new JSONObject(apiUrl.get());
                    activity.previous_block(Integer.parseInt(apiBlocknumber) - 1);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "No block found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button next = (Button) v.findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiUrl apiUrl = new ApiUrl(apiBlockAddress + String.valueOf(Integer.parseInt(apiBlocknumber) + 1));
                apiUrl.execute();

                try {
                    JSONObject json = new JSONObject(apiUrl.get());
                    activity.next_block(Integer.parseInt(apiBlocknumber) + 1);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "No block found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public interface BlockInterface {
        void previous_block(int blockNum);
        void next_block(int blockNum);
    }

    public void display(View v) {

        apiBlocknumber = userBlocknumber.getText().toString();

        ApiUrl apiUrl = new ApiUrl(apiBlockAddress + apiBlocknumber);
        apiUrl.execute();

        try {
            JSONObject json = new JSONObject(apiUrl.get());
        } catch (Exception e) {
            Toast.makeText(v.getContext(), "No block found", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject json = new JSONObject(apiUrl.get()).getJSONObject("data");
            storeHash = json.getString("hash");
            storeSize = json.getString("size");
            storeConfirm = json.getString("confirmations");
            storeVoutSum = json.getString("vout_sum");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        hash.setText(storeHash);
        size.setText(storeSize);
        confirmations.setText(storeConfirm);
        vout_sum.setText(storeVoutSum);

    }

}

package edu.temple.bitcoinfinalproject;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PriceFragment extends Fragment {


    public PriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Holds api address to get the current bit coin price
        String coinPriceAPI = "http://btc.blockr.io/api/v1/coin/info";

        // JSON object called coinPrice
        JSONObject coinPrice;

        // USING ApiURL class to store apiURL address
        ApiUrl apiUrl = new ApiUrl(coinPriceAPI);

        // Execute apiURL
        apiUrl.execute();

        View v = inflater.inflate(R.layout.fragment_price, container, false);


        try {

            // Parsing json
            coinPrice = new JSONObject(apiUrl.get()).getJSONObject("data")
                    .getJSONObject("markets").getJSONObject("coinbase");
            TextView coinbase_price_view = (TextView) v.findViewById(R.id.coinbase_price_view);
            coinbase_price_view.setText(String.valueOf(coinPrice.getDouble("value")));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return v;
    }

}

package edu.temple.bitcoinfinalproject;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment implements View.OnClickListener, DrawImage.Listener {

    // Chartinterface names activity
    ChartInterface activity;

    //  Storing yahoo graph api link
    String graphApiURL = "https://chart.yahoo.com/z?s=BTCUSD=X&t=";

    // String to store url
    public String url;


    // Declaring image view named graphImage
    private ImageView graphImage;

    // Declaing goButton named loadGraph
    private Button loadGraph;

    // Edittext named getDayFromUser
    public EditText getDayFromUser;

    // String called text to hold days
    public String text = "";

    // boolean called tap
    public boolean tap = false;




    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (ChartInterface) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        graphImage = (ImageView) v.findViewById(R.id.image);
        loadGraph = (Button) v.findViewById(R.id.btn_load_image);
        loadGraph.setOnClickListener(this);
        getDayFromUser = (EditText) v.findViewById(R.id.editText);
        getDayFromUser.setText(text);
         if (url == null)
            url = graphApiURL + getDayFromUser.getText().toString();

        return v;
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        graphImage.setImageBitmap(bitmap);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_image:
                tap = true;
                url = graphApiURL + getDayFromUser.getText().toString();
                activity.startService();
                new DrawImage(this).execute(url);
                break;
        }
    }

    public void updateChart() {
        new DrawImage(this).execute(url);
    }

    public interface ChartInterface {
        public void startService();
    }

}

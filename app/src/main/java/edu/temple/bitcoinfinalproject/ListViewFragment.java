package edu.temple.bitcoinfinalproject;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {


    // Interface for listViewFragment called activity
    ListInterface activity;


    public ListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (ListInterface) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_listview, container, false);
        ListView listView = (ListView) v.findViewById(R.id.listView);

        Resources res = getResources();
        String[] menus = res.getStringArray(R.array.menu_array);

        ArrayAdapter arrayAdapter = new ArrayAdapter(v.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                menus);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.loadList(position);
            }
        });


        return v;
    }

    public interface ListInterface {
        public void loadList(int position);
    }

}

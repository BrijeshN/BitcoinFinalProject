package edu.temple.bitcoinfinalproject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ListViewAdapter extends FragmentStatePagerAdapter {

    ArrayList<BlockFragment> listOfMenu;

    public ListViewAdapter(FragmentManager fm, ArrayList<BlockFragment> listOfMenu) {
        super(fm);
        this.listOfMenu = listOfMenu;
    }

    @Override
    public BlockFragment getItem(int i) {
        BlockFragment fragment = listOfMenu.get(i);

        return fragment;
    }

    @Override
    public int getCount() {
        return listOfMenu.size();
    }

}

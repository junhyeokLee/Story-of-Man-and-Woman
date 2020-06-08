package com.dev_sheep.story_of_man_and_woman.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dev_sheep.story_of_man_and_woman.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Use the {@link Home_Rank_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home_Rank_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;


    public Home_Rank_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_Rank_Fragment newInstance(int param1) {
        Home_Rank_Fragment fragment = new Home_Rank_Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.adapter_rank, container, false);
        TextView tv = (TextView) v.findViewById(R.id.tv_number);
        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.imageView);
        TextView name = (TextView) v.findViewById(R.id.m_id);
        name.setText("dqwdqd");


        tv.setText(mParam1 + "");

        return v;

    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


}

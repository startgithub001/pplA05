package ppl.handyman.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.util.TreeSet;

import ppl.handyman.R;
import ppl.handyman.handler.SQLiteHandler;
import ppl.handyman.handler.SessionHandler;
import ppl.handyman.activity.OrderActivity;
import ppl.handyman.adapter.ImageAdapter;

/**
 * Created by Ari on 4/12/2016.
 */
public class SearchFragment extends Fragment {
    SQLiteHandler sql;
    private Button search;
    private GridView gridview;
    private TreeSet<String> pickedCategory;
    private SessionHandler session;

    public SearchFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getContext());
        sql = new SQLiteHandler(getContext());
        session = new SessionHandler(getContext());
        pickedCategory = new TreeSet<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /**
         * TO DO:
         * WE NEED TO REFACTOR THIS CODE!!!
         */
        View view = inflater.inflate(R.layout.fragment_newsearch, container, false);
        gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(view.getContext()));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        String orderan[] = {"ac","air","bengkel","besi","cat","kayu","listrik","taman","tembok"};

            @Override
            public void onItemClick(AdapterView<?> parent, View imgView, int position, long id) {
                pickedCategory.add(orderan[position]);
                session.setPickedCategory(pickedCategory);
                Intent intent = new Intent(getContext(), OrderActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
;
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        pickedCategory.clear();
    }
}

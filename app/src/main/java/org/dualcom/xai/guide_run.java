package org.dualcom.xai;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Виталий on 01.02.2015.
 */
public class guide_run extends Fragment {

    Context context;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.guide_run, container, false);

        context = container.getContext();

        return view;
    }
}

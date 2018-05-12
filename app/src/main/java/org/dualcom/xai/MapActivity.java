package org.dualcom.xai;

import android.os.Bundle;

import android.support.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;

public class MapActivity extends com.flipboard.bottomsheet.commons.BottomSheetFragment {

    private View rootView;
    public android.content.Context context;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_map, container, false);
        context = rootView.getContext();

        PhotoView photoView = (PhotoView) rootView.findViewById(R.id.khai_map);
        photoView.setImageResource(R.drawable.khai_map);

        return rootView;
    }

}

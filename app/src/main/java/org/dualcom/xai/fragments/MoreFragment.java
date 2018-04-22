package org.dualcom.xai.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;
import com.google.android.gms.maps.MapView;

import org.dualcom.xai.MyClass.LIST;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;
import org.dualcom.xai.R;

import java.util.Objects;

public class MoreFragment extends BottomSheetFragment {

    public View rootView;
    public Context context;

    public String _lesson, _lesson_tmp, _auditory, _teacher, _spliter;

    public MapFragment mapFragment;
    public FragmentTransaction transaction;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (rootView==null) ? inflater.inflate(R.layout.fragment_more, container, false) : rootView;
        context = rootView.getContext();

        _lesson = Storage.loadData(context, "_tmp_lesson");
        _spliter = LIST.getHousingStr(_lesson.split("/")[_lesson.split("/").length-1]);
        _lesson_tmp = _lesson.split(_spliter)[_lesson.split(_spliter).length-1];

        _auditory = "";
        for(int i = 0; i < (_lesson.split(_spliter).length-1); i++)
            _auditory += _lesson.split(_spliter)[i] + _spliter;

        Storage.saveData(context, "_tmp_auditory", _auditory);

        _lesson = (_lesson_tmp.length()>1) ? _lesson_tmp : _lesson;

        _teacher = Storage.getWithRemoveData(context, "_tmp_teacher");

        TextView nameLesson = rootView.findViewById(R.id.nameLesson);
        nameLesson.setText(_lesson);

        TextView nameTeacher = rootView.findViewById(R.id.nameTeacher);
        nameTeacher.setText(_teacher);

        TextView labelHousing = rootView.findViewById(R.id.labelHousing);
        labelHousing.setText(context.getResources().getStringArray(R.array.housings)[LIST.getHousing(Storage.loadData(context, "_tmp_lesson"))]);

        mapFragment = new MapFragment();

        transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mapView, mapFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MapFragment.MY_PERMISSIONS_REQUEST_LOCATION){
            mapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }

}

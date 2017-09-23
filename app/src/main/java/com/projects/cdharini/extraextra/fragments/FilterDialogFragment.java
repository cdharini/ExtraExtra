package com.projects.cdharini.extraextra.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.projects.cdharini.extraextra.R;
import com.projects.cdharini.extraextra.utils.ExtraExtraConstants;


public class FilterDialogFragment extends DialogFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    /* TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    TextView tvBeginDateSelected;
    Spinner spSortOrder;
    CheckBox cbNewsDesk1;
    CheckBox cbNewsDesk2;
    CheckBox cbNewsDesk3;
    Button btnSave;


    public FilterDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterDialogFragment newInstance(String param1, String param2) {
        FilterDialogFragment fragment = new FilterDialogFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            //  mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get views
        spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);
        cbNewsDesk1 = (CheckBox) view.findViewById(R.id.cbNewsdesk1);
        cbNewsDesk2 = (CheckBox) view.findViewById(R.id.cbNewsdesk2);
        cbNewsDesk3 = (CheckBox) view.findViewById(R.id.cbNewsdesk3);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        tvBeginDateSelected = (TextView) view.findViewById(R.id.tvBeginDateSelected);

        // Populate views
        tvBeginDateSelected.setText(ExtraExtraConstants.BEGIN_DATE_DEFAULT);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sortorder_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortOrder.setAdapter(adapter);
        loadSelections();

        // On click listeners
        btnSave.setOnClickListener(this);
        tvBeginDateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerDialogFragment();
                // SETS the target fragment for use later when sending results
                newFragment.setTargetFragment(FilterDialogFragment.this, 300);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

    }

    /*
     * Click listener for when Filter Pref Save button is clicked
     */
    @Override
    public void onClick(View v) {
        FilterDialogFragmentListener listener = (FilterDialogFragmentListener) getActivity();

        // Save shared prefs
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = pref.edit();
        String begindate = (String) tvBeginDateSelected.getText();
        edit.putString(ExtraExtraConstants.BEGIN_DATE_PREF, begindate);
        edit.putString(ExtraExtraConstants.SORT_ORDER_PREF, spSortOrder.getSelectedItem().toString().toLowerCase());
        edit.putString(ExtraExtraConstants.NEWS_DESK_PREF, createNewsDeskPref());
        edit.commit();

        // Call listener
        listener.onFinishFilterDialog();
        dismiss();
    }

    public interface FilterDialogFragmentListener {
        void onFinishFilterDialog();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        tvBeginDateSelected.setText(String.format("%04d-%02d-%02d", year, month, dayOfMonth));
    }

    private void loadSelections(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        tvBeginDateSelected.setText(preferences.getString(ExtraExtraConstants.BEGIN_DATE_PREF, ExtraExtraConstants.BEGIN_DATE_DEFAULT));
        setSpinnerToValue(spSortOrder, preferences.getString(ExtraExtraConstants.SORT_ORDER_PREF, "oldest"));
        populateCheckboxes(preferences.getString(ExtraExtraConstants.NEWS_DESK_PREF, ""));

    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().toLowerCase().equals(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }

    private String createNewsDeskPref() {
        String pref = "";
        StringBuilder builder = new StringBuilder();
        builder.append(cbNewsDesk1.isChecked()?("\""+cbNewsDesk1.getText()+"\""): "");
        builder.append(cbNewsDesk2.isChecked()?(" \""+cbNewsDesk2.getText()+"\""): "");
        builder.append(cbNewsDesk3.isChecked()?(" \""+cbNewsDesk3.getText()+"\""): "");
        pref = builder.toString();
        return pref;
    }

    private void populateCheckboxes(String newsDeskPref) {
        cbNewsDesk1.setChecked(newsDeskPref.contains(cbNewsDesk1.getText()));
        cbNewsDesk2.setChecked(newsDeskPref.contains(cbNewsDesk2.getText()));
        cbNewsDesk3.setChecked(newsDeskPref.contains(cbNewsDesk3.getText()));
    }
}
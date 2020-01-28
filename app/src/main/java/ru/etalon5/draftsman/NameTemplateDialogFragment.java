package ru.etalon5.draftsman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NameTemplateDialogFragment extends DialogFragment {

    private DialogCommunicator mCommunicator;


    public NameTemplateDialogFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        if (activity instanceof DialogCommunicator) {
            mCommunicator = (DialogCommunicator) getActivity();
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet NameTemplateDialogFragment.communicator");
        }
    }

    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.storedTemplate));
        View v = inflater.inflate(R.layout.fragment_name_template, null);

        Button btnOk = (Button) v.findViewById(R.id.buttonNameTemplatesOk);
        final EditText editTextName = (EditText) v.findViewById(R.id.editTextNameTemplate);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mCommunicator.dialogFinish();
                ((DraftActivity) getActivity()).saveTemplate(editTextName.getText().toString().trim());
            }
        });

        return v;
    }

    public interface DialogCommunicator {
        public void dialogFinish();
    }
}
